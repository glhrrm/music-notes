package br.edu.ifrs.musicnotes.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.helper.Firebase;
import br.edu.ifrs.musicnotes.helper.Helper;
import br.edu.ifrs.musicnotes.model.Album;

public class ReviewFragment extends Fragment {

    private Album album;
    private TextView title;
    private TextView artists;
    private TextView year;
    private ImageView cover;
    private RatingBar rating;
    private EditText review;
    private TextView updatedAtLabel;
    private TextView updatedAt;
    private FloatingActionButton fabSaveAlbum;
    private ChipGroup chipGroupTags;
    private List<String> newTags;
    private ImageButton buttonAddTag;
    private float newRating;
    private String newReview;
    private RelativeLayout layoutReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        layoutReview.setVisibility(View.INVISIBLE);

        album = (Album) requireArguments().getSerializable("album");

        fabSaveAlbum.setOnClickListener(v -> saveAlbum());

        newTags = new ArrayList<>();
        buttonAddTag.setOnClickListener(v -> addTag());

        showBasicData();
        showExtraData();
    }

    private void addTag() {
        View dialogView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_add_tag, null);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Nova tag")
                .setView(dialogView)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            String tag = ((TextInputEditText) dialogView.findViewById(R.id.inputTag)).getText().toString();

            if (!tag.isEmpty()) {
                setTag(tag);
                alertDialog.dismiss();
            }
        });
    }

    private void showExtraData() {
        Firebase.getAlbums().child(album.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            album = snapshot.getValue(Album.class);
                            album.setId(snapshot.getKey());

                            rating.setRating(album.getRating());

                            review.setText(album.getReview());

                            if (album.getTags() != null) {
                                album.getTags().forEach(tag -> setTag(tag));
                            }

                            updatedAtLabel.setVisibility(View.VISIBLE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(album.getUpdatedAt());
                            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String updatedAtString = dateFormat.format(calendar.getTime());
                            updatedAt.setText(updatedAtString);
                        }

                        layoutReview.setVisibility(View.VISIBLE);
                        FrameLayout layoutProgressBar = requireView().findViewById(R.id.layoutProgressBar);
                        layoutProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showBasicData() {
        title.setText(album.getTitle());
        artists.append(Helper.stringBuilder(album.getArtists()));
        year.setText(String.valueOf(album.getYear()));
        Glide.with(this).load(album.getImages().get("medium")).into(cover);
    }

    private void bindViews(View view) {
        layoutReview = view.findViewById(R.id.layoutReview);
        title = view.findViewById(R.id.title);
        artists = view.findViewById(R.id.artists);
        year = view.findViewById(R.id.year);
        cover = view.findViewById(R.id.cover);
        chipGroupTags = view.findViewById(R.id.chipGroupTags);
        buttonAddTag = view.findViewById(R.id.buttonAddTag);
        rating = view.findViewById(R.id.rating);
        review = view.findViewById(R.id.review);
        updatedAtLabel = view.findViewById(R.id.updatedAtLabel);
        updatedAt = view.findViewById(R.id.updatedAt);
        fabSaveAlbum = view.findViewById(R.id.fabSaveAlbum);
    }

    private void saveAlbum() {
        newRating = rating.getRating();
        newReview = review.getText().toString();

        if (hasDataChanged()) {
            album.setRating(newRating);
            album.setReview(newReview);
            album.setTags(newTags);
            album.setUpdatedAt(System.currentTimeMillis());

            Firebase.getAlbums().child(album.getId()).setValue(album)
                    .addOnSuccessListener(aVoid -> {
                        Snackbar.make(requireActivity().getWindow().getDecorView().getRootView(),
                                "Sua resenha foi atualizada",
                                Snackbar.LENGTH_SHORT)
                                .show();

                        NavHostFragment.findNavController(ReviewFragment.this)
                                .navigateUp();
                    });
        } else {
            NavHostFragment.findNavController(ReviewFragment.this)
                    .navigateUp();
        }
    }

    private boolean hasDataChanged() {
        newRating = rating.getRating();
        newReview = review.getText().toString();

        if (album.getReview() == null) {
            album.setReview("");
        }

        if (album.getTags() == null) {
            album.setTags(new ArrayList<>());
        }

        Set<String> oldAlbumTags = new HashSet<>(album.getTags());
        Set<String> newAlbumTags = new HashSet<>(newTags);

        return newRating != album.getRating() || !newReview.equals(album.getReview()) || !newAlbumTags.equals(oldAlbumTags);
    }

    private void setTag(String tag) {
        Chip chipTag = (Chip) LayoutInflater.from(getActivity())
                .inflate(R.layout.chip_tag, null);
        chipTag.setText(tag);
        chipTag.setCheckable(false);
        chipTag.setCloseIconVisible(false);
        chipTag.setOnClickListener(view -> chipTag.setCloseIconVisible(!chipTag.isCloseIconVisible()));
        chipTag.setOnCloseIconClickListener(view -> {
            newTags.remove(tag);
            chipGroupTags.removeView(view);
        });

        chipGroupTags.addView(chipTag);
        newTags.add(tag);
    }
}