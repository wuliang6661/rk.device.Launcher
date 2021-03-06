package cvc;

public class EventUtil {

    /**
     * 初始化所有jni库
     */
    public static final int INIT_JNI = 0;


    /**
     * 初始话摄像头校验行和列
     */
    public static final int START_CVC = 1;

    /**
     * 校验
     */
    public static final int START_CORRECT = 2;

    /**
     * 中断校验
     */
    public static final int STOP_CORRECT = 6;


    /**
     * 摄像头校准完成
     */

    public static final int START_CALIBRATION = 3;


    /**
     * 人脸检测
     */

    public static final int CVC_DETECTFACE = 4;
    /**
     * 活体检测
     */
    public static final int CVC_LIVINGFACE = 5;

    /**
     * 开启摄像头
     */
    public static final int MEDIA_OPEN = 7;

    /**
     * 关闭摄像头
     */
    public static final int MEDIA_CLOSE = 8;

    /**
     * 注销所有jni
     */
    public static final int DEINIT_JNI = 9;

}
