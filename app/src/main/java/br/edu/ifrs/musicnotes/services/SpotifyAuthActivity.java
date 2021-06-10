package br.edu.ifrs.musicnotes.services;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import br.edu.ifrs.musicnotes.BuildConfig;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyAuthActivity extends AppCompatActivity {

    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    private static final String CLIENT_SECRET = BuildConfig.CLIENT_SECRET;
    private static final String REDIRECT_URI = BuildConfig.REDIRECT_URI;
    private static final int REQUEST_CODE = BuildConfig.REQUEST_CODE;
    private static final String TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token";
    private static final int TOKEN_EXPIRATION_MILLIS = 3600000;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences("spotifyAuth", MODE_PRIVATE);

        boolean isUserAuthenticated = mSharedPreferences.getBoolean("isUserAuthenticated", false);
        if (isUserAuthenticated) {
            finishActivityWithResult();
            if (hasTokenExpired()) {
                try {
                    JSONObject tokenObject = refreshToken();
                    storeTokens(tokenObject);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            authenticate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            SharedPreferences.Editor editor = getSharedPreferences("spotifyAuth", MODE_PRIVATE).edit();
            editor.putBoolean("isUserAuthenticated", true);
            editor.apply();

            String code = response.getCode();
            try {
                JSONObject tokenObject = getTokens(code);
                storeTokens(tokenObject);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        finishActivityWithResult();
    }

    private void authenticate() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.CODE, REDIRECT_URI);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private JSONObject getTokens(String code) throws IOException, JSONException {
        FormBody.Builder body = new FormBody.Builder();
        body.add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET);

        Request request = new Request.Builder()
                .url(TOKEN_ENDPOINT)
                .post(body.build())
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        JSONObject result = null;
        if (response.isSuccessful()) {
            result = new JSONObject(response.body().string());
        }

        return result;
    }

    private JSONObject refreshToken() throws IOException, JSONException {
        String refreshToken = mSharedPreferences.getString("refreshToken", "");

        FormBody.Builder body = new FormBody.Builder();
        body.add("grant_type", "refresh_token")
                .add("refresh_token", Objects.requireNonNull(refreshToken))
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET);

        Request request = new Request.Builder()
                .url(TOKEN_ENDPOINT)
                .post(body.build())
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        JSONObject result = null;
        if (response.isSuccessful()) {
            result = new JSONObject(response.body().string());
        }

        return result;
    }

    private void storeTokens(JSONObject jsonObject) throws JSONException {
        SharedPreferences.Editor editor = getSharedPreferences("spotifyAuth", MODE_PRIVATE).edit();

        String accessToken = jsonObject.getString("access_token");
        editor.putString("accessToken", accessToken);

        if (jsonObject.has("refresh_token")) {
            String refreshToken = jsonObject.getString("refresh_token");
            editor.putString("refreshToken", refreshToken);
        }

        long tokenTimestamp = System.currentTimeMillis();
        editor.putLong("tokenTimestamp", tokenTimestamp);

        editor.apply();
    }

    private boolean hasTokenExpired() {
        long currentTimestamp = System.currentTimeMillis();
        long tokenTimestamp = mSharedPreferences.getLong("tokenTimestamp", currentTimestamp);

        return currentTimestamp - tokenTimestamp > TOKEN_EXPIRATION_MILLIS;
    }

    private void finishActivityWithResult() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}