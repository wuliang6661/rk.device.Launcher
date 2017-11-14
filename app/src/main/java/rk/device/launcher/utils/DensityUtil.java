package rk.device.launcher.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by mundane on 2017/11/13 下午4:52
 */

public class DensityUtil {
	public static int px2dp(float pxValue) {
		final float density = Resources.getSystem().getDisplayMetrics().density;
		return (int) (pxValue / density + 0.5f);
	}

	public static int dp2px(float dpValue) {
		DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
		float density = displayMetrics.density;
		return (int) (dpValue * density + 0.5f);
	}

}
