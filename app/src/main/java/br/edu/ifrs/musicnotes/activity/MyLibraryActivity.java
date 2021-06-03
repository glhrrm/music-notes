package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.edu.ifrs.musicnotes.R;

public class MyLibraryActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPreferences = getSharedPreferences("spotifyAuth", MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOutIcon) {
//            FirebaseAuth.getInstance().signOut();
            mSharedPreferences.edit().clear().apply();
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
        } else if (item.getItemId() == R.id.myLibraryIcon) {
            startActivity(new Intent(this, MyLibraryActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}