package rk.device.launcher.global;

import android.app.Application;
import android.content.Context;

import rk.device.launcher.base.utils.STUtils;
import rk.device.launcher.utils.Utils;

/**
 * Created by wuliang on 2017/11/11 下午3:49
 *
 * 程序全局监听
 */

public class LauncherApplication extends Application {
	public static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
		Utils.init(this);
		STUtils.init(this);
	}

	/**
	 * 获取上下文
	 * @return Context
	 */
	public static Context getContext() {
		return sContext;
	}


}
