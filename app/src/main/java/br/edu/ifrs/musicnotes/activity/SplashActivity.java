package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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
        handle.postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), AuthActivity.class));
            finish();
        }, 2000);
    }
}