package rk.device.launcher.global;

import android.app.Application;
import android.content.Context;

/**
 * Created by mundane on 2017/11/11 下午3:49
 */

public class AppApplication extends Application {
	public static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
	}

	/**
	 * 获取上下文
	 * @return Context
	 */
	public static Context getContext() {
		return sContext;
	}
}
