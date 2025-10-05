package com.example.a29_app_sg.push_not;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = "FCMService";
  private CredentialsManager credentialsManager;

  @Override
  public void onCreate() {
    super.onCreate();
    credentialsManager = new CredentialsManager(this);
  }

  @Override
  public void onNewToken(String token) {
    Log.d(TAG, "Refreshed token: " + token);
    TokenStorageManager tokenStorageManager = new TokenStorageManager(this);
    tokenStorageManager.saveToken(token);
    // sendRegistrationToServer(token);
  }

  private void registerToken(String identificacion) {
    try {
      String token = TokenStorageManager.getToken();
      String serverUrl = credentialsManager.getServerUrl() + "/register_token";
      if (token == null) {
        Log.w(TAG, "Token is null, cannot send to server");
        return;
      }

      JSONObject jsonInput = new JSONObject();
      jsonInput.put("token", token);
      jsonInput.put("identificacion", identificacion);
      String jsonInputString = jsonInput.toString();

      HttpRequestManager.sendPostRequest(this, serverUrl, jsonInput);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void registerToken(String identificacion, HttpRequestManager.HttpCallback callback) {
    try {
      String token = TokenStorageManager.getToken();
      String serverUrl = credentialsManager.getServerUrl() + "/register_token";
      if (token == null) {
        Log.w(TAG, "Token is null, cannot send to server");
        return;
      }

      JSONObject jsonInput = new JSONObject();
      jsonInput.put("token", token);
      jsonInput.put("identificacion", identificacion);
      String jsonInputString = jsonInput.toString();

      HttpRequestManager.sendPostRequest(this, serverUrl, jsonInput, callback);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public interface OnTokenReceivedListener {
    void onTokenReceived(String token);
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);

    String title = null;
    String body = null;
    String pushId = remoteMessage.getData().get("push_id");
    if (remoteMessage.getNotification() != null) {
      title = remoteMessage.getNotification().getTitle();
      body = remoteMessage.getNotification().getBody();
    }

    String button1Text = remoteMessage.getData().get("button1_text");
    String button1Url = remoteMessage.getData().get("button1_url");
    String button2Text = remoteMessage.getData().get("button2_text");
    String button2Url = remoteMessage.getData().get("button2_url");
    if (button1Text != null && button1Url != null) {
      // Manejar la notificación con el pushId
    } else {}
  }

  private void showNotification(String title, String body) {
    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = "my_channel_id";
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        "My Channel",
        NotificationManager.IMPORTANCE_DEFAULT
      );
      notificationManager.createNotificationChannel(channel);
    }

    NotificationCompat.Builder notificationBuilder =
      new NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true);

    notificationManager.notify(1, notificationBuilder.build());
  }

  private void showNotificationWithButtons(
    String title,
    String body,
    String pushId,
    String button1Text,
    String button1Url,
    String button2Text,
    String button2Url
  ) {
    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = "my_channel_id"; // Cambia esto por el ID
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        "My Channel",
        NotificationManager.IMPORTANCE_DEFAULT
      );
      notificationManager.createNotificationChannel(channel);
    }
    NotificationCompat.Builder notificationBuilder =
      new NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia esto por tu ícono
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true);

    // Botón 1
    Intent actionIntent1 = new Intent(this, NotificationActionReceiver.class);
    // actionIntent1.setAction("ACTION_BUTTON_1");
    actionIntent1.putExtra("url", button1Url);
    actionIntent1.putExtra("push_id", pushId);
    actionIntent1.putExtra("button1_text", button1Text);
    PendingIntent actionPendingIntent1 = PendingIntent.getBroadcast(
      this,
      1,
      actionIntent1,
      PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );
    notificationBuilder.addAction(
      R.drawable.ic_launcher_foreground, // Cambia esto por el ícono del botón
      button1Text,
      actionPendingIntent1
    );
    // Botón 2
    if (button2Text != null && button2Url != null) {
      Intent actionIntent2 = new Intent(this, NotificationActionReceiver.class);
      // actionIntent2.setAction("ACTION_BUTTON_2");
      actionIntent2.putExtra("url", button2Url);
      actionIntent2.putExtra("push_id", pushId);
      actionIntent2.putExtra("button2_text", button2Text);
      PendingIntent actionPendingIntent2 = PendingIntent.getBroadcast(
        this,
        2,
        actionIntent2,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
      );
      notificationBuilder.addAction(
        R.drawable.ic_launcher_foreground, // Cambia esto por el ícono del botón
        button2Text,
        actionPendingIntent2
      );
    }
    notificationManager.notify(1, notificationBuilder.build());
  }
}
