package rk.device.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.StatusBo;
import rk.device.launcher.bean.event.DestoryEvent;
import rk.device.launcher.bean.event.UpdateConfig;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by hanbin on 2017/9/23.
 * <p>
 * 心跳service
 */

public class SocketService extends Service {

    private static final String TAG = "SocketService";
    private static SocketService mService = null;
    private DeviceUuidFactory deviceUuidFactory = null;
    private int heartTime = 50000;

    MyTimerTask task;

    public static SocketService getInstance() {
        if (mService == null) {
            mService = new SocketService();
        }
        return mService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        if (deviceUuidFactory == null) {
            deviceUuidFactory = new DeviceUuidFactory(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, TAG + " onStartCommand");
        sendDeviceStatusToPlatform();
        return super.onStartCommand(intent, flags, startId);
    }

    Timer timer = new Timer();

    final Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    LogUtil.i(TAG, TAG + " uploadDeviceStatus");
                    JSONObject params = new JSONObject();
                    try {
                        params.put("uuid", deviceUuidFactory.getUuid());
                        params.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
                        params.put("mac", FileUtils.readFile2String("/proc/board_sn", "UTF-8"));
                        params.put("hw_ver", Build.HARDWARE);
                        params.put("version_code", PackageUtils.getCurrentVersionCode());
                        params.put("version_name", PackageUtils.getCurrentVersion());
                        params.put("imsi", PackageUtils.getImsi(LauncherApplication.getContext()));
                        params.put("msisdn", PackageUtils.getMsisdn(LauncherApplication.getContext()));
                        params.put("battery", LauncherApplication.sLevel);
                        params.put("temperature", LauncherApplication.sTemperature);
                        params.put("signal", WifiHelper.obtainWifiInfo(LauncherApplication.getContext()));
                        params.put("card_capacity", 9999);
                        params.put("whitelist_count", 9999);
                        params.put("finger_capacity", LauncherApplication.totalUserCount);
                        params.put("finger_count", LauncherApplication.remainUserCount);
                        params.put("opened", 0);
                        params.put("work_mode", 9999);
                        params.put("power_mode", LauncherApplication.sIsCharge);
                    } catch (JSONException e) {

                    }
                    BaseApiImpl.uploadDeviceStatus(params).subscribe(new Subscriber<StatusBo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(StatusBo statusBo) {

                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void sendDeviceStatusToPlatform() {
        if (task != null) {
            task.cancel();
        }
        task = new MyTimerTask();
        timer.schedule(task, 1000, heartTime);
    }

    private class MyTimerTask extends TimerTask {

        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }


    @Override
    public void onDestroy() {
        LogUtil.e("SocketService onDestory");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DestoryEvent messageEvent) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        handler.removeCallbacksAndMessages(null);
        stopSelf();
    }


    /**
     * 系统配置更新,心跳时间变更
     *
     * @param updateConfig
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateConfig updateConfig) {
        heartTime = SPUtils.getInt(Constant.HEART);
    }


}
