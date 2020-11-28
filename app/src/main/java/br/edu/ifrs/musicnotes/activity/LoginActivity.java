package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.edu.ifrs.musicnotes.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        TextInputEditText userEmailEditText = findViewById(R.id.userEmail);
        TextInputEditText userPasswordEditText = findViewById(R.id.userPassword);

        String userEmail = Objects.requireNonNull(userEmailEditText.getText()).toString();
        String userPassword = Objects.requireNonNull(userPasswordEditText.getText()).toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        finish();
                    } else {
                        Snackbar.make(getWindow().getDecorView().getRootView(),
                                "Erro ao logar usuário",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }
}