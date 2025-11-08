package com.example.a29_app_sg.push_not;

import com.getcapacitor.Plugin;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.JSObject;

@CapacitorPlugin(name = "PushPlugin")
public class PushPlugin extends Plugin {

    @PluginMethod
    public void getToken(PluginCall call) {
        TokenStorageManager manager = new TokenStorageManager(getContext());
        String token = manager.getToken();
        if (token != null) {
            JSObject ret = new JSObject();
            ret.put("token", token);
            call.resolve(ret);
        } else {
            call.reject("No token found");
        }
    }
}