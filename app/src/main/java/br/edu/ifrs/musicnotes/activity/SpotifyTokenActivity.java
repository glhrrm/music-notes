package br.edu.ifrs.musicnotes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import br.edu.ifrs.musicnotes.BuildConfig;

public class SpotifyTokenActivity extends AppCompatActivity {

    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    private static final String REDIRECT_URI = BuildConfig.REDIRECT_URI;
    private static final int REQUEST_CODE = BuildConfig.REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth();
        this.getSharedPreferences("spotify", MODE_PRIVATE);
    }

    private void auth() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    SharedPreferences.Editor editor = getSharedPreferences("spotify", MODE_PRIVATE).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();

                    Log.d("starting", "got auth token");

                    finish();
                    break;
                case ERROR:
                    Log.e("error", response.getError());
                    break;
            }
        }
    }
}