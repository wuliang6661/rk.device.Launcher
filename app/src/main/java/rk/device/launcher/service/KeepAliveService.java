package rk.device.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import cn.aidl.IMyAidlInterface;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.Utils;

/**
 * Created by wuliang on 2018/3/22.
 * <p>
 * 运行在新的进程中，做一个保活服务
 */

public class KeepAliveService extends Service {


    private int minsonds = 0;        //30秒主进程无响应


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private IBinder mBinder = new IMyAidlInterface.Stub() {

        @Override
        public void sendService(int anInt) throws RemoteException {
            minsonds = 0;
        }
    };


    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.e("wuliang", "解除绑定");
        return super.onUnbind(intent);
    }


    /**
     * 查询主线程是否死亡,如果超过30秒无响应，则重启机器
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                minsonds++;
                if (minsonds == 30) {
                    reboot();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });


    /**
     * 重启设备
     */
    public void reboot() {
        Intent intent2 = new Intent(Intent.ACTION_REBOOT);
        intent2.putExtra("nowait", 1);
        intent2.putExtra("interval", 1);
        intent2.putExtra("window", 0);
        Utils.getContext().sendBroadcast(intent2);
    }

}
