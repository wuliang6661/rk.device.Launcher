package rk.device.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.bean.NetDismissBO;

/**
 * Created by wuliang on 2017/11/24.
 * <p>
 * 监听是否有网络链接
 */

public class NetBroadcastReceiver extends BroadcastReceiver {


    private OnNetConnectListener listener;


    @Override
    public void onReceive(Context context, Intent intent) {
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        if (listener != null) {
                            listener.isConnect(true);
                        }
						RxBus.getDefault().post(new NetDismissBO(true));
                        Log.i("TAG", "网络连上");
                    }
                } else {
                    if (listener != null) {
                        listener.isConnect(false);
                    }
					RxBus.getDefault().post(new NetDismissBO(false));
                    Log.i("TAG", "网络断开");
                }
            } else {
                if (listener != null) {
                    listener.isConnect(false);
                }
				RxBus.getDefault().post(new NetDismissBO(false));
                Log.i("TAG", "网络断开");
            }
        }
    }


    public void setNetConnectListener(OnNetConnectListener listener) {
        this.listener = listener;
    }


    public interface OnNetConnectListener {

        void isConnect(boolean isConcent);

    }

}
