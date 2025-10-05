package com.example.myapp; //cambia con el nombre de tu paquete

import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
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
        } else {

        }
    }

    private void showNotificationWithButtons(String title, String body, String pushId, String button1Text, String button1Url, String button2Text, String button2Url) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "my_channel_id"; // Cambia esto por el ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
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
        PendingIntent actionPendingIntent1 = PendingIntent.getBroadcast(this, 1, actionIntent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
            PendingIntent actionPendingIntent2 = PendingIntent.getBroadcast(this, 2, actionIntent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            notificationBuilder.addAction(
                R.drawable.ic_launcher_foreground, // Cambia esto por el ícono del botón
                button2Text,
                actionPendingIntent2
            );
        }
        notificationManager.notify(1, notificationBuilder.build());
    }
}