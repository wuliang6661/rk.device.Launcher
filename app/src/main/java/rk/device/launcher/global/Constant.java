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
    String SETTING_NUM = "SETTING_NUM";          //以下为存入值的类型

    int SETTING_TYPE1 = 1;                      //下次网络设置

    int SETTING_TYPE2 = 2;                      //下次激活设置

    int SETTING_TYPE3 = 3;                      //下次设置管理员密码

    int SETTING_TYPE4 = 4;                      //下次门禁设置

    int SETTING_TYPE5 = 5;                      //下次系统设置

    int SETTING_TYPE6 = 6;                      //下次基础设置

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
     * sp取值value为String 值为 ID_NAME
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
     * 继电器的常开还是常闭
     */
    String DEVICE_OFF = "DEVICE_OFF";

    /**
     * 继电器开关时长
     */
    String DEVICE_TIME = "DEVICE_TIME";

    /**
     * 是否自动更新时间
     */
    String UPDATE_TIME = "UPDATE_TIME";

    /**
     * 心跳间隔
     */
    String HEART = "heartbeatInterval";

    /**
     * 物管IP
     */
    String MANAGER_IP = "managerIpAddr";

    /**
     * 物管端口
     */
    String MANAGER_PORT = "managerPort";

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

    /********* DataBase Operation Feedback Code *********/
    int UPDATE_SUCCESS = 1000;                  //更新用户信息成功
    int NULL_NAME = -1001;                  //用户名称为空
    int NULL_POPEDOMTYPE = -1002;                  //权限类型为空
    int NULL_UNIQUEID = -1003;                  //唯一标识为空
    int NOT_EXIST = -1004;

    /********* User Type Code **********/
    int USER_TYPE_OPEN_ONLY = 1;                    // 1:  开门权限，只能开门
    int USER_TYPE_PATROL_ONLY = 2;                    // 2:  巡更权限，向服务器发送一条上报消息，代表已巡更
    int USER_TYPE_ADMINISTRATOR = 3;                    // 3:  管理员权限，可以开门、巡更、或进入设置页面更改设置

    String IS_FIRST_OPEN_APP = "is_first_open_app";

    /**
     * access_token保存的key值，鉴权Token
     */
    String ACCENT_TOKEN = "access_token";

    /**
     * 一体机验资平台访问的Token
     */
    String GRANT_TOKEN = "grant_token";
    String GRANT_TIME = "grant_time";


    /*************** status //1：正常，2：待添加，3：待更新，4：待删除 *******************/

    int NORMAL = 1;
    int TO_BE_ADD = 2;
    int TO_BE_UPDATE = 3;
    int TO_BE_DELETE = 4;


}
