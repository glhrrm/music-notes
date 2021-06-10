package br.edu.ifrs.musicnotes.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.activity.MainActivity;

public class LoginFragment extends Fragment {

    private FrameLayout layoutProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutProgressBar = requireActivity().findViewById(R.id.layoutProgressBar);
        layoutProgressBar.setVisibility(View.GONE);

        Button buttonLogin = requireActivity().findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> {
            String email = ((TextInputEditText) requireActivity().findViewById(R.id.inputEmail)).getText().toString();
            String password = ((TextInputEditText) requireActivity().findViewById(R.id.inputPassword)).getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                layoutProgressBar.setVisibility(View.VISIBLE);

                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            startActivity(new Intent(getContext(), MainActivity.class));
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(view, "Erro ao logar usuário", Snackbar.LENGTH_SHORT).show();
                        });
            } else {
                Snackbar.make(view, R.string.fill_all_fields, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}