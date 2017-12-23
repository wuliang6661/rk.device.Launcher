package rk.device.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.WifiHelper;

/**
 * Created by wuliang on 2017/12/8.
 * <p>
 * 监听网络变化
 */

public class NetChangeBroadcastReceiver extends BroadcastReceiver {


    private WifiHelper mWifiHelper;

    private CallBack callBack;


    @Override
    public void onReceive(Context context, Intent intent) {
        mWifiHelper = new WifiHelper(context);
        String action = intent.getAction();
        boolean isScanResultChange = action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        boolean isNetworkStateChange = action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        boolean isWifiNetworkStateChange = action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION);
        boolean isConnectStateChange = action.equals(ConnectivityManager.CONNECTIVITY_ACTION);
        if (isScanResultChange || isNetworkStateChange || isWifiNetworkStateChange) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            setNetworkStutas(info);
        } else if (isConnectStateChange) { // 有线或者无线的连接方式发生了改变
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            setNetworkStutas(info);
        }
    }
	
    /**
     * 判断网络状态并返回结果
     */
    private void setNetworkStutas(NetworkInfo info) {
        if (info == null) {
            if (callBack != null) {
                callBack.onCallMessage(false, 0, 0);
            }
            return;
        }
        // 是有线的连接方式并且处于可用、可连接的状态
        if (info.getType() == ConnectivityManager.TYPE_ETHERNET && NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
            if (callBack != null) {
                callBack.onCallMessage(true, 0, 0);
            }
        }
        //wifi
        else if (info.getType() == ConnectivityManager.TYPE_WIFI && NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
            ScanResult scanResult = mWifiHelper.getConnectedScanResult();
            // 先判断wifi是否可用
            if (scanResult != null && mWifiHelper.checkWifiState()) {
                //判断信号强度，显示对应的指示图标
                if (callBack != null) {
                    callBack.onCallMessage(true, 1, scanResult.level);
                }
            } else { // wifi不可用
                if (callBack != null) {
                    callBack.onCallMessage(false, 0, 0);
                }
            }
        } else { // 剩余的是连接不可用的状态
            if (callBack != null) {
                callBack.onCallMessage(false, 0, 0);
            }
        }
    }


    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    public interface CallBack {


        /**
         * 网络连接的返回状态
         *
         * @param isNoNet         网络是否连接
         * @param WifiorNetStatus 连接是Wifi还是网线   0:网线 1: wifi
         * @param scanLever       wifi信号强度
         */
        void onCallMessage(boolean isNoNet, int WifiorNetStatus, int scanLever);

    }

}
