package br.edu.ifrs.musicnotes.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.model.Album;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyApi {

    private static final String SEARCH_ENDPOINT = "https://api.spotify.com/v1/search";
    private static final String ALBUM_ENDPOINT = "https://api.spotify.com/v1/albums";
    private static String token;

    public static void authenticate(AppCompatActivity activity, Runnable callback) {
        token = activity.getBaseContext()
                .getSharedPreferences("spotifyAuth", Activity.MODE_PRIVATE)
                .getString("accessToken", "");

        Fragment spotifyAuthFragment = new SpotifyAuthFragment(activity, callback);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(spotifyAuthFragment, "auth");
        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Album> getAlbums(List<String> ids) {
//        TODO: dividir a lista em partes de até 20 ids (limite de busca na API do Spotify)
        while (ids.size() > 20) {
            ids.remove(ids.size() - 1);
        }

        List<Album> albumList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(ALBUM_ENDPOINT + "?ids=" + String.join(",", ids))
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    JSONObject res = new JSONObject(response.body().string());
                    JSONArray albums = new JSONArray(res.getString("albums"));

                    for (int item = 0; item < albums.length(); item++) {
                        JSONObject jsonAlbum = albums.getJSONObject(item);
                        Album album = setAlbumObject(jsonAlbum);
                        albumList.add(album);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return albumList;
    }

    private static Album setAlbumObject(JSONObject item) {
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
            String albumCoverMedium = images.getJSONObject(1).get("url").toString(); // 300x300px
            Map<String, String> albumCover = new HashMap<>();
            albumCover.put("medium", albumCoverMedium);

            String releaseDate = item.get("release_date").toString();
            int albumYear = Integer.parseInt(releaseDate.substring(0, 4));

            album = new Album(albumId, albumName, artistList, albumCover, albumYear);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return album;
    }

    public static List<Album> searchAlbums(String query) {
//      Definir programaticamente
        int limit = 20, offset = 0;

        List<Album> albumList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(SEARCH_ENDPOINT + "?q=" + query + "&type=album&limit=" + limit + "&offset=" + offset)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    JSONObject res = new JSONObject(response.body().string());
                    JSONObject albums = new JSONObject(res.getString("albums"));
                    JSONArray items = new JSONArray(albums.getString("items"));

                    for (int item = 0; item < items.length(); item++) {
                        JSONObject jsonAlbum = items.getJSONObject(item);
                        Album album = setAlbumObject(jsonAlbum);
                        albumList.add(album);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return albumList;
    }

    public static class SpotifyAuthFragment extends Fragment {

        private final AppCompatActivity activity;
        private final Runnable callback;
        private ActivityResultLauncher<Intent> authActivityResultLauncher;

        public SpotifyAuthFragment(AppCompatActivity activity, Runnable callback) {
            this.activity = activity;
            this.callback = callback;
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);

            authActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            SharedPreferences sharedPreferences = activity.getApplicationContext()
                                    .getSharedPreferences("spotifyAuth", Activity.MODE_PRIVATE);
                            token = sharedPreferences.getString("accessToken", "");

                            callback.run();
                        }

                        onDestroy();
                    });
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_empty, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Intent intent = new Intent(activity, SpotifyAuthActivity.class);
            authActivityResultLauncher.launch(intent);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }
}
