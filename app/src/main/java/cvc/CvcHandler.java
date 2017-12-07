package cvc;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by hanbin on 2017/11/28.
 * <p>
 * 此处异步调用所有jni的操作，需要耗时
 */
public class CvcHandler extends Handler {

    private int[] rectWidth = new int[1];
    private int[] rectHeight = new int[1];
    private int[] possibilityCode = new int[1];
    private int encodedFaceMemLen = 512 * (1 << 10);  /* 512KM */
    private byte[] faces = new byte[encodedFaceMemLen];  /* will be filled with encoded JPEG data */
    private int[] length = new int[1];

    private OnBioAssay onBioAssay;
    private OnEyesCallBack callBack;

    private int countFace = 1;   //记录返回11的次数 ，超过5次，则隐藏人脸框


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case EventUtil.START_CVC:     //初始化校准
                int width = msg.arg1;
                int height = msg.arg2;
                int stutas = CvcHelper.CVC_calibratorInit(width, height);
                if (stutas != 0) {
                    Log.i("CVC_calibratorInit", "初始化校准失败");
                    if (callBack != null) {
                        callBack.initError();
                    }
                } else {
                    if (callBack != null) {
                        callBack.initSuress();
                    }
                }
                break;
            case EventUtil.START_CORRECT:     //开始校准
                //设置完宽高，采集棋盘格
                int ret = CvcHelper.CVC_calibratorCollectImage();
                if (ret == 0) {
                    Log.i("CVC_calibratorCollect", "识别成功，下一张开始。。。");
                    if (callBack != null) {
                        callBack.picluerNext();
                    }
                    return;
                }
                break;
            case EventUtil.START_CALIBRATION:    //摄像头校准完成
                int stas = CvcHelper.CVC_calibratorStereoCalibrate();
                if (stas == 0) {
                    Log.i("CVC_calibratorStereo", "校准结束");
                    if (callBack != null) {
                        callBack.pictureFinnish();
                    }
                }
                break;
            case EventUtil.CVC_DETECTFACE:     //人脸识别
                CvcRect rect = (CvcRect) msg.obj;
                int statusCode = CvcHelper.CVC_detectFace(Camera.CameraInfo.CAMERA_FACING_BACK, rect, rectWidth, rectHeight);
                if (statusCode == 0) {
                    if (onBioAssay != null) {
                        countFace = 1;
                        onBioAssay.setOnBioFace(rect, rectWidth, rectHeight);
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
                break;
            case EventUtil.CVC_LIVINGFACE:     //活体检测
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
                break;
        }
        super.handleMessage(msg);
    }

    public CvcHandler(Looper looper) {
        super(looper);
    }


    public void setOnBioAssay(OnBioAssay onBioAssay) {
        this.onBioAssay = onBioAssay;
    }


    public void setEyesCallback(OnEyesCallBack callback) {
        this.callBack = callback;
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
         * 校准下一张
         */
        void picluerNext();

        /**
         * 校准完成
         */
        void pictureFinnish();

    }
}