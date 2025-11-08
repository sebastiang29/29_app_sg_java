package com.netsend;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
  }

  public static void initializeFCMToken(Context context) {
    Log.d(TAG, "Inicializando token FCM automáticamente...");
    getCurrentToken(
      context,
      new OnTokenReceivedListener() {
        @Override
        public void onTokenReceived(String token) {
          if (token != null) {
            Log.d(TAG, "Token obtenido automáticamente: " + token);
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
    Log.d(TAG, "🚀 === PUSH NOTIFICATION RECIBIDA ===");
    Log.d(TAG, "📡 From: " + remoteMessage.getFrom());
    Log.d(TAG, "📦 Message ID: " + remoteMessage.getMessageId());
    Log.d(TAG, "⏰ Sent Time: " + remoteMessage.getSentTime());
    Log.d(TAG, "🏷️ Message Type: " + remoteMessage.getMessageType());
    Log.d(TAG, "🎯 To: " + remoteMessage.getTo());
    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "🔔 === NOTIFICATION PAYLOAD ===");
      Log.d(TAG, "📝 Title: " + remoteMessage.getNotification().getTitle());
      Log.d(TAG, "📄 Body: " + remoteMessage.getNotification().getBody());
      Log.d(TAG, "🏷️ Tag: " + remoteMessage.getNotification().getTag());
      Log.d(TAG, "🎨 Color: " + remoteMessage.getNotification().getColor());
      Log.d(TAG, "🔊 Sound: " + remoteMessage.getNotification().getSound());
      Log.d(TAG, "🖼️ Image: " + remoteMessage.getNotification().getImageUrl());
      Log.d(
        TAG,
        "📊 Channel: " + remoteMessage.getNotification().getChannelId()
      );
      Log.d(
        TAG,
        "👆 Click Action: " + remoteMessage.getNotification().getClickAction()
      );
    } else {
      Log.d(TAG, "❌ No notification payload");
    }
    /* String title = null;
    String body = null;
    if (remoteMessage.getNotification() != null) {
      title = remoteMessage.getNotification().getTitle();
      body = remoteMessage.getNotification().getBody();
    }
    if (title == null) title = "Notificación";
    if (body == null) body = "Prueba de notificación"; */
    String title = remoteMessage.getData().get("title");
    String body = remoteMessage.getData().get("body");
    String image = remoteMessage.getData().get("image");
    String pushId = remoteMessage.getMessageId();
    String url = remoteMessage.getData().get("url");
    String button1Text = remoteMessage.getData().get("button1_text");
    String button1Url = remoteMessage.getData().get("button1_url");
    String button2Text = remoteMessage.getData().get("button2_text");
    String button2Url = remoteMessage.getData().get("button2_url");
    if (button1Text != null && button1Url != null) {
      showNotificationWithButtons(
        title,
        body,
        image,
        pushId,
        button1Text,
        button1Url,
        button2Text,
        button2Url
      );
    } else {
      showNotification(title, body, image, url, pushId);
    }
  }

  public static void getCurrentToken(
    Context context,
    OnTokenReceivedListener listener
  ) {
    TokenStorageManager tokenStorage = new TokenStorageManager(context);
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

  private void showNotification(
    String title,
    String body,
    String imageUrl,
    String url,
    String pushId
  ) {
    Log.d(TAG, "Mostrando notificación simple");
    Bitmap bigPicture = null;
    if (imageUrl != null && !imageUrl.isEmpty()) {
      bigPicture = getBitmapFromUrl(imageUrl);
    }
    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = "fcm_notifications";
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        "Push Notifications",
        NotificationManager.IMPORTANCE_HIGH
      );
      channel.setVibrationPattern(new long[] { 100, 200, 300 });
      channel.enableVibration(true);
      channel.enableLights(true);
      notificationManager.createNotificationChannel(channel);
    }
    int notificationId = (int) System.currentTimeMillis();
    NotificationCompat.Builder notificationBuilder =
      new NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setAutoCancel(true)
        .setFullScreenIntent(null, true)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    if (bigPicture != null) {
      notificationBuilder.setStyle(
        new NotificationCompat.BigPictureStyle()
          .bigPicture(bigPicture)
          .bigLargeIcon((Bitmap) null)
      );
    }
    Intent actionIntent = new Intent(this, NotificationActionReceiver.class);
    actionIntent.setAction(
      "com.example.a29_app_sg.push_not.NOTIFICATION_ACTION"
    );
    actionIntent.putExtra("url", url);
    actionIntent.putExtra("push_id", pushId);
    actionIntent.putExtra("button_text", "default");
    actionIntent.putExtra("notification_id", String.valueOf(notificationId));
    PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
      this,
      0,
      actionIntent,
      PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );
    notificationBuilder.setContentIntent(actionPendingIntent);
    notificationManager.notify(notificationId, notificationBuilder.build());
  }

  private void showNotificationWithButtons(
    String title,
    String body,
    String imageUrl,
    String pushId,
    String button1Text,
    String button1Url,
    String button2Text,
    String button2Url
  ) {
    Bitmap bigPicture = null;
    if (imageUrl != null && !imageUrl.isEmpty()) {
      bigPicture = getBitmapFromUrl(imageUrl);
    }
    Log.d(TAG, "Mostrando notificación con botones");
    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = "fcm_notifications"; // Cambia esto por el ID
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        "Push Notifications",
        NotificationManager.IMPORTANCE_HIGH
      );
      channel.setVibrationPattern(new long[] { 100, 200, 300 });
      channel.enableVibration(true);
      channel.enableLights(true);
      notificationManager.createNotificationChannel(channel);
    }
    int notificationId = (int) System.currentTimeMillis();
    NotificationCompat.Builder notificationBuilder =
      new NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia esto por tu ícono
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setAutoCancel(true)
        .setFullScreenIntent(null, true)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    if (bigPicture != null) {
      notificationBuilder.setStyle(
        new NotificationCompat.BigPictureStyle()
          .bigPicture(bigPicture)
          .bigLargeIcon((Bitmap) null)
      );
    }
    // Botón 1
    Intent actionIntent1 = new Intent(this, NotificationActionReceiver.class);
    actionIntent1.setAction(
      "com.example.a29_app_sg.push_not.NOTIFICATION_ACTION"
    );
    actionIntent1.putExtra("url", button1Url);
    actionIntent1.putExtra("push_id", pushId);
    actionIntent1.putExtra("button_text", button1Text);
    actionIntent1.putExtra("notification_id", String.valueOf(notificationId));
    PendingIntent actionPendingIntent1 = PendingIntent.getBroadcast(
      this,
      1,
      actionIntent1,
      PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );
    notificationBuilder.addAction(
      R.drawable.ic_launcher_foreground,
      button1Text,
      actionPendingIntent1
    );
    // Botón 2
    if (button2Text != null && button2Url != null) {
      Intent actionIntent2 = new Intent(this, NotificationActionReceiver.class);
      actionIntent2.setAction(
        "com.example.a29_app_sg.push_not.NOTIFICATION_ACTION"
      );
      actionIntent2.putExtra("url", button2Url);
      actionIntent2.putExtra("push_id", pushId);
      actionIntent2.putExtra("button_text", button2Text);
      actionIntent2.putExtra("notification_id", String.valueOf(notificationId));
      PendingIntent actionPendingIntent2 = PendingIntent.getBroadcast(
        this,
        2,
        actionIntent2,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
      );
      notificationBuilder.addAction(
        R.drawable.ic_launcher_foreground,
        button2Text,
        actionPendingIntent2
      );
    }
    notificationManager.notify(notificationId, notificationBuilder.build());
  }

  private Bitmap getBitmapFromUrl(String imageUrl) {
    try {
      java.net.URL url = new java.net.URL(imageUrl);
      java.net.HttpURLConnection connection =
        (java.net.HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      java.io.InputStream input = connection.getInputStream();
      return android.graphics.BitmapFactory.decodeStream(input);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
