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

    private int ret;
    private int[] livingCode = new int[3];
    private int count = 0;
    private boolean isAdjust = false;

    private int[] rectWidth = new int[1];
    private int[] rectHeight = new int[1];
    private int[] possibilityCode = new int[1];
    private int encodedFaceMemLen = 512 * (1 << 10);  /* 512KM */
    private byte[] faces = new byte[encodedFaceMemLen];  /* will be filled with encoded JPEG data */
    private int[] length = new int[1];
    private OnBioAssay onBioAssay;

    private int countFace = 1;   //记录返回11的次数 ，超过5次，则隐藏人脸框

    public void setOnBioAssay(OnBioAssay onBioAssay) {
        this.onBioAssay = onBioAssay;
    }


    public CvcHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case EventUtil.START_CVC:
                if (isAdjust) {
                    Log.i("result code:", String.valueOf(livingCode[0]));
                    return;
                }
                int width = msg.arg1;
                int height = msg.arg2;
                if (width == 0 || height == 0) {
                    Log.i("CVC_determineLivingFace", "请设置宽高");
                    return;
                } else {
                    //如果是第一次，需要设置宽高
                    if (count == 0) {
                        ret = CvcHelper.CVC_calibratorInit(width, height);
                        if (ret != 0) {
                            Log.i("CVC_calibratorInit", "初始化校准失败");
                            return;
                        }
                    }
                    //设置完宽高，采集棋盘格
                    ret = CvcHelper.CVC_calibratorCollectImage();
                    if (ret != 0) {
                        Log.i("CVC_calibratorCollect", "未识别到棋盘格");
                        return;
                    }
                    count++;
                }
                break;
            case EventUtil.START_CALIBRATION:
                ret = CvcHelper.CVC_calibratorStereoCalibrate();
                if (ret != 0) {
                    Log.i("CVC_calibratorStereo", "校准失败");
                    return;
                } else {
                    Log.i("CVC_calibratorStereo", "校准成功");
                    isAdjust = true;
                    count = 0;
                }
                break;
            case EventUtil.CVC_DETECTFACE:     //人脸识别
                CvcRect rect = (CvcRect) msg.obj;
                int statusCode = CvcHelper.CVC_detectFace(
                        Camera.CameraInfo.CAMERA_FACING_BACK, rect, rectWidth,
                        rectHeight);
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
                    // possibilityCode[0]   //返回是真人的概率
                    // length[0]
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


}