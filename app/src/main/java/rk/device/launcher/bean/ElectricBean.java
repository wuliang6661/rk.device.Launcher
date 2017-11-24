package rk.device.launcher.bean;

import java.io.Serializable;

/**
 * Created by wuliang on 2017/11/24.
 * <p>
 * 电量bean
 */

public class ElectricBean implements Serializable {

    private int levelPercent;    //电量百分比

    /**
     * 电池状态
     * 1. BatteryManager.BATTERY_STATUS_CHARGING    充电中
     * 2.BatteryManager.BATTERY_STATUS_NOT_CHARGING   未充电
     * 3.BatteryManager.BATTERY_STATUS_FULL         充电完成
     * 4.BatteryManager.BATTERY_STATUS_DISCHARGING   放电中
     */
    private int status;


    public ElectricBean(int levelPercent, int status) {
        this.levelPercent = levelPercent;
        this.status = status;
    }


    public int getLevelPercent() {
        return levelPercent;
    }

    public void setLevelPercent(int levelPercent) {
        this.levelPercent = levelPercent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
