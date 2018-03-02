package rk.device.launcher.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import java.lang.ref.WeakReference;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.crash.CrashHandler;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.service.VerifyService;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.STUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.cache.CacheUtils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaBack;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;
import rk.device.server.service.AppHttpServerService;

/**
 * Created by wuliang on 2017/11/11 下午3:49
 * <p>
 * 程序全局监听
 */

public class LauncherApplication extends Application {

    public static Context sContext;

    /**
     * 记录电量数据上报
     */
    public static int sLevel;

    /**
     * 温度
     */
    public static float sTemperature;

    /**
     * 记录是否在充电
     */
    public static int sIsCharge;

    /**
     * 记录是否在录入指纹
     */
    public static int sIsFingerAdd;

    /**
     * 记录是否在录入NFC卡
     */
    public static int sIsNFCAdd;

    /**
     * 当前指纹模块
     */
    public static int fingerModuleID;

    /**
     * 指纹头总容量
     */
    public static int totalUserCount;

    /**
     * 指纹头剩余容量
     */
    public static int remainUserCount;

    /**
     * 记录指纹模块是否初始化成功
     *
     * @value -1 失败
     * @value 0 成功
     */
    public static int sInitFingerSuccess = -1;

    public static boolean isTcp = false;//是否连接tcp

    private final String DB_NAME = "rk.db";
    private final String TEMP_DB_NAME = "temp.db";
    private final String DB_JOUR = "rk.db-journal";


    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.setDefaultErrorActivityDrawable(R.mipmap.ic_launcher);
        CustomActivityOnCrash.setEventListener(new CrashEventListener(this));
        CrashHandler.getInstance().init(this);
        CacheUtils.init();
        Utils.init(this);
        STUtils.init(this);
        SPUtils.inviSp();
        PackageUtils.getTemperature(this);
//        setDb();
    }


//    /**
//     * 检测数据库更新
//     */
//    private void setDb() {
//        new CopyFileThread().start();
//    }
//
//
//    private class CopyFileThread extends Thread {
//        @Override
//        public void run() {
//            try {
//                File dbFolder = new File(CacheUtils.getBaseCache());
//                if (!dbFolder.exists()) {
//                    dbFolder.mkdirs();
//                }
//                File dbFile = new File(dbFolder, DB_NAME);
//                if (dbFile.exists()) {
//                    File tempDbFile = new File(dbFolder, TEMP_DB_NAME);
//                    FileUtils.setPermission(tempDbFile.getAbsolutePath());
//                    FileUtils.copyFile(dbFile, tempDbFile);
//                    dbFile.delete();
//                    File selfDbFile = new File(dbFolder, DB_NAME);
//                    FileUtils.copyFile(tempDbFile, selfDbFile);
//                    FileUtils.setPermission(selfDbFile.getAbsolutePath());
//                    tempDbFile.delete();
//                    File journal = new File(dbFolder, DB_JOUR);
//                    if (journal.exists()) {
//                        journal.delete();
//                    }
//                    LogUtil.d("数据导入完成");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 获取上下文
     *
     * @return Context
     */
    public static Context getContext() {
        return sContext;
    }


    private static class CrashEventListener implements CustomActivityOnCrash.EventListener {

        WeakReference<LauncherApplication> weakReference;

        CrashEventListener(LauncherApplication application) {
            weakReference = new WeakReference<>(application);
        }

        @Override
        public void onLaunchErrorActivity() {
            LogUtil.d("wuliang", "application destory!!!");
            LauncherApplication application = weakReference.get();
            if (application != null) {
                application.stopService(new Intent(application, SocketService.class));
                application.stopService(new Intent(application, VerifyService.class));
                application.stopService(new Intent(application, AppHttpServerService.class));
            }
            SurfaceHolderCaremaFont.stopCarema();
            SurfaceHolderCaremaBack.stopCarema();
            FaceUtils.getInstance().stopFaceFR();
            deInitJni();
            FaceUtils.getInstance().destory();
        }

        @Override
        public void onRestartAppFromErrorActivity() {

        }

        @Override
        public void onCloseAppFromErrorActivity() {

        }

        /**
         * 反注册所有JNI
         */
        void deInitJni() {
            JniHandler mHandler = JniHandler.getInstance();
            Message msg = Message.obtain();
            msg.what = EventUtil.DEINIT_JNI;
            mHandler.sendMessage(msg);
        }
    }
}
