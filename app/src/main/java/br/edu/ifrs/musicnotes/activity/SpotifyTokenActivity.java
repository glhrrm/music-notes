package br.edu.ifrs.musicnotes.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import br.edu.ifrs.musicnotes.BuildConfig;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyTokenActivity extends AppCompatActivity {

    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    private static final String CLIENT_SECRET = BuildConfig.CLIENT_SECRET;
    private static final String REDIRECT_URI = BuildConfig.REDIRECT_URI;
    private static final int REQUEST_CODE = BuildConfig.REQUEST_CODE;
    private static final int TOKEN_EXPIRATION_MILLIS = 3600000;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences("spotifyAuth", MODE_PRIVATE);

        boolean isUserLogged = mSharedPreferences.getBoolean("isUserLogged", false);
        if (isUserLogged) {
            checkTokenExpiration();
        } else {
            auth();
        }
    }

    private void checkTokenExpiration() {
        long currentTimestamp = System.currentTimeMillis();
        long tokenTimestamp = mSharedPreferences.getLong("tokenTimestamp", currentTimestamp);

        if (currentTimestamp - tokenTimestamp > TOKEN_EXPIRATION_MILLIS) {
            refreshToken();
        } else {
            resumeSearchActivity();
        }
    }

    private void resumeSearchActivity() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void auth() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.CODE, REDIRECT_URI);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            SharedPreferences.Editor editor = getSharedPreferences("spotifyAuth", MODE_PRIVATE).edit();
            editor.putBoolean("isUserLogged", true);
            editor.apply();

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            String code = response.getCode();

            getTokens(code);
        }
    }

    private void getTokens(String code) {
        FormBody.Builder body = new FormBody.Builder();
        body.add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET);

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(body.build())
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @SneakyThrows
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                JSONObject res = new JSONObject(Objects.requireNonNull(response.body()).string());
                String accessToken = res.getString("access_token");
                String refreshToken = res.getString("refresh_token");
                long tokenTimestamp = System.currentTimeMillis();

                SharedPreferences.Editor editor = getSharedPreferences("spotifyAuth", MODE_PRIVATE).edit();
                editor.putString("accessToken", accessToken);
                editor.putString("refreshToken", refreshToken);
                editor.putLong("tokenTimestamp", tokenTimestamp);
                editor.apply();

                checkTokenExpiration();
            }
        });
    }

    private void refreshToken() {
        String refreshToken = mSharedPreferences.getString("refreshToken", "");

        FormBody.Builder body = new FormBody.Builder();
        body.add("grant_type", "refresh_token")
                .add("refresh_token", Objects.requireNonNull(refreshToken))
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET);

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(body.build())
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @SneakyThrows
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                JSONObject res = new JSONObject(Objects.requireNonNull(response.body()).string());
                String accessToken = res.getString("access_token");
                long tokenTimestamp = System.currentTimeMillis();

                SharedPreferences.Editor editor = getSharedPreferences("spotifyAuth", MODE_PRIVATE).edit();
                editor.putString("accessToken", accessToken);
                editor.putLong("tokenTimestamp", tokenTimestamp);
                editor.apply();

                resumeSearchActivity();
            }
        });
    }
}