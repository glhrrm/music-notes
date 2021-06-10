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

import br.edu.ifrs.musicnotes.R;

public class PreLoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pre_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonLogin = requireActivity().findViewById(R.id.buttonOptionLogin);
        Button buttonRegister = requireActivity().findViewById(R.id.buttonOptionRegister);

        buttonLogin.setOnClickListener(v -> NavHostFragment.findNavController(PreLoginFragment.this)
                .navigate(R.id.action_preLoginFragment_to_loginFragment));

        buttonRegister.setOnClickListener(v -> NavHostFragment.findNavController(PreLoginFragment.this)
                .navigate(R.id.action_preLoginFragment_to_registerFragment));
    }
}