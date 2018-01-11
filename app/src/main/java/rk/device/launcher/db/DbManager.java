package rk.device.launcher.db;

import org.greenrobot.greendao.database.Database;

import rk.device.launcher.db.entity.DaoMaster;
import rk.device.launcher.db.entity.DaoSession;
import rk.device.launcher.db.entity.RecordDao;
import rk.device.launcher.db.entity.UserDao;
import rk.device.launcher.utils.CommonUtils;


/**
 * @author : mundane
 * @time : 2017/4/14 13:12
 * @description :
 * @file : DbManager.java
 */

public class DbManager {

	private static final String DB_NAME = "rk.db";
	private static final String DB_PASSWORD = "793478MUDd97fdUjl2";
	private UserDao mUserDao;

	private static class SingletonHolder {
		private static final DbManager INSTANCE = new DbManager();
	}

	private DbManager() {
//		SQLiteDatabase.loadLibs(CommonUtils.getContext());
//		File databaseFile = CommonUtils.getContext().getDatabasePath(DB_NAME);
//		databaseFile.mkdirs();
//		databaseFile.delete();
		mUserDao = getUserDao();
	}

	public UserDao getUserDao() {
		return getDaoSession().getUserDao();
	}

    public RecordDao getRecordDao() {
        return getDaoSession().getRecordDao();
    }


    private DaoSession getDaoSession() {
        MyOpenHelper helper = new MyOpenHelper(CommonUtils.getContext(), DB_NAME);
//		Database db = helper.getEncryptedWritableDb(DB_PASSWORD);
        Database db = helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    private UserDao getUserDao1() {
		return mUserDao;
	}

	public static DbManager getInstance() {
		return SingletonHolder.INSTANCE;
	}


}
