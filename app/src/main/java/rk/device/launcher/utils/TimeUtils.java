package rk.device.launcher.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        long time = (System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L));
        return getFormatTimeFromTimestamp(time, null);
    }

    /**
     * 根据时间戳获取指定格式的时间，如2011-11-30 08:40
     *
     * @param timestamp 时间戳 单位为毫秒
     * @param format 指定格式 如果为null或空串则使用默认格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getFormatTimeFromTimestamp(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        if (format == null || format.trim().equals("")) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int year = Integer.valueOf(sdf.format(new Date(timestamp)).substring(0, 4));
            System.out.println("currentYear: " + currentYear);
            System.out.println("year: " + year);
        } else {
            sdf.applyPattern(format);
        }
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 将时间字符串转为时间戳
     * <p>
     * time格式为pattern
     * </p>
     *
     * @param time 时间字符串
     * @return 毫秒时间戳
     */
    public static long string2Millis(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(time)
                    .getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 将字符串转为指定格式的int
     */
    public static int stringToFormat(String time, String format) {
        try {
            SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                    Locale.getDefault());
            Date date = SimpleDateFormat.parse(time);
            SimpleDateFormat.applyPattern(format);
            return Integer.parseInt(SimpleDateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取当前时间戳
     * 
     * @return
     */
    public static int getTimeStamp() {
        return (int) (System.currentTimeMillis() / 1000l);
    }

}
