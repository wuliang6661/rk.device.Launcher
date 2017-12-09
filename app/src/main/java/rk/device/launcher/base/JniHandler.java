package rk.device.launcher.base;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import cvc.CvcHelper;
import cvc.CvcRect;
import cvc.EventUtil;
import peripherals.LedHelper;
import peripherals.MdHelper;
import peripherals.NfcHelper;

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
    private int MdStatus = 1;
    private int NfcStatus = 1;

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
            case EventUtil.START_CALIBRATION:    //摄像头校准完成
                eyesFinish();
                break;
            case EventUtil.CVC_DETECTFACE:     //人脸识别
                faceCall();
                break;
            case EventUtil.CVC_LIVINGFACE:     //活体检测
                livingCall();
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
        if (NfcStatus != 0) {
            NfcStatus = NfcHelper.PER_nfcInit();
        }
        Log.i("wuliang", "cvcStatus == " + cvcStatus + "LedStatus == " + LedStatus + "MdStatus == "+ MdStatus + "NfcStatus == " + NfcStatus) ;
        if (cvcStatus == 0 && LedStatus == 0 && NfcStatus == 0 && MdStatus == 0) {
            Log.i("wuliang", "all device init surecc!!!!");
        }
        if (initListener != null) {
            initListener.initCallBack(cvcStatus, LedStatus, MdStatus, NfcStatus);
        }
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
            if (possibilityCode[0] >= 50) {   //50%以上的概率是真人
                if (onBioAssay != null) {
                    onBioAssay.setOnBioAssay(possibilityCode, faces, length);
                }
            }
        }
    }


    /**
     * 摄像头校准初始化
     */
    private void initEyes(int width, int height) {
        int stutas = CvcHelper.CVC_calibratorInit(width, height);
        if (stutas != 0) {
            Log.i("CVC_calibratorInit", "初始化校准失败" + stutas);
            if (callBack != null) {
                callBack.initError();
            }
        } else {
            if (callBack != null) {
                callBack.initSuress();
            }
        }
    }

    /**
     * 开始收集照片
     */
    private void startCallPicture() {
        //设置完宽高，采集棋盘格
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
     * 摄像头校准完成
     */
    private void eyesFinish() {
        int stas = CvcHelper.CVC_calibratorStereoCalibrate();
        if (stas == 0) {
            Log.i("CVC_calibratorStereo", "校准结束");
            if (callBack != null) {
                callBack.pictureFinnish();
            }
        }
    }


    public JniHandler(Looper looper) {
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

        void initCallBack(int cvcStatus, int LedStatus, int MdStatus, int NfcStatus);
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
        void initError();

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
        void pictureFinnish();
    }
}