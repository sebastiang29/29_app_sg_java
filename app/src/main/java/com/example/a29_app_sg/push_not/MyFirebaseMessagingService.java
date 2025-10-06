package com.example.a29_app_sg.push_not;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.a29_app_sg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = "FCMService";
  private CredentialsManager credentialsManager;

  @Override
  public void onCreate() {
    super.onCreate();
    credentialsManager = new CredentialsManager(this);
    initializeToken();
  }

  private void initializeToken() {
    Log.d(TAG, "Inicializando token FCM automáticamente...");
    getCurrentToken(
      this,
      new OnTokenReceivedListener() {
        @Override
        public void onTokenReceived(String token) {
          if (token != null) {
            Log.d(TAG, "Token obtenido automáticamente: " + token);
            // Opcional: registrar automáticamente
            // registerToken(MyFirebaseMessagingService.this, "user_id");
          } else {
            Log.w(TAG, "No se pudo obtener el token automáticamente");
          }
        }
      }
    );
  }

  private void initializeFCMToken(Context context) {
    Log.d(TAG, "Inicializando token FCM automáticamente...");
    getCurrentToken(
      context,
      new OnTokenReceivedListener() {
        @Override
        public void onTokenReceived(String token) {
          if (token != null) {
            Log.d(TAG, "Token obtenido automáticamente: " + token);
            // Opcional: registrar automáticamente
            // registerToken(MyFirebaseMessagingService.this, "user_id");
          } else {
            Log.w(TAG, "No se pudo obtener el token automáticamente");
          }
        }
      }
    );
  }

  @Override
  public void onNewToken(String token) {
    Log.d(TAG, "Refreshed token: " + token);
    TokenStorageManager tokenStorageManager = new TokenStorageManager(this);
    tokenStorageManager.saveToken(token);
    // registerToken(this, "user_id", null);
  }

  public static void registerToken(Context context, String identificacion) {
    try {
      registerToken(context, identificacion, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void registerToken(
    Context context,
    String identificacion,
    HttpRequestManager.HttpCallback callback
  ) {
    try {
      TokenStorageManager tokenStorageManager = new TokenStorageManager(
        context
      );
      CredentialsManager credentialsManager = new CredentialsManager(context);
      String token = tokenStorageManager.getToken();
      String serverUrl = credentialsManager.getServerUrl() + "/register_token";
      if (token == null) {
        Log.w(TAG, "Token is null, cannot send to server");
        if (callback != null) callback.onError("FCM Token is not available");
        return;
      }

      JSONObject jsonInput = new JSONObject();
      jsonInput.put("token", token);
      jsonInput.put("identificacion", identificacion);

      HttpRequestManager.sendPostRequest(
        context,
        serverUrl,
        jsonInput,
        callback
      );
    } catch (Exception e) {
      Log.e(TAG, "Error in registerToken: " + e.getMessage());
      if (callback != null) callback.onError("Exception: " + e.getMessage());
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
      showNotificationWithButtons(
        title,
        body,
        pushId,
        button1Text,
        button1Url,
        button2Text,
        button2Url
      );
    } else {
      showNotification(title, body);
    }
  }

  public static void getCurrentToken(
    Context context,
    OnTokenReceivedListener listener
  ) {
    TokenStorageManager tokenStorage = new TokenStorageManager(context);
    String savedToken = tokenStorage.getToken();

    if (savedToken != null) {
      listener.onTokenReceived(savedToken);
    } else {
      // Obtener nuevo token de Firebase
      FirebaseMessaging.getInstance()
        .getToken()
        .addOnCompleteListener(
          new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
              if (!task.isSuccessful()) {
                Log.w(
                  TAG,
                  "Fetching FCM registration token failed",
                  task.getException()
                );
                listener.onTokenReceived(null);
                return;
              }
              String token = task.getResult();
              tokenStorage.saveToken(token);
              listener.onTokenReceived(token);
            }
          }
        );
    }
  }

  private void showNotification(String title, String body) {
    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = "fcm_notifications"; // Cambia esto por el ID
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        "Push Notifications",
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
    String channelId = "fcm_notifications"; // Cambia esto por el ID
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        "Push Notifications",
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
    actionIntent1.putExtra("button_text", button1Text);
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
      actionIntent2.putExtra("button_text", button2Text);
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
