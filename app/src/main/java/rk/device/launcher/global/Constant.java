package rk.device.launcher.global;

import rk.device.launcher.BuildConfig;

/**
 * Created by mundane on 2017/11/14 下午5:41
 */

public interface Constant {
    String KEY_INTENT = "KEY_INTENT";
    String KEY_BUNDLE = "KEY_BUNDLE";
    String KEY_TITLE = "KEY_TITLE";
    String KEY_PASSWORD = "KEY_PASSWORD";
    String LOG_TAG = "launcher";
    boolean isDebug = BuildConfig.DEBUG;

    /**
     * 设备名称（门禁设置）
     * <p>
     * sp取值value为String
     */
    String DEVICE_NAME = "DEVICE_NAME";
    /**
     * 关联设备编号(如需增加设备类型，只需按编号增加即可)
     * 0: 智能门锁
     * 1:智能门禁 (身份证)
     * 2:智能门禁
     * 3:电梯
     * <p>
     * sp取值value为DeviceCorrelateBean
     */
    String DEVICE_TYPE = "DEVICE_TYPE";
    /**
     * 语音提示开关
     * <p>
     * sp取值value为boolean
     */
    String DEVICE_MP3 = "DEVICE_MP3";


}
