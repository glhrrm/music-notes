package br.edu.ifrs.musicnotes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

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

        for (Iterator i = mAlbum.getArtists().iterator(); i.hasNext(); ) {
            artistName.append(i.next().toString());
            if (i.hasNext()) artistName.append(", ");
        }

        albumYear.setText(String.valueOf(mAlbum.getYear()));

        Glide.with(this).load(mAlbum.getImage()).into(albumCover);

        DatabaseReference albums = mFirebase.child("albums");
        DatabaseReference albumId = albums.child(mAlbum.getId());
        albumId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                View loadingFragment = findViewById(R.id.loadingFragment);
                loadingFragment.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    mAlbum = snapshot.getValue(Album.class);
                    albumRating.setRating(mAlbum.getRating() / 2);
                    albumReview.setText(mAlbum.getReview());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        RatingBar ratingBar = findViewById(R.id.albumRating);
        int albumRating = (int) (ratingBar.getRating() * 2);

        EditText editTextAlbumReview = findViewById(R.id.albumReview);
        String albumReview = editTextAlbumReview.getText().toString();

        mAlbum.setRating(albumRating);
        mAlbum.setReview(albumReview);

        DatabaseReference albums = mFirebase.child("albums");
        albums.child(mAlbum.getId()).setValue(mAlbum);

        finish();
    }
}