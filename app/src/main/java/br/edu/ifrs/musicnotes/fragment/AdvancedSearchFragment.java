package br.edu.ifrs.musicnotes.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.helper.Firebase;
import br.edu.ifrs.musicnotes.interfaces.ParameterizedCallback;
import br.edu.ifrs.musicnotes.model.Album;
import br.edu.ifrs.musicnotes.services.SpotifyApi;

public class AdvancedSearchFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advanced_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = requireActivity().findViewById(R.id.searchTag);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] tags = query.trim().split("\\s*,\\s*");
                getAlbumsByTags(tags, albumIds -> {
                    SpotifyApi.authenticate((AppCompatActivity) requireActivity(), () -> {
                        List<Album> albumList = SpotifyApi.getAlbums((List<String>) albumIds);
                        showAlbums(albumList);
                    });
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAlbumsByTags(String[] tags, ParameterizedCallback callback) {
        List<String> albumIds = new ArrayList<>();

        Firebase.getAlbums().addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(dataSnapshot -> {
                    if (dataSnapshot.hasChild("tags")) {
                        dataSnapshot.child("tags").getChildren().forEach(tag -> {
                            if (Arrays.asList(tags).contains(tag.getValue().toString())) {
                                albumIds.add(dataSnapshot.getKey());
                            }
                        });
                    }
                });

                callback.run(albumIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showAlbums(List<Album> albumList) {
        TextView textView = requireActivity().findViewById(R.id.textview);
        albumList.forEach(album -> {
            textView.append(album.getTitle() + " " + album.getArtists() + "; ");
        });
    }
}