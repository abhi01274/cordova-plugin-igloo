package com.igloo.plugin;

import android.content.Context;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import co.igloohome.ble.lock.BleManager;
import io.reactivex.disposables.Disposable;

public class IglooPlugin extends CordovaPlugin {
    private IglooPlugin iglooPlugin; // replace Object with actual SDK class
    private boolean isInitialized = false;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        try {
            Context context = this.cordova.getActivity().getApplicationContext();
           BleManager bleManager = new BleManager(cordova.getContext());
           bleManager.setDebug(true);

            isInitialized = true;
        } catch (Exception e) {
            isInitialized = false;
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "getInitializationStatus":
                return getInitializationStatus(callbackContext);
            //case "getLockLogs":
            //    return getLockLogs(callbackContext);
            default:
                return false;
        }
    }

    private boolean getInitializationStatus(CallbackContext callbackContext) {
        JSONObject result = new JSONObject();
        try {
            result.put("status", isInitialized ? "initialized" : "not_initialized");
            callbackContext.success(result);
        } catch (JSONException e) {
            callbackContext.error("Failed to get initialization status");
        }
        return true;
    }
}
