package br.edu.ifrs.musicnotes.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.helper.Firebase;
import br.edu.ifrs.musicnotes.helper.Helper;
import br.edu.ifrs.musicnotes.model.Album;

public class AlbumActivity extends AppCompatActivity implements Helper {

    private ConstraintLayout mAlbumContainer;
    private ShimmerFrameLayout mShimmerContainer;
    private Album mAlbum;
    private TextView mAlbumName, mArtistName, mAlbumYear;
    private ImageView mAlbumCover;
    private RatingBar mRatingBar;
    private float mNewAlbumRating;
    private EditText mEditTextAlbumReview;
    private String mNewAlbumReview;
    private ChipGroup mChipGroupTags;
    private List<String> mNewAlbumTags;
    private TextView updatedAtLabel, updatedAt;
    private FloatingActionButton fabSaveAlbum;
    private ImageButton mButtonAddTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        bindViews();

        displayShimmer(true);

        fabSaveAlbum.setOnClickListener(view -> saveAlbum());

        mNewAlbumTags = new ArrayList<>();
        mButtonAddTag.setOnClickListener(view -> addTag());

        mAlbum = (Album) getIntent().getSerializableExtra("album");

        setAlbumFromApi();
        setAlbumFromDatabase();
    }

    /*
   Sets data from Spotify API passed through an Album bundle object from SearchActivity.
   These data are not stored into the app database, i.e., album title, artists, year and images.
    */
    private void setAlbumFromApi() {
        mAlbumName.setText(mAlbum.getTitle());

        for (Iterator<String> artist = mAlbum.getArtists().iterator(); artist.hasNext(); ) {
            mArtistName.append(artist.next());
            if (artist.hasNext()) mArtistName.append(", ");
        }

        mAlbumYear.setText(String.valueOf(mAlbum.getYear()));

        Glide.with(this).load(mAlbum.getImages().get("medium")).into(mAlbumCover);
    }

    /*
    Sets data from database based on id property passed through an Album bundle object from SearchActivity.
    These are app-exclusive data, i.e., album review, rating, tags and last update.
     */
    private void setAlbumFromDatabase() {
        Firebase.getAlbums().child(mAlbum.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mAlbum = snapshot.getValue(Album.class);
                            mAlbum.setId(snapshot.getKey());

                            mRatingBar.setRating(mAlbum.getRating());

                            mEditTextAlbumReview.setText(mAlbum.getReview());

                            if (mAlbum.getTags() != null) {
                                mAlbum.getTags().forEach(tag -> setTag(tag));
                            }

                            updatedAtLabel.setText(R.string.updated_at_label);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(mAlbum.getUpdatedAt());
                            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String updatedAtString = dateFormat.format(calendar.getTime());
                            updatedAt.setText(updatedAtString);
                        }

                        runOnUiThread(() -> displayShimmer(false));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /*
   Shows an input dialog to add a new tag and sets it if not empty.
    */
    private void addTag() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tag, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
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

    /*
    Creates a chip view, sets click behaviors
    and adds its content (the tag itself) to album tags array list.
     */
    private void setTag(String tag) {
        Chip chipTag = (Chip) LayoutInflater.from(AlbumActivity.this)
                .inflate(R.layout.chip_tag, null);
        chipTag.setText(tag);
        chipTag.setCheckable(false);
        chipTag.setCloseIconVisible(false);
        chipTag.setOnClickListener(view -> chipTag.setCloseIconVisible(!chipTag.isCloseIconVisible()));
        chipTag.setOnCloseIconClickListener(view -> {
            mNewAlbumTags.remove(tag);
            mChipGroupTags.removeView(view);
        });

        mChipGroupTags.addView(chipTag);
        mNewAlbumTags.add(tag);
    }

    /*
    Stores the album into the database.
     */
    private void saveAlbum() {
        mNewAlbumRating = mRatingBar.getRating();
        mNewAlbumReview = mEditTextAlbumReview.getText().toString();

        if (hasDataChanged()) {
            mAlbum.setRating(mNewAlbumRating);
            mAlbum.setReview(mNewAlbumReview);
            mAlbum.setTags(mNewAlbumTags);
            mAlbum.setUpdatedAt(System.currentTimeMillis());

            Firebase.getAlbums().child(mAlbum.getId()).setValue(mAlbum)
                    .addOnSuccessListener(aVoid -> {
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    });
        } else {
            finish();
        }
    }

    /*
    Checks whether album review, rating and tags has changed or not
    in order to avoid unnecessary database updates.
     */
    private boolean hasDataChanged() {
        mNewAlbumRating = mRatingBar.getRating();
        mNewAlbumReview = mEditTextAlbumReview.getText().toString();

        if (mAlbum.getReview() == null) {
            mAlbum.setReview("");
        }

        if (mAlbum.getTags() == null) {
            mAlbum.setTags(new ArrayList<>());
        }

        Set<String> mOldAlbumTagSet = new HashSet<>(mAlbum.getTags());
        Set<String> mNewAlbumTagSet = new HashSet<>(mNewAlbumTags);
        return mNewAlbumRating != mAlbum.getRating() || !mNewAlbumReview.equals(mAlbum.getReview()) || !mNewAlbumTagSet.equals(mOldAlbumTagSet);
    }

    private void bindViews() {
        mAlbumName = findViewById(R.id.albumName);
        mArtistName = findViewById(R.id.artistName);
        mAlbumYear = findViewById(R.id.albumYear);
        mAlbumCover = findViewById(R.id.albumCover);
        mRatingBar = findViewById(R.id.albumRating);
        mEditTextAlbumReview = findViewById(R.id.albumReview);
        updatedAtLabel = findViewById(R.id.updatedAtLabel);
        updatedAt = findViewById(R.id.updatedAt);
        mChipGroupTags = findViewById(R.id.chipGroupTags);
        mButtonAddTag = findViewById(R.id.buttonAddTag);
        fabSaveAlbum = findViewById(R.id.fabSaveAlbum);
        mAlbumContainer = findViewById(R.id.albumContainer);
        mShimmerContainer = findViewById(R.id.shimmerContainer);
    }

    @Override
    public void displayShimmer(boolean display) {
        if (display) {
            mAlbumContainer.setVisibility(View.GONE);
            mShimmerContainer.setVisibility(View.VISIBLE);
        } else {
            mShimmerContainer.setVisibility(View.GONE);
            mAlbumContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (hasDataChanged()) {
            new AlertDialog.Builder(this)
                    .setMessage("Seus dados não estão salvos. Deseja voltar à tela anterior?")
                    .setPositiveButton("Sim", (dialog, which) -> finish())
                    .setNegativeButton("Não", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            finish();
        }
    }
}