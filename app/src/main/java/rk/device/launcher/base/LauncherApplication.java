package rk.device.launcher.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.util.List;

import rk.device.launcher.crash.CrashHandler;
import rk.device.launcher.db.MyOpenHelper;
import rk.device.launcher.db.entity.DaoMaster;
import rk.device.launcher.db.entity.DaoSession;
import rk.device.launcher.db.entity.Empty;
import rk.device.launcher.db.entity.EmptyDao;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.STUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.cache.CacheUtils;

/**
 * Created by wuliang on 2017/11/11 下午3:49
 * <p>
 * 程序全局监听
 */

public class LauncherApplication extends Application {
    private static final String TAG = "LauncherApplication";

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
     * -3 指纹模块未启动，或者没有指纹模块
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

    //    private final String DB_NAME = "rk.db";
//    private final String TEMP_DB_NAME = "temp.db";
//    private final String DB_JOUR = "rk.db-journal";
    private static final String DATABASE_NAME = CacheUtils.DB_PATH;
    private static DaoSession sDaoSession;

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        CrashHandler.getInstance().init(this);
        CacheUtils.init();
        Utils.init(this);
        STUtils.init(this);
        SPUtils.inviSp();
        PackageUtils.getTemperature(this);
        createDaoSession();
        createDbFileAndSetPermission();
    }


    private void createDaoSession() {
        MyOpenHelper helper = new MyOpenHelper(this, DATABASE_NAME);
        Database db = helper.getWritableDb();
        sDaoSession = new DaoMaster(db).newSession();
    }


    private void createDbFileAndSetPermission() {
        DaoSession daoSession = getDaoSession();
        EmptyDao emptyDao = daoSession.getEmptyDao();
        List<Empty> emptyList = emptyDao.loadAll();
        if (emptyList.isEmpty()) {
            emptyDao.insert(new Empty());
        } else {
            emptyDao.deleteAll();
            emptyDao.insert(new Empty());
        }
        File dbFile = new File("/data/rk_backup/app_cache/rk.db");
        if (dbFile.exists()) {
            LogUtil.d(TAG, "数据库文件已创建");
        } else {
            LogUtil.e(TAG, "数据库文件未创建");
        }
        FileUtils.setDbFilePermission();
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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
