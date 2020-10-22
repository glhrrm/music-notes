package br.edu.ifrs.musicnotes.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.ifrs.musicnotes.R;

public class AlbumCreateActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_create);

        Button btnCreateAlbum = findViewById(R.id.btnCreateAlbum);
        btnCreateAlbum.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText albumNameInput = findViewById(R.id.editTextAlbumName);
        EditText artistNameInput = findViewById(R.id.editTextArtistName);

        String albumName = albumNameInput.getText().toString();
        String artistName = artistNameInput.getText().toString();

        if (!albumName.isEmpty() && !artistName.isEmpty()) {
            String extraExample = (String) getIntent().getExtras().get("extraExample");
            String text = extraExample + "Álbum " + albumName + " do artista " + artistName + " criado";
            Toast.makeText(AlbumCreateActivity.this, text, Toast.LENGTH_LONG).show();
        } else {
            String text = "Preencha o álbum e o artista";
            Toast.makeText(AlbumCreateActivity.this, text, Toast.LENGTH_LONG).show();
        }
    }
}