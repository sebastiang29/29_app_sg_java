package com.netsend;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CredentialsManager {

  private static final String TAG = "CredentialsManager";
  private Properties properties;
  private Context context;
  private boolean isLoaded = false;

  public CredentialsManager(Context context) {
    this.context = context;
    loadCredentials();
  }

  private void loadCredentials() {
    properties = new Properties();
    try {
      InputStream inputStream = context
        .getAssets()
        .open("credentials.properties");
      properties.load(inputStream);
      inputStream.close();
      isLoaded = true;
      Log.d(TAG, "Credentials loaded successfully");
    } catch (IOException e) {
      Log.e(TAG, "Error loading credentials: " + e.getMessage());
      e.printStackTrace();
      isLoaded = false;
    }
  }

  public String getServerUrl() {
    if (!isLoaded) {
      Log.w(TAG, "Credentials not loaded, returning default server URL");
      return "https://default-server.com/api/endpoint";
    }
    String serverUrl = properties.getProperty(
      "server_url",
      "https://tuservidor.com/api/endpoint"
    );
    Log.d(TAG, "Server URL retrieved: " + serverUrl);
    return serverUrl;
  }

  public String getApiKey() {
    if (!isLoaded) {
      Log.w(TAG, "Credentials not loaded, returning default API key");
      return "default_api_key_123";
    }
    String apiKey = properties.getProperty("api_key", "tu_api_key_aqui");
    Log.d(
      TAG,
      "API Key retrieved: " +
      (apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : apiKey)
    );
    return apiKey;
  }

  public boolean areCredentialsLoaded() {
    return isLoaded;
  }

  public CredentialsData getAllCredentials() {
    return new CredentialsData(getServerUrl(), getApiKey());
  }

  public static class CredentialsData {

    public final String serverUrl;
    public final String apiKey;

    public CredentialsData(String serverUrl, String apiKey) {
      this.serverUrl = serverUrl;
      this.apiKey = apiKey;
    }

    @Override
    public String toString() {
      return (
        "CredentialsData{" +
        "serverUrl='" +
        serverUrl +
        '\'' +
        ", apiKey='***'" +
        '}'
      );
    }
  }
}
