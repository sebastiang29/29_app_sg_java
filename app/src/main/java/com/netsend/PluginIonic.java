/* package com.netsend;

import com.getcapacitor.Plugin;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.JSObject;

@CapacitorPlugin(name = "PluginIonic")
public class PluginIonic extends Plugin {

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
} */