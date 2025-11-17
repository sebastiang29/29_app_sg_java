package com.netsend;

import android.content.Context;
import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class HttpRequestManager {

  private static final String TAG = "HttpRequestManager";

  public static void sendPostRequest(
    Context context,
    String serverUrl,
    JSONObject bodyJson
  ) {
    sendPostRequest(context, serverUrl, bodyJson, null);
  }

  public static void sendPostRequest(
    Context context,
    String serverUrl,
    JSONObject bodyJson,
    HttpCallback callback
  ) {
    new Thread(
      new Runnable() {
        @Override
        public void run() {
          try {
            TokenStorageManager tokenStorageManager = new TokenStorageManager(context);
            JSONObject headerJson = new JSONObject();
            String userKey = tokenStorageManager.getUserKey();
            if (userKey != null && !userKey.isEmpty()) {
              headerJson.put("x-user-key", userKey);
            }
            CredentialsManager credentialsManager = new CredentialsManager(
              context
            );
            if (serverUrl == null || serverUrl.isEmpty()) {
              Log.w(TAG, "Server URL is not configured");
              if (callback != null) {
                callback.onError("Server URL is not configured");
              }
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
            String jsonInputString = bodyJson.toString();
            try (OutputStream os = conn.getOutputStream()) {
              byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
              os.write(input, 0, input.length);
            }
            int code = conn.getResponseCode();
            Log.d(TAG, "Response Code: " + code);
            if (callback != null) {
              if (code >= 200 && code < 300) {
                callback.onSuccess("Request successful. Code: " + code);
              } else {
                callback.onError("HTTP Error: " + code);
              }
            }
            conn.disconnect();
          } catch (Exception e) {
            Log.e(TAG, "Error sending POST request", e);
            if (callback != null) {
              callback.onError("Exception: " + e.getMessage());
            }
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
