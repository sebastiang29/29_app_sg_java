package com.example.a29_app_sg.push_not;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.util.Log;

public class JavaScriptInterface {
  private Context context;
  private static final String TAG = "JSInterface";

  public JavaScriptInterface(Context context) {
    this.context = context;
  }

  @JavascriptInterface
  public void registerToken(String identificacion) {
    Log.d(TAG, "registerToken called with identificacion: " + identificacion);
    MyFirebaseMessagingService.messagingService.registerToken(identificacion, new HttpRequestManager.Callback() {
      @Override
      public void onSuccess(String response) {
        Log.d(TAG, "Token registered successfully: " + response);
      }

      @Override
      public void onFailure(Exception e) {
        Log.e(TAG, "Failed to register token", e);
      }
    });
  }
}