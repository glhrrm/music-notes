package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ifrs.musicnotes.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth != null) {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        }

        buttonLogin.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        buttonRegister.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });
    }
}