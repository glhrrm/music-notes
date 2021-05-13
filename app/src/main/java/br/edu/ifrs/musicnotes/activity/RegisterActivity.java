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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        TextInputEditText userEmailEditText = findViewById(R.id.userEmail);
        TextInputEditText userPasswordEditText = findViewById(R.id.userPassword);

        String userEmail = Objects.requireNonNull(userEmailEditText.getText()).toString();
        String userPassword = Objects.requireNonNull(userPasswordEditText.getText()).toString();

        if (!userEmail.isEmpty() && !userPassword.isEmpty()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        } else {
                            Snackbar.make(view,
                                    "Erro ao criar usuário",
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    });
        } else {
            Snackbar.make(view, "Preencha todos os campos", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}