package com.example.a29_app_sg.push_not;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.widget.Toast;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getStringExtra("url");
        String pushId = intent.getStringExtra("push_id");
        String button1Text = intent.getStringExtra("button1_text");
        if (url != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        }
        if (pushId != null && button1Text != null) {
            sendHttpRequest(pushId, button1Text);
        }
        // Muestra un Toast como confirmación
        //Toast.makeText(context, "Abriendo: " + url, Toast.LENGTH_SHORT).show();
    }

    private void sendHttpRequest(String pushId, String buttonText) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL("https://your-server.com/track_click"); // Cambia esto por tu URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            String jsonInputString = "{\"push_id\": \"" + pushId + "\", \"button_text\": \"" + buttonText + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            //int code = conn.getResponseCode();
            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

    }
}