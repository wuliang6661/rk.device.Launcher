package rk.device.launcher.bean.event;

/**
 * Created by hanbin on 2017/11/24.
 */

public class TimeEvent {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public boolean isUpdateTime;

    public TimeEvent(int year,
                     int month,
                     int day,
                     int hour,
                     int minute, boolean isUpdateTime) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.isUpdateTime = isUpdateTime;
    }

}
