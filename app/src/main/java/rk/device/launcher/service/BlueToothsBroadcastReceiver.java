package rk.device.launcher.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wuliang on 2017/11/30.
 * <p>
 * 监听蓝牙状态改变
 */

public class BlueToothsBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG = "BlueToothsBroadcastRece";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //获取蓝牙设备实例【如果无设备链接会返回null，如果在无实例的状态下调用了实例的方法，会报空指针异常】
        //主要与蓝牙设备有关系
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Log.i(TAG, "监听蓝牙变化");
        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.i(TAG, "蓝牙设备:" + device.getName() + "已链接");
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                Log.i(TAG, "蓝牙设备:" + device.getName() + "断开链接");
                break;
            //上面的两个链接监听，其实也可以BluetoothAdapter实现，修改状态码即可
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG, "蓝牙关闭");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "蓝牙开启");
                        break;
                }
                break;
            default:
                break;
        }
    }
}