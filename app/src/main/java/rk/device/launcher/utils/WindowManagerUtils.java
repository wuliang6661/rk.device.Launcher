package rk.device.launcher.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

/**
 * Created by hb on 15/12/8.
 */
public class WindowManagerUtils {
    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取图片宽度
     *
     * @param resources
     * @param viewId
     * @return
     */
    public static int getBitmapWidth(Resources resources, int viewId) {
        return BitmapFactory.decodeResource(resources, viewId).getWidth();
    }
}
