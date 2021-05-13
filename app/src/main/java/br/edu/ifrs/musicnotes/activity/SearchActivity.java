package br.edu.ifrs.musicnotes.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.adapter.AlbumAdapter;
import br.edu.ifrs.musicnotes.helper.Helper;
import br.edu.ifrs.musicnotes.listener.RecyclerItemClickListener;
import br.edu.ifrs.musicnotes.model.Album;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, Helper {

    private static final String ENDPOINT = "https://api.spotify.com/v1/search?";
    private static final int ALBUM_ACTIVITY_REQUEST_CODE = 0;
    private static final int TOKEN_ACTIVITY_REQUEST_CODE = 1;
    private static final int LIMIT = 10;
    private static int mOffset = 0;
    private RecyclerView mRecyclerAlbums;
    private SharedPreferences mSharedPreferences;
    private List<Album> mAlbumList;
    private ShimmerFrameLayout mShimmerContainer;
    private String mQuery;
    private AlbumAdapter mAlbumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mShimmerContainer = findViewById(R.id.shimmerContainer);
        mRecyclerAlbums = findViewById(R.id.recyclerAlbums);

        displayShimmer(false);

        SearchView searchView = findViewById(R.id.searchAlbum);
        searchView.setOnQueryTextListener(this);

        mSharedPreferences = getSharedPreferences("spotifyAuth", MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOutIcon) {
            FirebaseAuth.getInstance().signOut();
            mSharedPreferences.edit().clear().apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mQuery = query;
        mAlbumList = new ArrayList<>();
        mOffset = 0;

        displayShimmer(true);

        Intent intent = new Intent(this, SpotifyTokenActivity.class);
        startActivityForResult(intent, TOKEN_ACTIVITY_REQUEST_CODE);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ALBUM_ACTIVITY_REQUEST_CODE:
                    Snackbar.make(getWindow().getDecorView().getRootView(),
                            "Sua resenha foi atualizada",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    break;
                case TOKEN_ACTIVITY_REQUEST_CODE:
                    getAlbums();
                    break;
            }
        }
    }

    /*
    Retrieves albums from Spotify API.
     */
    private void getAlbums() {
        String token = mSharedPreferences.getString("accessToken", "");

        Request request = new Request.Builder()
                .url(ENDPOINT + "q=" + mQuery + "&type=album&limit=" + LIMIT + "&offset=" + mOffset)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()) {
                    mOffset += LIMIT;
                    setAlbumListView(response);
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    /*
    Sets album list from Spotify API response.
     */
    private void setAlbumListView(Response response) {
        try {
            JSONObject res = new JSONObject(Objects.requireNonNull(response.body()).string());
            JSONObject albums = new JSONObject(res.getString("albums"));
            JSONArray items = new JSONArray(albums.getString("items"));

            for (int item = 0; item < items.length(); item++) {
                JSONObject jsonAlbum = items.getJSONObject(item);
                Album album = setAlbumObject(jsonAlbum);
                mAlbumList.add(album);
            }

            runOnUiThread(this::setAlbumAdapter);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Sets adapter for recycler view, including list scroll and item click settings.
     */
    private void setAlbumAdapter() {
        // first search for a given query
        if (mOffset == LIMIT) {
            mAlbumAdapter = new AlbumAdapter(getApplicationContext(), mAlbumList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerAlbums.setLayoutManager(layoutManager);
            mRecyclerAlbums.setHasFixedSize(true);
            mRecyclerAlbums.setAdapter(mAlbumAdapter);

            displayShimmer(false);

            mRecyclerAlbums.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        getAlbums();
                    }
                }
            });

            mRecyclerAlbums.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            mRecyclerAlbums,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                }

                                @Override
                                public void onItemClick(View view, int position) {
                                    Album album = mAlbumList.get(position);
                                    Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
                                    intent.putExtra("album", album);
                                    startActivityForResult(intent, ALBUM_ACTIVITY_REQUEST_CODE);
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }
                            }
                    )
            );
            // subsequent searches
        } else {
            mAlbumAdapter.notifyDataSetChanged();
        }
    }

    /*
    Creates album object from Spotify API with id, title, artists, year and images.
     */
    private Album setAlbumObject(JSONObject item) {
        Album album = new Album();

        try {
            String albumId = item.get("id").toString();
            String albumName = item.get("name").toString();

            JSONArray artists = (JSONArray) item.get("artists");
            List<String> artistList = new ArrayList<>();

            for (int artist = 0; artist < artists.length(); artist++) {
                String artistName = artists.getJSONObject(artist).get("name").toString();
                artistList.add(artistName);
            }

            JSONArray images = new JSONArray(item.getString("images"));
            String albumCoverSmall = images.getJSONObject(2).get("url").toString(); // 64x64px
            String albumCoverMedium = images.getJSONObject(1).get("url").toString(); // 300x300px
            Map<String, String> albumCover = new HashMap<>();
            albumCover.put("small", albumCoverSmall);
            albumCover.put("medium", albumCoverMedium);

            String releaseDate = item.get("release_date").toString();
            int albumYear = Integer.parseInt(releaseDate.substring(0, 4));

            album = new Album(albumId, albumName, artistList, albumCover, albumYear);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return album;
    }

    @Override
    public void displayShimmer(boolean display) {
        if (display) {
            mRecyclerAlbums.setVisibility(View.GONE);
            mShimmerContainer.setVisibility(View.VISIBLE);
        } else {
            mShimmerContainer.setVisibility(View.GONE);
            mRecyclerAlbums.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAlbumAdapter != null) {
            mAlbumAdapter.notifyDataSetChanged();
        }
    }
}