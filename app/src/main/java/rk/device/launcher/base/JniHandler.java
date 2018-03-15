package rk.device.launcher.base;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import cvc.CvcHelper;
import cvc.CvcRect;
import cvc.EventUtil;
import mediac.MediacHelper;
import peripherals.FingerHelper;
import peripherals.LedHelper;
import peripherals.MdHelper;
import peripherals.NfcHelper;
import peripherals.NumberpadHelper;
import peripherals.RelayHelper;
import peripherals.Sys;
import rk.device.launcher.crash.CrashUtils;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;

/**
 * Created by wuliang on 2017/11/28.
 * <p>
 * 此处异步调用所有jni的操作，需要耗时
 */
public class JniHandler extends Handler {

    private CvcRect cvcRect = new CvcRect();
    private int[] rectWidth = new int[1];
    private int[] rectHeight = new int[1];
    private int[] possibilityCode = new int[1];
    private int encodedFaceMemLen = 512 * (1 << 10);  /* 512KM */
    private byte[] faces = new byte[encodedFaceMemLen];  /* will be filled with encoded JPEG data */
    private int[] length = new int[1];

    private OnBioAssay onBioAssay;
    private OnEyesCallBack callBack;
    private OnInitListener initListener;

    private int countFace = 1;   //记录返回11的次数 ，超过5次，则隐藏人脸框

    private int cvcStatus = 1;
    private int LedStatus = 1;
    private int NfcStatus = 1;
    private int fingerStatus = -1;//指纹库
    private int MdStatus = 1;   //人体感应

    private boolean isStopCorrect = false;

    private static final int faceThreshold = 0;    //默认设置活体检测真人概率为50则返回人脸

    private static JniHandler jniHandler;

    private long[] carmer01 = new long[1];    //前置摄像头的编号
    private long[] carmer02 = new long[1];    //后置摄像头的编号


