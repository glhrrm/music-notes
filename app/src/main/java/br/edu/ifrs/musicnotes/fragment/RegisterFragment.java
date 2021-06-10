package br.edu.ifrs.musicnotes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifrs.musicnotes.R;

public class RegisterFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonRegister = requireActivity().findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            String email = ((TextInputEditText) requireActivity().findViewById(R.id.inputEmail)).getText().toString();
            String password = ((TextInputEditText) requireActivity().findViewById(R.id.inputPassword)).getText().toString();
            String confirmPassword = ((TextInputEditText) requireActivity().findViewById(R.id.inputConfirmPassword)).getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Snackbar.make(view, R.string.fill_all_fields, Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (password.equals(confirmPassword)) {
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            Snackbar.make(view, "Usuário criado com sucesso", Snackbar.LENGTH_SHORT).show();
                            NavHostFragment.findNavController(RegisterFragment.this)
                                    .navigate(R.id.action_registerFragment_to_preLoginFragment);
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(view, "Erro ao criar usuário", Snackbar.LENGTH_SHORT).show();
                        });
            } else {
                Snackbar.make(view, "Senhas não conferem", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}