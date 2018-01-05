package rk.device.launcher.utils;

import java.text.ParseException;
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

    /**
     * 获取30天后的时间
     */
    public static String getTridTime() {
        long time = new Date().getTime() + (30 * 24 * 60 * 60 * 1000);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(time);
    }


    /**
     * 将时间字符串转为时间戳
     * <p>time格式为pattern</p>
     *
     * @param time 时间字符串
     * @return 毫秒时间戳
     */
    public static long string2Millis(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