    /**
     * 全局单例获取此类对象
     */
    public static JniHandler getInstance() {
        if (jniHandler == null) {
            synchronized (JniHandler.class) {
                if (jniHandler == null) {
                    HandlerThread thread = new HandlerThread("new_thread");
                    thread.start();
                    Looper looper = thread.getLooper();
                    jniHandler = new JniHandler(looper);
                }
            }
        }
        return jniHandler;
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case EventUtil.INIT_JNI:    //初始化所有外设的jni库
                initSuress();
                break;
            case EventUtil.START_CVC:     //初始化校准
                int width = msg.arg1;
                int height = msg.arg2;
                initEyes(width, height);
                break;
            case EventUtil.START_CORRECT:     //开始校准
                startCallPicture();
                break;
            case EventUtil.STOP_CORRECT:    //中断校准
                stopCallPicture();
                break;
            case EventUtil.START_CALIBRATION:    //摄像头校准完成
                eyesFinish();
                break;
            case EventUtil.CVC_DETECTFACE:     //人脸识别
                faceCall();
                break;
            case EventUtil.CVC_LIVINGFACE:     //活体检测
                livingCall();
                break;
            case EventUtil.MEDIA_OPEN:    //开启摄像头
                openCarema();
                break;
            case EventUtil.MEDIA_CLOSE:    //关闭摄像头
                closeCarema();
                break;
            case EventUtil.DEINIT_JNI:
                deInitJni();
                break;
        }
        super.handleMessage(msg);
    }


    /**
     * 初始化外设库，并处理结果
     */
    private void initSuress() {
        if (cvcStatus != 0) {
            cvcStatus = CvcHelper.CVC_init();
        }
        if (LedStatus != 0) {
            LedStatus = LedHelper.PER_ledInit();
        }
        if (MdStatus != 0) {
            MdStatus = MdHelper.PER_mdInit();
        }
        int carema01 = MediacHelper.MEDIAC_init(0, carmer01);
        int carema02 = MediacHelper.MEDIAC_init(1, carmer02);
        Log.i("wuliang", "cvcStatus == " + cvcStatus + "LedStatus == " + LedStatus
                + "MdStatus == " + MdStatus + "carema01 == " + carema01 + "carema02 ==" + carema02);
        int faceSuress = CvcHelper.CVC_setLivingFaceThreshold(faceThreshold);
        int callsuress = NumberpadHelper.PER_numberpadInit();
        int relayStatus = RelayHelper.RelayInit();
        if (SPUtils.getBoolean(Constant.DEVICE_OFF, true)) {
            RelayHelper.RelaySetOn();
        } else {
            RelayHelper.RelaySetOff();
        }
        Log.d("wuliang", "relayStatus == " + relayStatus);
        //init finger
//        if (fingerStatus <= 0) {
//            fingerStatus = FingerHelper.JNIFpInit();
//            LauncherApplication.fingerModuleID = fingerStatus;
//            Log.i("wuliang", "fingerStatus " + fingerStatus);
//            if (fingerStatus > 0) {
//                LauncherApplication.sInitFingerSuccess = 0;
//                LauncherApplication.totalUserCount = FingerHelper.JNIFpGetTotalUser(fingerStatus);
//                LauncherApplication.remainUserCount = FingerHelper.JNIFpGetRemainSpace(fingerStatus);
//            }
//        }
        if (NfcStatus != 0) {
            NfcStatus = NfcHelper.PER_nfcInit();
            Log.i("JniHandler", "NfcStatus " + NfcStatus);
        }
        if (SPUtils.getBoolean(Constant.KEY_LIGNT, false)) {    //判断灯是否是开的，开灯的情况下将补光灯打开
            LedHelper.PER_ledToggle(1);
        }
        if (initListener != null) {
            initListener.initCallBack(cvcStatus, LedStatus, NfcStatus, fingerStatus);
        }
    }

    /**
     * 开启摄像头
     */
    private void openCarema() {
        Log.d("wuliang", "open carema");
        int open = MediacHelper.MEDIAC_open(carmer01[0]);
        if (open == 0) {
            MediacHelper.MEDIAC_setFormat(carmer01[0], 640, 480);
            MediacHelper.MEDIAC_startStreaming(carmer01[0]);
        } else if (open == 19) {    //carema设备节点不存在,设备重启
            new CrashUtils().reboot();
            return;
        }
        int open02 = MediacHelper.MEDIAC_open(carmer02[0]);
        if (open02 == 0) {
            MediacHelper.MEDIAC_setFormat(carmer02[0], 640, 480);
            MediacHelper.MEDIAC_startStreaming(carmer02[0]);
        } else if (open02 == 19) {    //carema设备节点不存在,设备重启
            new CrashUtils().reboot();
        }
    }

    /**
     * 关闭摄像头
     */
    private void closeCarema() {
        Log.d("wuliang", "close carema");
        int stop = MediacHelper.MEDIAC_stopStreaming(carmer01[0]);
        Log.d("wuliang", "close carema" + stop);
        if (stop == 0) {
            MediacHelper.MEDIAC_close(carmer01[0]);
        }
        int stop02 = MediacHelper.MEDIAC_stopStreaming(carmer02[0]);
        if (stop02 == 0) {
            MediacHelper.MEDIAC_close(carmer02[0]);
        }
        System.gc();
    }


    /**
     * 注销所有硬件
     */
    public void deInitJni() {
        CvcHelper.CVC_deinit();
        if (LauncherApplication.fingerModuleID != -1) {
            FingerHelper.JNIFpDeInit(LauncherApplication.fingerModuleID);
        }
        LogUtil.d("wuliang", "fingerStatus == " + fingerStatus);
        LedHelper.PER_ledDeinit();
        MdHelper.PER_mdDeinit();
        NfcHelper.PER_nfcDeinit();
        NumberpadHelper.PER_numberpadDeinit();
        RelayHelper.RelayDeInit();
        MediacHelper.MEDIAC_deinit(carmer01[0]);
        MediacHelper.MEDIAC_deinit(carmer02[0]);
    }


    /**
     * 人脸识别
     */
    private void faceCall() {
        int statusCode = CvcHelper.CVC_detectFace(Camera.CameraInfo.CAMERA_FACING_BACK, cvcRect, rectWidth, rectHeight);
        if (statusCode == 0) {
            if (onBioAssay != null) {
                countFace = 1;
                onBioAssay.setOnBioFace(cvcRect, rectWidth, rectHeight);
            }
        } else if (statusCode == 11) {
            countFace++;
            if (countFace == 30) {    // 连续30次未识别到人脸，则隐藏
                if (onBioAssay != null) {
                    countFace = 1;
                    onBioAssay.setOnBioFace(new CvcRect(), new int[]{0}, new int[]{0});
                }
            }
        }
    }


    /**
     * 活体检测
     */
    private void livingCall() {
        length[0] = encodedFaceMemLen;
        int resultCode = CvcHelper.CVC_determineLivingFace(possibilityCode, faces, length);
        if (0 == resultCode) {
            Log.i("cvc=====possibilitycode", possibilityCode[0] + "");
            if (length[0] > 0) {
                if (possibilityCode[0] >= faceThreshold) {   //50%以上的概率是真人
                    if (onBioAssay != null) {
                        onBioAssay.setOnBioAssay(possibilityCode, faces, length);
                    }
                }
            }
        }
    }


    /**
     * 摄像头校准初始化
     */
    private void initEyes(int width, int height) {
        Log.i("wuliang", "isStopCorrecrt = " + isStopCorrect);
        if (isStopCorrect) {
            if (callBack != null) {
                callBack.initError("请稍等片刻再初始化，程序正在加载中...");
            }
            return;
        }
        int stutas = CvcHelper.CVC_calibratorInit(width, height);
        if (stutas != 0) {
            Log.i("CVC_calibratorInit", "初始化校准失败" + stutas);
            if (callBack != null) {
                callBack.initError("棋盘格设置错误！请重新设置！");
            }
        } else {
            Log.i("CVC_calibratorInit", "初始化校准成功" + stutas);
            if (callBack != null) {
                callBack.initSuress();
            }
        }
    }

    /**
     * 开始收集照片
     */
    private void startCallPicture() {
        Log.d("wuliang", "isStopCorrecrt = " + isStopCorrect);
        //设置完宽高，采集棋盘格
        if (isStopCorrect) {
            Log.i("wuliang", "cvc eyes stop!!");
            isStopCorrect = false;
            return;
        }
        int ret = CvcHelper.CVC_calibratorCollectImage();
        if (ret == 0) {
            Log.i("CVC_calibratorCollect", "识别成功，下一张开始。。。");
            if (callBack != null) {
                callBack.picluerNextSuress();
            }
        } else {
            Log.i("CVC_calibratorCollect", "识别失败，下一张开始。。。");
            if (callBack != null) {
                callBack.picluerNextError();
            }
        }
    }

    /**
     * 停止收集照片
     */
    private void stopCallPicture() {
        isStopCorrect = true;
    }

    /**
     * 摄像头校准完成
     */
    private void eyesFinish() {
        int stas = CvcHelper.CVC_calibratorStereoCalibrate();
        if (stas == 0) {
            Log.i("CVC_calibratorStereo", "校准结束");
            if (callBack != null) {
                callBack.pictureFinnish(true);
            }
        } else {
            if (callBack != null) {
                callBack.pictureFinnish(false);
            }
        }
    }


    private JniHandler(Looper looper) {

        super(looper);
    }


    public void setOnBioAssay(OnBioAssay onBioAssay) {
        this.onBioAssay = onBioAssay;
    }


    public void setEyesCallback(OnEyesCallBack callback) {
        this.callBack = callback;
    }


    public void setOnInitListener(OnInitListener callBack) {
        this.initListener = callBack;
    }


    /**
     * 所有JNI接口初始化接口
     */
    public interface OnInitListener {

        void initCallBack(int cvcStatus, int LedStatus, int NfcStatus, int fingerStatus);
    }


    /**
     * 活体检测返回接口
     */

    public interface OnBioAssay {

        /**
         * 人脸识别返回判断结果
         */
        void setOnBioFace(CvcRect rect, int[] rectWidth, int[] rectHeight);

        /**
         * 活体检测返回判断结果
         */
        void setOnBioAssay(int[] possibilityCode, byte[] faces, int[] lenght);

    }


    /**
     * 棋盘格校准接口
     */
    public interface OnEyesCallBack {


        /**
         * 初始化成功
         */
        void initSuress();

        /**
         * 初始话棋盘格失败
         */
        void initError(String message);

        /**
         * 校准成功，开始下一张
         */
        void picluerNextSuress();

        /**
         * 校准失败，开始下一张
         */
        void picluerNextError();


        /**
         * 校准完成
         */
        void pictureFinnish(boolean isSuress);

    }
}