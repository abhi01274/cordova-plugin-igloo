package com.yourcompany.iglooaccess;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

import com.igloohome.sdk.IglooAccessClient;

public class IglooAccessPlugin extends CordovaPlugin {

    private IglooAccessClient client;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("initialize")) {
            String clientId = args.getString(0);
            String clientSecret = args.getString(1);
            client = new IglooAccessClient.Builder(cordova.getActivity().getApplicationContext())
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
            callbackContext.success("SDK Initialized");
            return true;
        } else if (action.equals("unlock")) {
            String lockId = args.getString(0);
            client.getLock(lockId).unlock(result -> {
                if (result.isSuccess()) {
                    callbackContext.success("Unlock successful");
                } else {
                    callbackContext.error("Unlock failed: " + result.getError().toString());
                }
            });
            return true;
        }
        return false;
    }
}
