package com.netsend;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.json.JSONObject;

/* import android.os.StrictMode;
import android.widget.Toast;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL; */

public class NotificationActionReceiver extends BroadcastReceiver {

  private static final String TAG = "NotificationActionReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    CredentialsManager credentialsManager = new CredentialsManager(context);
    String serverUrl = credentialsManager.getServerUrl() + "/track_click";
    String url = intent.getStringExtra("url");
    String pushId = intent.getStringExtra("push_id");
    String buttonText = intent.getStringExtra("button_text");
    int notificationId = Integer.parseInt(
      intent.getStringExtra("notification_id")
    );
    Log.d(TAG, "Server URL: " + serverUrl);
    Log.d(TAG, "Push ID: " + pushId);
    Log.d(TAG, "Notification ID: " + notificationId);
    /* if (pushId != null && buttonText != null) {
      sendHttpRequest(context, serverUrl, url, pushId, buttonText);
    } */
    if (url != null && !url.isEmpty() && (url.startsWith("https://"))) {
      try {
        Log.d(TAG, "Button clicked: " + buttonText + ", URL: " + url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
        NotificationManager notificationManager =
          (NotificationManager) context.getSystemService(
            Context.NOTIFICATION_SERVICE
          );
        /* if (url.startsWith("https://")) {
          Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
          browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(browserIntent);
        } else if (url.startsWith("/")) {
          Intent internalIntent = new Intent(context, InternalActivity.class);
          internalIntent.putExtra("url", url);
          internalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(internalIntent);
        } */
        notificationManager.cancel(notificationId);
      } catch (Exception e) {
        Log.e(TAG, "Error opening URL: " + e.getMessage());
        e.printStackTrace();
      }
    }
    // Muestra un Toast como confirmación
    //Toast.makeText(context, "Abriendo: " + url, Toast.LENGTH_SHORT).show();
  }

  private void sendHttpRequest(
    Context context,
    String serverUrl,
    String url,
    String pushId,
    String buttonText
  ) {
    try {
      JSONObject jsonInput = new JSONObject();
      jsonInput.put("url", url);
      jsonInput.put("push_id", pushId);
      jsonInput.put("button_text", buttonText);

      // HttpRequestManager.sendPostRequest(this, serverUrl, jsonInput, callback);
      HttpRequestManager.sendPostRequest(
        context,
        serverUrl,
        jsonInput,
        new HttpRequestManager.HttpCallback() {
          @Override
          public void onSuccess(String response) {
            Log.d(TAG, "Request successful: " + response);
          }

          @Override
          public void onError(String error) {
            Log.e(TAG, "Request error: " + error);
          }
        }
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
