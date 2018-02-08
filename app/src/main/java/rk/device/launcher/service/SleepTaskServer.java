package rk.device.launcher.service;

import android.os.Handler;
import android.os.Message;

import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.SPUtils;

/**
 * Created by wuliang on 2017/12/19.
 * <p>
 * 管理待机时间的线程
 */

public class SleepTaskServer extends Handler {


    private static SleepTaskServer sleepTaskServer;

    /**
     * 延时时间
     */
    private long DenyTime = 60 * 1000L;    //默认20秒无操作返回首页


    private SleepTaskServer() {
        super();
//        DenyTime = SPUtils.getLong(Constant.KEY_SLEEP_TIME, DenyTime);
    }


    public static SleepTaskServer getSleepHandler() {
        if (sleepTaskServer == null) {
            synchronized (SleepTaskServer.class) {
                if (sleepTaskServer == null) {
                    sleepTaskServer = new SleepTaskServer();
                }
            }
        }
        return sleepTaskServer;
    }


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 0x11:     //有人点击，重新开始
                startSleepTask();
                break;
            case 0x22:      //已进入休眠
                stopSleepTask();
                break;
            case 0x33:    //休眠已结束,重新开始计时
                startSleepTask();
                break;
            case 0x44:     //重新设置休眠时间
                DenyTime = SPUtils.getLong(Constant.KEY_SLEEP_TIME);
                startSleepTask();
                break;
        }
    }


    /**
     * 开启休眠任务
     */
    private void startSleepTask() {
//        if (DenyTime == -1) {
//            stopSleepTask();
//            return;
//        }
        removeCallbacks(sleepWindowTask);
        postDelayed(sleepWindowTask, DenyTime);
    }

    /**
     * 结束休眠任务
     */
    private void stopSleepTask() {
        removeCallbacks(sleepWindowTask);
    }


    /**
     * 休眠任务
     */
    private Runnable sleepWindowTask = new Runnable() {

        @Override
        public void run() {
//            int[] mdStaus = new int[1];
//            int mdStatus = MdHelper.PER_mdGet(1, mdStaus);
//            if (mdStatus == 0 && mdStaus[0] == 1) {
//                startSleepTask();
//            } else {
//                Intent intent = new Intent(context, SleepActivity.class);
//                context.startActivity(intent);
//            }
            AppManager.getAppManager().goBackMain();
        }
    };
}
