package rk.device.launcher.utils;

import android.text.TextUtils;
import android.util.Log;

import rk.device.launcher.global.Constant;


public class LogUtil {


	public static void d(String text) {
		d(Constant.LOG_TAG, text);
	}

	public static void i(String text) {
		i(Constant.LOG_TAG, text);
	}

	public static void e(String text) {
		e(Constant.LOG_TAG, text);
	}

	private static final int DEFAULT_SUBSTRING_LENGTH = 3000;

	public static void d(String tag, String text) {
		if (!Constant.isDebug) {
			return;
		}
		if ((TextUtils.isEmpty(text))) {
			Log.e(tag, "log输出内容为空");
		} else if (text.length() <= DEFAULT_SUBSTRING_LENGTH) {
			Log.d(tag, text);
		} else {

			for (int i = 0; i < text.length(); i += 3000) {
				if (i + 3000 < text.length()) {
					//0-3000, 3000-6000
					Log.d(tag, text.substring(i, i + 3000));
				} else {
					Log.d(tag, text.substring(i, text.length()));
				}
			}
		}
	}

	public static void i(String tag, String text) {
		if (!Constant.isDebug) {
			return;
		}
		if ((TextUtils.isEmpty(text))) {
			Log.e(tag, "log输出内容为空");
		} else if (text.length() <= DEFAULT_SUBSTRING_LENGTH) {
			Log.i(Constant.LOG_TAG, text);
		} else {

			for (int i = 0; i < text.length(); i += 3000) {
				if (i + 3000 < text.length()) {
					//0-3000, 3000-6000
					Log.i(tag, text.substring(i, i + 3000));
				} else {
					Log.i(tag, text.substring(i, text.length()));
				}
			}
		}
	}

	public static void e(String tag, String text) {
		if (!Constant.isDebug) {
			return;
		}
		if ((TextUtils.isEmpty(text))) {
			Log.e(tag, "log输出内容为空");
		} else if (text.length() <= DEFAULT_SUBSTRING_LENGTH) {
			Log.e(tag, text);
		} else {

			for (int i = 0; i < text.length(); i += 3000) {
				if (i + 3000 < text.length()) {
					//0-3000, 3000-6000
					Log.e(tag, text.substring(i, i + 3000));
				} else {
					Log.e(tag, text.substring(i, text.length()));
				}
			}
		}
	}



}
