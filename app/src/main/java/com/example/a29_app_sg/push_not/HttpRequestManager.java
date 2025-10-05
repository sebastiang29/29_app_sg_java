package com.example.a29_app_sg.push_not;

import android.content.Context;
import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class HttpRequestManager {

  private static final String TAG = "HttpRequestManager";

  public void sendPostRequest(
    Context context,
    String serverUrl,
    JSONObject jsonInput
  ) {
    new Thread(
      new Runnable() {
        @Override
        public void run() {
          try {
            CredentialsManager credentialsManager = new CredentialsManager(
              context
            );
            if (serverUrl == null || serverUrl.isEmpty()) {
              Log.w(TAG, "Server URL is not configured");
              return;
            }
            URL url = new URL(serverUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            String apiKey = credentialsManager.getApiKey();
            if (apiKey != null && !apiKey.isEmpty()) {
              conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            conn.setDoOutput(true);
            String jsonInputString = jsonInput.toString();
            try (OutputStream os = conn.getOutputStream()) {
              byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
              os.write(input, 0, input.length);
            }
            int code = conn.getResponseCode();
            Log.d(TAG, "Response Code: " + code);
            conn.disconnect();
          } catch (Exception e) {
            Log.e(TAG, "Error sending POST request", e);
          }
        }
      }
    ).start();
  }

  public void sendPostRequest(
    Context context,
    String serverUrl,
    JSONObject jsonInput,
    HttpCallback callback
  ) {
    new Thread(
      new Runnable() {
        @Override
        public void run() {
          try {
            CredentialsManager credentialsManager = new CredentialsManager(
              context
            );
            if (serverUrl == null || serverUrl.isEmpty()) {
              Log.w(TAG, "Server URL is not configured");
              return;
            }
            URL url = new URL(serverUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            String apiKey = credentialsManager.getApiKey();
            if (apiKey != null && !apiKey.isEmpty()) {
              conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            conn.setDoOutput(true);
            String jsonInputString = jsonInput.toString();
            try (OutputStream os = conn.getOutputStream()) {
              byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
              os.write(input, 0, input.length);
            }
            int code = conn.getResponseCode();
            Log.d(TAG, "Response Code: " + code);
            conn.disconnect();
          } catch (Exception e) {
            Log.e(TAG, "Error sending POST request", e);
          }
        }
      }
    ).start();
  }
  
  public interface HttpCallback {
    void onSuccess(String response);
    void onError(String error);
  }
}
