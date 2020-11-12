package br.edu.ifrs.musicnotes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.model.Album;

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mFirebase = FirebaseDatabase.getInstance().getReference();
    private Album mAlbum;
    private ShimmerFrameLayout mShimmerContainer;
    private ConstraintLayout mAlbumContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mAlbumContainer = findViewById(R.id.albumContainer);
        mAlbumContainer.setVisibility(View.GONE);

        mShimmerContainer = findViewById(R.id.shimmerContainer);
        mShimmerContainer.setVisibility(View.VISIBLE);

        Button btnSaveAlbum = findViewById(R.id.btnSaveAlbum);
        btnSaveAlbum.setOnClickListener(this);

        mAlbum = (Album) getIntent().getSerializableExtra("album");

        TextView albumName = findViewById(R.id.albumName);
        TextView artistName = findViewById(R.id.artistName);
        TextView albumYear = findViewById(R.id.albumYear);
        final RatingBar albumRating = findViewById(R.id.albumRating);
        final EditText albumReview = findViewById(R.id.albumReview);
        ImageView albumCover = findViewById(R.id.albumCover);

        albumName.setText(mAlbum.getTitle());

        for (Iterator<String> i = mAlbum.getArtists().iterator(); i.hasNext(); ) {
            artistName.append(i.next());
            if (i.hasNext()) artistName.append(", ");
        }

        albumYear.setText(String.valueOf(mAlbum.getYear()));

        Glide.with(this).load(mAlbum.getImages().get("medium")).into(albumCover);

        DatabaseReference albums = mFirebase.child("albums");
        DatabaseReference albumId = albums.child(mAlbum.getId());
        albumId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mAlbum = snapshot.getValue(Album.class);
                    albumRating.setRating(mAlbum.getRating());
                    albumReview.setText(mAlbum.getReview());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mShimmerContainer.setVisibility(View.GONE);
                        mAlbumContainer.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        RatingBar ratingBar = findViewById(R.id.albumRating);
        float albumRating = ratingBar.getRating();

        EditText editTextAlbumReview = findViewById(R.id.albumReview);
        String albumReview = editTextAlbumReview.getText().toString();

        mAlbum.setRating(albumRating);
        mAlbum.setReview(albumReview);

//        mFirebase.child("albums").child(mAlbum.getId()).setValue(mAlbum)

        Album album = new Album(mAlbum.getReview(), mAlbum.getRating());
        DatabaseReference albumReference = mFirebase.child("albums").child(mAlbum.getId());
        albumReference.setValue(album)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
    }
}