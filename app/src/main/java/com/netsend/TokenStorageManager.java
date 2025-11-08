package com.netsend;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenStorageManager {

  private static final String TAG = "TokenStorageManager";
  private static final String PREFS_NAME = "fcm_token_prefs";
  private static final String TOKEN_KEY = "fcm_token";
  private SharedPreferences sharedPreferences;

  public TokenStorageManager(Context context) {
    sharedPreferences = context.getSharedPreferences(
      PREFS_NAME,
      Context.MODE_PRIVATE
    );
  }

  public void saveToken(String token) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(TOKEN_KEY, token);
    editor.apply();
    Log.d(TAG, "Token saved: " + token);
  }

  public String getToken() {
    String token = sharedPreferences.getString(TOKEN_KEY, null);
    Log.d(TAG, "Token retrieved: " + (token != null ? "exist" : "null"));
    return token;
  }

  public boolean hasToken() {
    return getToken() != null;
  }
}
