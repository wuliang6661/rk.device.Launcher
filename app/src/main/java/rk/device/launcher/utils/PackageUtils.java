package rk.device.launcher.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.base.LauncherApplication;

/**
 * Created by hanbin on 2017/7/15.
 */

public class PackageUtils {
    /**
     * 获取系统版本号
     *
     * @return
     */
    public static int getCurrentVersionCode() {
        int curVersionCode = 0;
        try {
            PackageInfo info = LauncherApplication.getContext().getPackageManager()
                    .getPackageInfo(LauncherApplication.getContext().getPackageName(), 0);
            curVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return curVersionCode;
    }

    /**
     * 获取app当前版本
     * 
     * @return
     */
    public static String getCurrentVersion() {
        String curVersion = "";
        try {
            PackageInfo info = LauncherApplication.getContext().getPackageManager()
                    .getPackageInfo(LauncherApplication.getContext().getPackageName(), 0);
            curVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return curVersion;
    }

    public static String getPageageName() {
        String packageName = "";
        try {
            PackageInfo info = LauncherApplication.getContext().getPackageManager()
                    .getPackageInfo(LauncherApplication.getContext().getPackageName(), 0);
            packageName = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return packageName;
    }

    /**
     * 获取当前应用程序的包名
     *
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getPackageName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 判断Service是否已经启动
     *
     * @param activity
     * @param className
     * @return
     */
    public static boolean isWorked(Activity activity, String className) {
        ActivityManager myManager = (ActivityManager) activity
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(500);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 手机IMSI号
     *
     * @param context
     * @return
     */
    public static String getImsi(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String _imsi = tm.getSubscriberId();
        if (_imsi != null && !_imsi.equals("")) {
            return _imsi;
        }
        return "未知";
    }

    /**
     * 手机Msisdn号
     * 
     * @param context
     * @return
     */
    public static String getMsisdn(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String tel = tm.getLine1Number();//取出MSISDN，很可能为空
        if (tel != null && !tel.equals("")) {
            return tel;
        }
        return "未知";
    }

    private static Sensor        mTempSensor = null;
    private static SensorManager sm          = null;

    public static void getTemperature(Context context) {
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : allSensors) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                if (s.getStringType().toUpperCase().indexOf("TEMP") > 0) {
                    // 可以看到，这里将包含有TEMP关键字的sensor付给了变量mTempSensor
                    // 而这个mTempSensor 就是我们需要的温度传感器
                    mTempSensor = s;
                }
            }
        }
        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    if (event.sensor.getStringType().toUpperCase().indexOf("TEMP") > 0) {
                    /* 温度传感器返回当前的温度，单位是摄氏度（°C）。 */
                        LauncherApplication.sTemperature = event.values[0];
                        sm.unregisterListener(this, mTempSensor);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        }, mTempSensor, SensorManager.SENSOR_DELAY_GAME);
    }


}
