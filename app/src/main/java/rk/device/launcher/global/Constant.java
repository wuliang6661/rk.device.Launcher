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
     * 缓存在本地的初始引导设置顺序
     * <p>
     * sp取值value为int
     */
    String SETTING_NUM = "SETTING_NUM";    //以下为存入值的类型

    int SETTING_TYPE1 = 1;                //下次进入跳入基础设置

    int SETTING_TYPE2 = 2;                //下次进入跳入网络设置

    int SETTING_TYPE3 = 3;                //下次进入跳入门禁设置

    int SETTING_TYPE4 = 4;                //下次进入跳入系统设置

    /**
     * 初始设置是否设置完成
     * <p>
     * sp取值value为boolean
     */
    String IS_FIRST_SETTING = "IS_FIRSTSETTING";
    /**
     * 设备名称（门禁设置 ）
     * <p>
     * sp取值value为String
     */
    String DEVICE_NAME = "DEVICE_NAME";
    /**
     * 关联设备(存储方式为 设备ID_name)
     * <p>
     * sp取值value为String  值为 ID_NAME
     */
    String DEVICE_TYPE = "DEVICE_TYPE";
    /**
     * 语音提示开关
     * <p>
     * sp取值value为boolean
     */
    String DEVICE_MP3 = "DEVICE_MP3";

    /**
     * 待机时间的key值
     */
    String KEY_SLEEP_TIME = "key_sleep_time";

    /**
     * 客户号的key值
     */
    String KEY_CLIENT_CODE = "key_client_code";

    /**
     * 补光灯的key值
     */
    String KEY_LIGNT = "key_light";

    /**
     * IP的key值
     */
    String KEY_IP = "key_ip";

    /**
     * 端口号的key值
     */
    String KEY_PORT = "key_port";

    /**
     * 蓝牙
     */
    String BLUE_TOOTH = "blue_tooth";

    /**
     * 蓝牙名称
     */
    String BLUE_NAME = "blue_name";

    String KEY_ROM = "rom";

    String KEY_APK = "apk";

    String KEY_FIRSTPAGE_CONTENT = "key_firstpage_content";

    String KEY_ADDRESS = "key_address";

    /**
     * RSA加密公钥
     */
    String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClYIdKTRqCXdaYlcc5O+gS3EgI\r" +
            "4RWwu4VmOtYeGlCUvZQZJnYPiTqceyDCgwHUbPgb5Ww3minIdzaRNGvUIk4LSJh9\r" +
            "tMiEeGcQidGL0VOzTHktkGI5I4XrlziWcFjkEbxwtlkK2e7iwc8Srw6Cc1GpYr89\r" +
            "wtBccN1ttDWNQR4zRwIDAQAB\r";

    /**
     * RSA解密私钥
     */
    String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKVgh0pNGoJd1piV\r" +
            "xzk76BLcSAjhFbC7hWY61h4aUJS9lBkmdg+JOpx7IMKDAdRs+BvlbDeaKch3NpE0\r" +
            "a9QiTgtImH20yIR4ZxCJ0YvRU7NMeS2QYjkjheuXOJZwWOQRvHC2WQrZ7uLBzxKv\r" +
            "DoJzUalivz3C0Fxw3W20NY1BHjNHAgMBAAECgYBgNS2/jed0G/8xuUCzDSTPhBLs\r" +
            "B3XE4PKULHpP/OMQBNHZZ8Sa+Sx9yCugvPIdkF8ua2NvXWIdWy0FgEeSm3pmqlXf\r" +
            "dwmYqupwOHxZVmgO3Q+Xi7yAhNAjDLQY9BjO+CXFjzzPDFSy4f2nyMhQYAZ+qd4t\r" +
            "bbjJ2JXRusNdddsJcQJBANiu/RczzpyvuDmW1aV32mcujDJ4949LPbf94XBMR7NW\r" +
            "13uLHRG8hMjfL6HRFoTuzFwt2QRdk2g4WlyRS7kwxIsCQQDDYlbbsnHLvdLZJjFr\r" +
            "RnrKQW1jGDB5kWRlOy3qPe3PZfMeIXTN9aVNr1IwP8ORTYiDd4UwgDDDyEjhZ2uf\r" +
            "iFe1AkAYhBwAnwz2b29BnM5JbXkZiYu1PjiMTvAJvEDgitDl+qJgyQmd8x12+MGT\r" +
            "5mtM2RPoLgvK0aeW+CFJLetKZbknAkBC/gn4JHQ+PqT4Dc9uD+l0RgDCtH2SgVai\r" +
            "GyuAIKHUtgkpMKGDKRH31ABjSMm+nY4MPcPuQFX4G0lZ+AL/3VjtAkBDiQCJVVzl\r" +
            "RUyLuF0eO9+p1Rzvtso1evNqn8YeovHbyRIkkfsec/sBTIM2IfPCU/v2AtYjUYK/\r" +
            "EEV4MTbisRoQ\r";




}
