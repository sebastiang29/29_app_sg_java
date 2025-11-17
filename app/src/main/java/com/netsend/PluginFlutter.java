/* package com.netsend;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.content.Context;

public class PluginFlutter implements FlutterPlugin, MethodCallHandler {
    private MethodChannel channel;
    private Context context;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "plugin_flutter");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getToken")) {
            TokenStorageManager manager = new TokenStorageManager(context);
            String token = manager.getToken();
            if (token != null) {
                result.success(token);
            } else {
                result.error("NO_TOKEN", "No token found", null);
            }
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
} */