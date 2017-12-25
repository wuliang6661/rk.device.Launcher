package rk.device.launcher.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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
}
