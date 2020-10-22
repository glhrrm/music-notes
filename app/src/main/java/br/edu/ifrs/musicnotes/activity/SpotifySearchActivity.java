package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.listener.RecyclerItemClickListener;
import br.edu.ifrs.musicnotes.adapter.RecyclerAdapter;
import br.edu.ifrs.musicnotes.model.Album;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifySearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String ENDPOINT = "https://api.spotify.com/v1/search?";
    private RecyclerView mRecyclerAlbums;
    private SharedPreferences mSharedPreferences;
    private View mLoadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_search);

        mLoadingFragment = findViewById(R.id.loadingFragment);
        mLoadingFragment.setVisibility(View.GONE);

        SearchView searchView = findViewById(R.id.searchAlbum);
        searchView.setOnQueryTextListener(this);

        mSharedPreferences = getSharedPreferences("spotify", MODE_PRIVATE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mLoadingFragment.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();

        String token = mSharedPreferences.getString("token", "");
        Log.d("token sharedPreferences", "meu token: " + token);

        Request request = new Request.Builder()
                .url(ENDPOINT + "q=" + query + "&type=album&limit=15")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingFragment.setVisibility(View.GONE);
                        }
                    });
                    mountAlbumListView(response);
                } else {
                    if (response.code() == 401 || response.code() == 400) {
                        Intent intent = new Intent(getApplicationContext(), SpotifyTokenActivity.class);
                        startActivity(intent);
                    }
                    throw new IOException("Unexpected code " + response);
                }

            }
        });

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void mountAlbumListView(Response response) {
        final List<Album> albumList = new ArrayList<>();
        String albumId = "", albumName = "", albumCover = "";
        int albumYear;

        try {
            JSONObject res = new JSONObject(response.body().string());
            JSONObject albums = new JSONObject(res.getString("albums"));
            JSONArray items = new JSONArray(albums.getString("items"));

            for (int i = 0; i < items.length(); i++) {
                albumId = items.getJSONObject(i).get("id").toString();
                albumName = items.getJSONObject(i).get("name").toString();

                JSONArray artists = (JSONArray) items.getJSONObject(i).get("artists");
                List<String> artistList = new ArrayList<>();

                for (int j = 0; j < artists.length(); j++) {
                    String artistName = artists.getJSONObject(j).get("name").toString();
                    artistList.add(artistName);
                }

                JSONArray images = new JSONArray(items.getJSONObject(i).getString("images"));
                albumCover = images.getJSONObject(1).get("url").toString(); // imagem 300x300px

                String releaseDate = items.getJSONObject(i).get("release_date").toString();
                Calendar c = Calendar.getInstance();
                c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(releaseDate));
                albumYear = c.get(Calendar.YEAR);

                albumList.add(new Album(albumId, albumName, artistList, albumCover, albumYear));
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RecyclerAdapter adapter = new RecyclerAdapter(getApplicationContext(), albumList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerAlbums = findViewById(R.id.recyclerAlbums);
                    mRecyclerAlbums.setLayoutManager(layoutManager);
                    mRecyclerAlbums.setHasFixedSize(true);
                    mRecyclerAlbums.setAdapter(adapter);

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
                                            Album album = albumList.get(position);
                                            Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
                                            intent.putExtra("album", album);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onLongItemClick(View view, int position) {

                                        }
                                    }
                            )
                    );
                }
            });

        } catch (JSONException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}