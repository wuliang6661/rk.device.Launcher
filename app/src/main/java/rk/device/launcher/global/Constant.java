package rk.device.launcher.global;

import rk.device.launcher.BuildConfig;

/**
 * Created by mundane on 2017/11/14 下午5:41
 */

public interface Constant {
    String  KEY_INTENT       = "KEY_INTENT";
    String  KEY_BUNDLE       = "KEY_BUNDLE";
    String  KEY_TITLE        = "KEY_TITLE";
    String  KEY_PASSWORD     = "KEY_PASSWORD";
    String  LOG_TAG          = "launcher";
    boolean isDebug          = BuildConfig.DEBUG;

    /**
     * 缓存在本地的初始引导设置顺序
     * <p>
     * sp取值value为int
     */
    String  SETTING_NUM      = "SETTING_NUM";    //以下为存入值的类型

    int     SETTING_TYPE1    = 1;                //下次进入跳入基础设置

    int     SETTING_TYPE2    = 2;                //下次进入跳入网络设置

    int     SETTING_TYPE3    = 3;                //下次进入跳入门禁设置

    int     SETTING_TYPE4    = 4;                //下次进入跳入系统设置
    /**
     * 初始设置是否设置完成
     * <p>
     * sp取值value为boolean
     */
    String  IS_FIRST_SETTING = "IS_FIRSTSETTING";
    /**
     * 客户ID
     */
    String  CLIENT_ID        = "CLIENT_ID";
    /**
     * 设备名称（门禁设置 ）
     * <p>
     * sp取值value为String
     */
    String  DEVICE_NAME      = "DEVICE_NAME";
    /**
     * 关联设备(存储方式为 设备ID_name)
     * <p>
     * sp取值value为int
     */
    String  DEVICE_TYPE      = "DEVICE_TYPE";
    /**
     * 语音提示开关
     * <p>
     * sp取值value为boolean
     */
    String  DEVICE_MP3       = "DEVICE_MP3";

    /**
     * 待机时间的key值
     */
    String  KEY_SLEEP_TIME   = "key_sleep_time";

    /**
     * 客户号的key值
     */
    String  KEY_CLIENT_CODE  = "key_client_code";

    /**
     * 补光灯的key值
     */
    String  KEY_LIGNT        = "key_light";

    /**
     * IP的key值
     */
    String  KEY_IP           = "key_ip";

    /**
     * 端口号的key值
     */
    String  KEY_PORT         = "key_port";

    /**
     * 蓝牙
     */
    String  BLUE_TOOTH       = "blue_tooth";

}
