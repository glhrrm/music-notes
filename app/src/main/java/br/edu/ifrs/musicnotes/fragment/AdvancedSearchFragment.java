package br.edu.ifrs.musicnotes.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.adapter.AlbumAdapter;
import br.edu.ifrs.musicnotes.helper.Firebase;
import br.edu.ifrs.musicnotes.interfaces.ParameterizedCallback;
import br.edu.ifrs.musicnotes.model.Album;
import br.edu.ifrs.musicnotes.services.SpotifyApi;

public class AdvancedSearchFragment extends Fragment {

    private Context context;
    private View view;
    private RecyclerView recyclerAlbumSearch;
    private ImageButton buttonFilter;
    private RelativeLayout filterTagsLayout, filterRatingLayout, filterYearLayout, filterUpdatedAtLayout;
    private ImageButton checkFilterTags, checkFilterRating, checkFilterYear, checkFilterUpdatedAt;
    private RangeSlider filterRating;
    private TextInputEditText filterYearMin, filterYearMax, filterUpdatedAtMin, filterUpdatedAtMax;

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
            view = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        recyclerAlbumSearch = requireActivity().findViewById(R.id.recyclerAdvancedSearch);

        checkFilterRating.setOnClickListener(v -> {
            filterRating.setEnabled(!filterRating.isEnabled());
        });

        buttonFilter.setOnClickListener(v -> {
            int ratingMin = filterRating.getValues().get(0).intValue();
            int ratingMax = filterRating.getValues().get(1).intValue();

            Firebase.getAlbums().addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> ids = new ArrayList<>();

                    snapshot.getChildren().forEach(dataSnapshot -> {
                        Album album = dataSnapshot.getValue(Album.class);
                        int rating = (int) album.getRating() * 2;
                        if (rating >= ratingMin && rating <= ratingMax) {
                            ids.add(dataSnapshot.getKey());
                        }
                    });

                    SpotifyApi.authenticate((AppCompatActivity) requireActivity(), () -> {
                        List<Album> albumList = SpotifyApi.getAlbums(ids);

                        AlbumAdapter albumAdapter = new AlbumAdapter(albumList, album -> {
                            Log.d("album", album.getTitle());
                        });
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                        recyclerAlbumSearch.setLayoutManager(layoutManager);
                        recyclerAlbumSearch.setHasFixedSize(true);
                        recyclerAlbumSearch.setAdapter(albumAdapter);

                        LinearLayout filtersFragment = view.findViewById(R.id.filtersFragment);
                        filtersFragment.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    private void bindViews(View view) {
        filterTagsLayout = view.findViewById(R.id.filterTagsLayout);
        filterRatingLayout = view.findViewById(R.id.filterRatingLayout);
        filterYearLayout = view.findViewById(R.id.filterYearLayout);
        filterUpdatedAtLayout = view.findViewById(R.id.updatedAtLayout);
//        checkFilterTags = view.findViewById(R.id.checkFilterTags);
        checkFilterRating = view.findViewById(R.id.checkFilterRating);
//        checkFilterYear = view.findViewById(R.id.checkFilterYear);
//        checkFilterUpdatedAt = view.findViewById(R.id.checkFilterUpdatedAt);
        filterRating = view.findViewById(R.id.filterRating);
        filterYearMin = view.findViewById(R.id.filterYearMin);
        filterYearMax = view.findViewById(R.id.filterYearMax);
        filterUpdatedAtMin = view.findViewById(R.id.filterUpdatedAtMin);
        filterUpdatedAtMax = view.findViewById(R.id.filterUpdatedAtMax);
        buttonFilter = view.findViewById(R.id.buttonFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAlbumsByTags(String[] tags, ParameterizedCallback callback) {
        // String[] tags = query.trim().split("\\s*,\\s*");

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
}