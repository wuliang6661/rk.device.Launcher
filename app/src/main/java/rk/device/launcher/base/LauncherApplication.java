package rk.device.launcher.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.File;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import peripherals.FingerHelper;
import rk.device.launcher.R;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.STUtils;
import rk.device.launcher.utils.StatSoFiles;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaBack;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;

/**
 * Created by wuliang on 2017/11/11 下午3:49
 * <p>
 * 程序全局监听
 */

public class LauncherApplication extends Application implements CustomActivityOnCrash.EventListener {

    public static Context sContext;

    /**
     * 记录电量数据上报
     */
    public static int sLevel;

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
     * 记录指纹模块是否初始化成功
     *
     * @value -1 失败
     * @value 0 成功
     */
    public static int sInitFingerSuccess = -1;

    private final String DB_FOLDER = "/data/rk_backup/";
    private final String DB_NAME = "rk.db";
    private final String TEMP_DB_NAME = "temp.db";
    private final String DB_JOUR = "rk.db-journal";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    LogUtil.d("数据导入完成");
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.setDefaultErrorActivityDrawable(R.mipmap.ic_launcher);
        CustomActivityOnCrash.setEventListener(this);
        Utils.init(this);
        STUtils.init(this);
        SPUtils.inviSp();
        setDb();
    }


    /**
     * 检测数据库更新
     */
    private void setDb() {
        new CopyFileThread().start();
    }


    private class CopyFileThread extends Thread {
        @Override
        public void run() {
            try {
                File dbFolder = new File(DB_FOLDER);
                if (!dbFolder.exists()) {
                    dbFolder.mkdirs();
                }
                File dbFile = new File(dbFolder, DB_NAME);
                if (dbFile.exists()) {
                    File tempDbFile = new File(dbFolder, TEMP_DB_NAME);
                    FileUtils.setPermission(tempDbFile.getAbsolutePath());
                    FileUtils.copyFile(dbFile, tempDbFile);
                    dbFile.delete();
                    File selfDbFile = new File(dbFolder, DB_NAME);
                    FileUtils.copyFile(tempDbFile, selfDbFile);
                    FileUtils.setPermission(selfDbFile.getAbsolutePath());
                    tempDbFile.delete();
                    File journal = new File(dbFolder, DB_JOUR);
                    if (journal.exists()) {
                        journal.delete();
                    }
                    mHandler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取上下文
     *
     * @return Context
     */
    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onLaunchErrorActivity() {
        SurfaceHolderCaremaFont.stopCarema();
        SurfaceHolderCaremaBack.stopCarema();
        FaceUtils.getInstance().stopFaceFR();
        FingerHelper.JNIFpDeInit();
//        stopService(new Intent(this, SocketService.class));
//        stopService(new Intent(this, VerifyService.class));
    }

    @Override
    public void onRestartAppFromErrorActivity() {

    }

    @Override
    public void onCloseAppFromErrorActivity() {

    }
}
