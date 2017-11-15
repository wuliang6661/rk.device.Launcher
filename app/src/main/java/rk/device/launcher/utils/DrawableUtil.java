package rk.device.launcher.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import rk.device.launcher.widget.onedrawable.OneDrawable;

/**
 * Created by mundane on 2017/11/15 下午2:28
 */

public class DrawableUtil {
	public static void addPressedDrawable(Context context, int res, View view) {
		Drawable bgDrawable = OneDrawable.createBgDrawable(context, res);
		view.setBackground(bgDrawable);
	}
}
