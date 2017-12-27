package rk.device.launcher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import rk.device.launcher.db.entity.DaoMaster;


/**
 * @author : mundane
 * @time : 2017/4/14 11:43
 * @description :
 * @file : MyOpenHelper.java
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {
	public MyOpenHelper(Context context, String name) {
		super(context, name);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
	}
}
