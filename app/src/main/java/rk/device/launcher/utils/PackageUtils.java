package rk.device.launcher.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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

    public static String getPageageName(){
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
}
