package rk.device.launcher.crash;

import android.content.Intent;
import android.widget.Toast;

import peripherals.LedHelper;
import peripherals.MdHelper;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.Utils;


/**
 * Created by wuliang on 2018/2/28.
 * <p>
 * 日志存储，及处理修复
 */

public class CrashUtils {

    /**
     * 摄像头崩溃异常的次数,如果连续5次重启没用，则报摄像头已损坏
     */
    public static final String CAREMA_TIME = "CAREMA_TIME";


    /**
     * 处理carema的异常
     * <p>
     * return true则说明摄像头已损坏
     */
    public boolean caremaCrash() {
        int caremaTime = SPUtils.getInt(CAREMA_TIME, 0);
        int nowCrash = caremaTime++;   //崩溃次数+1
        if (nowCrash > 5) {
            return true;
        }
        SPUtils.putInt(CAREMA_TIME, nowCrash);
        reboot();
        return false;
    }


    /**
     * 处理人体红外异常处理
     */
    public boolean MdCrash(int status) {
        if (status == 0) {
            return true;
        }
        MdHelper.PER_mdDeinit();          //去初始化，重新初始化
        if (MdHelper.PER_mdInit() == 0) {
            return true;
        }
        Toast.makeText(Utils.getContext(), "人体红外异常！正在重启设备...", Toast.LENGTH_LONG).show();
        reboot();
        return false;
    }


    /**
     * 补光灯异常处理
     */
    public boolean LedCrash(int status, int type) {
        if (status == 0) {
            return true;
        }
        LedHelper.PER_ledDeinit();          //去初始化，重新初始化
        if (LedHelper.PER_ledInit() == 0) {
            LedHelper.PER_ledToggle(type);
            return true;
        }
        Toast.makeText(Utils.getContext(), "补光灯异常！正在重启设备...", Toast.LENGTH_LONG).show();
        reboot();
        return false;
    }


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