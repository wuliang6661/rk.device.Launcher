package rk.device.launcher.global;

public class Config {

    /**
     * 环境——动态配置项
     */
    public static ProgramMode mode = ProgramMode.PROGRAM_TEST_MODE;

    /**
     * 阿里云OSS AppId and AppKey
     */
    public static String ALIYUNOSS_APP_KEY = "LTAIKLnFkQbkYUg8";
    public static String ALIYUNOSS_APP_SECKET = "hJU0YVcW5rxI9ImNJNG5brlUpvpFiO";

    /** 接口信息 */
    /**
     * 域名
     */
    public static String APP_WEATHER = "";


    static {
        switch (mode) {
            case PRGRAM_PRODUCT_MODE:

                break;
            case PRGRAM_PREPRODUCT_MODE:
                APP_WEATHER = "https://app.roombanker.cn:9092";
                break;
            case PROGRAM_TEST_MODE:
                APP_WEATHER = "http://192.168.10.121:8083";
                break;
            case PROGRAM_DEV_MODE:
                break;
        }
    }

    /**
     * 系统运行环境参数
     */
    public enum ProgramMode {
        /**
         * 线上参数
         */
        PRGRAM_PRODUCT_MODE,
        /**
         * 预发参数
         */
        PRGRAM_PREPRODUCT_MODE,
        /**
         * 测试参数
         */
        PROGRAM_TEST_MODE,
        /**
         * 开发参数
         */
        PROGRAM_DEV_MODE
    }

}
