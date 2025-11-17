/* package com.netsend;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class PluginReact extends ReactContextBaseJavaModule {

    public PluginReact(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "PushModule";
    }
    
    @ReactMethod
    public void getToken(Promise promise) {
      TokenStorageManager manager = new TokenStorageManager(getReactApplicationContext());
      String token = manager.getToken();
      if (token != null) {
        promise.resolve(token);
      } else {
        promise.reject("No token found");
      }
    }
}
 */