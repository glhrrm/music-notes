package br.edu.ifrs.musicnotes.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.model.Album;

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    private ConstraintLayout mAlbumContainer;
    private ShimmerFrameLayout mShimmerContainer;
    private Album mAlbum;
    private TextView albumName, artistName, albumYear;
    private ImageView albumCover;
    private RatingBar ratingBar;
    private float albumRating;
    private EditText editTextAlbumReview;
    private String albumReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mAlbumContainer = findViewById(R.id.albumContainer);
        mShimmerContainer = findViewById(R.id.shimmerContainer);

        displayShimmer(true);

        FloatingActionButton fabSaveAlbum = findViewById(R.id.fabSaveAlbum);
        fabSaveAlbum.setOnClickListener(this);

        albumName = findViewById(R.id.albumName);
        artistName = findViewById(R.id.artistName);
        albumYear = findViewById(R.id.albumYear);
        albumCover = findViewById(R.id.albumCover);
        ratingBar = findViewById(R.id.albumRating);
        editTextAlbumReview = findViewById(R.id.albumReview);
        TextView updatedAtLabel = findViewById(R.id.updatedAtLabel);
        TextView updatedAt = findViewById(R.id.updatedAt);

        mAlbum = (Album) getIntent().getSerializableExtra("album");

        setDataFromApi();

        DatabaseReference albumId = mFirebase.child("albums").child(mAlbum.getId());
        albumId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mAlbum = snapshot.getValue(Album.class);

                    assert mAlbum != null;
                    ratingBar.setRating(mAlbum.getRating());
                    editTextAlbumReview.setText(mAlbum.getReview());
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

    @Override
    public void onClick(View view) {
        albumRating = ratingBar.getRating();
        albumReview = editTextAlbumReview.getText().toString();

        if (hasDataChanged()) {
            mAlbum.setRating(albumRating);
            mAlbum.setReview(albumReview);
            mAlbum.setUpdatedAt(System.currentTimeMillis());

            mFirebase.child("albums").child(mAlbum.getId()).setValue(mAlbum)
                    .addOnSuccessListener(aVoid -> {
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    });
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        albumRating = ratingBar.getRating();
        albumReview = editTextAlbumReview.getText().toString();

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

    protected void displayShimmer(boolean display) {
        if (display) {
            mAlbumContainer.setVisibility(View.GONE);
            mShimmerContainer.setVisibility(View.VISIBLE);
        } else {
            mShimmerContainer.setVisibility(View.GONE);
            mAlbumContainer.setVisibility(View.VISIBLE);
        }
    }

    protected void setDataFromApi() {
        albumName.setText(mAlbum.getTitle());

        for (Iterator<String> artist = mAlbum.getArtists().iterator(); artist.hasNext(); ) {
            artistName.append(artist.next());
            if (artist.hasNext()) artistName.append(", ");
        }

        albumYear.setText(String.valueOf(mAlbum.getYear()));

        Glide.with(this).load(mAlbum.getImages().get("medium")).into(albumCover);
    }

    protected boolean hasDataChanged() {
        return albumRating != mAlbum.getRating() || !albumReview.equals(mAlbum.getReview());
    }
}