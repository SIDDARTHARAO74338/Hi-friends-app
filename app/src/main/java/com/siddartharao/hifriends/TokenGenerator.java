package com.siddartharao.hifriends;

import android.content.Context;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.GenericUrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;

public class TokenGenerator {

    public void generateToken(Context context) {
        JsonFetcher.fetchJsonFile(context, new JsonFetcher.JsonFetchCallback() {
            @Override
            public void onSuccess(File jsonFile) {
                try {
                    GoogleCredentials credentials = GoogleCredentials
                            .fromStream(new FileInputStream(jsonFile))
                            .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

                    credentials.refreshIfExpired();
                    String token = credentials.getAccessToken().getTokenValue();

                    // Save the token in TokenStorage
                    TokenStorage.setToken(token);

                    Log.d("TokenGenerator", "Generated Token: " + token);

                } catch (Exception e) {
                    Log.e("TokenGenerator", "Error generating token", e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TokenGenerator", "Failed to fetch JSON file", e);
            }
        });
    }

}
