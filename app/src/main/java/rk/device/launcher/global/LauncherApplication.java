package rk.device.launcher.global;

import android.app.Application;
import android.content.Context;

import rk.device.launcher.utils.Utils;

/**
 * Created by mundane on 2017/11/11 下午3:49
 */

public class LauncherApplication extends Application {
	public static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
		Utils.init(this);
	}

	/**
	 * 获取上下文
	 * @return Context
	 */
	public static Context getContext() {
		return sContext;
	}
}
