package rk.device.launcher.utils;

import java.util.Date;

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
}
