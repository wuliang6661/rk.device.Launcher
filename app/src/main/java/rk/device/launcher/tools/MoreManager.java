package rk.device.launcher.tools;

import com.inuker.bluetooth.library.BluetoothClient;

import rk.device.launcher.global.LauncherApplication;

/**
 * Created by hb on 16/6/29.
 */
public class MoreManager {
    private static BluetoothClient mClient;


    public static BluetoothClient getBluetoothClient() {
        if (mClient == null) {
            synchronized (MoreManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(LauncherApplication.getContext());
                }
            }
        }
        return mClient;
    }

}
