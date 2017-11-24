package rk.device.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.ElectricBean;
import rk.device.launcher.utils.LogUtil;

/**
 * Created by wuliang on 2017/11/24.
 * <p>
 * 电池电量监听
 */

public class ElectricBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        int levelPercent = (int) (level * 100f / scale);
        LogUtil.d("电池电量百分比 = " + levelPercent);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_HEALTH_UNKNOWN);
        RxBus.getDefault().post(new ElectricBean(levelPercent, status));
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                LogUtil.d("充电中");
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                LogUtil.d("未充电");
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                LogUtil.d("充电完成");
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                LogUtil.d("放电中");
                break;
        }
    }
}