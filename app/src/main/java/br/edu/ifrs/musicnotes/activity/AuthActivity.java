package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.helper.Firebase;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (Firebase.isUserAuthenticated()) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}