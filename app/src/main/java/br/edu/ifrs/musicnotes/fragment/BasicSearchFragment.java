package br.edu.ifrs.musicnotes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.adapter.AlbumAdapter;
import br.edu.ifrs.musicnotes.model.Album;
import br.edu.ifrs.musicnotes.services.SpotifyApi;

public class BasicSearchFragment extends Fragment {

    private Context context;
    private FrameLayout layoutProgressBar;
    private RecyclerView recyclerAlbumSearch;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = requireActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Workaround to keep instance of this view when coming back from ReviewFragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_basic_search, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerAlbumSearch = requireActivity().findViewById(R.id.recyclerAlbumSearch);

        layoutProgressBar = requireActivity().findViewById(R.id.layoutProgressBar);
        layoutProgressBar.setVisibility(View.GONE);

        SearchView searchView = requireActivity().findViewById(R.id.searchAlbum);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                layoutProgressBar.setVisibility(View.VISIBLE);

                SpotifyApi.authenticate((AppCompatActivity) requireActivity(), () -> {
                    List<Album> albumList = SpotifyApi.searchAlbums(query);

                    AlbumAdapter albumAdapter = new AlbumAdapter(albumList, album -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("album", album);
                        NavHostFragment.findNavController(BasicSearchFragment.this)
                                .navigate(R.id.action_basicSearchFragment_to_reviewFragment, bundle);
                    });
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                    recyclerAlbumSearch.setLayoutManager(layoutManager);
                    recyclerAlbumSearch.setHasFixedSize(true);
                    recyclerAlbumSearch.setAdapter(albumAdapter);

                    layoutProgressBar.setVisibility(View.GONE);

                    //        TODO: scroll listener
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}