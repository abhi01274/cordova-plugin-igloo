package co.igloohome.plugin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import co.igloohome.ble.lock.BleManager;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class IglooPlugin extends CordovaPlugin {

    private CallbackContext callbackContext;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private ArrayList<Disposable> disposables = new ArrayList<>();
    private static final String SERVER_API_KEY = ""; // Set your API key

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        switch (action) {
            case "init":
                initSDK();
                return true;

            default:
                return false;
        }
    }

    private void initSDK() {
        if (SERVER_API_KEY.isEmpty()) {
            sendError("SERVER_API_KEY is not set.");
            return;
        }

        Timber.plant(new Timber.DebugTree());
        Timber.d("Starting SDK initialization.");

        String[] permissionsRequired;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsRequired = new String[]{
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            };
        } else {
            permissionsRequired = new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION
            };
        }

        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        for (String permission : permissionsRequired) {
            if (cordova.getContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                Timber.e(permission + " not granted.");
                permissionsNotGranted.add(permission);
            }
        }

        if (!permissionsNotGranted.isEmpty()) {
            sendLog("Some permissions are not granted: " + permissionsNotGranted.toString());
            // Note: Can't request permissions directly from a plugin without UI
            sendError("Permissions missing: " + permissionsNotGranted.toString());
            return;
        }

        // Initialize the BLE SDK
        BleManager bleManager = new BleManager(cordova.getContext());
        bleManager.setDebug(true);

        sendLog("Igloo SDK initialized successfully.");
    }

    private void sendLog(String message) {
        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, message);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        }
    }

    private void sendError(String message) {
        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, message);
            result.setKeepCallback(false);
            callbackContext.sendPluginResult(result);
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        for (Disposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
    }
}
