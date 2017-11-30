package rk.device.launcher.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hanbin on 2017/11/27.
 */

public class TimeUtils {
    /**
     * 获取当前小时
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        if (date == null)
            return -1;
        return date.getHours();
    }


    /**
     * 获取当前手机时间
     */
    public static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }

}
