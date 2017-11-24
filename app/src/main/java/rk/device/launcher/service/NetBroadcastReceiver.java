package rk.device.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.NetDismissBean;

/**
 * Created by wuliang on 2017/11/24.
 * <p>
 * 监听是否有网络链接
 */

public class NetBroadcastReceiver extends BroadcastReceiver {


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
                        RxBus.getDefault().post(new NetDismissBean(true));
                        Log.i("TAG", "网络连上");
                    }
                } else {
                    RxBus.getDefault().post(new NetDismissBean(false));
                    Log.i("TAG", "网络断开");
                }
            }
        }
    }
}
