package br.edu.ifrs.musicnotes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import br.edu.ifrs.musicnotes.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView appLogo = findViewById(R.id.appLogo);
        Glide.with(this).load(R.drawable.app_logo).into(appLogo);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                startActivity(new Intent(getApplicationContext(), SpotifySearchActivity.class));
                finish();
            }
        }, 3000);
    }
}