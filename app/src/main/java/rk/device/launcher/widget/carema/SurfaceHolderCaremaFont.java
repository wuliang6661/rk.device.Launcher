package rk.device.launcher.widget.carema;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * 此类和后置摄像头一起主管全局所有的摄像头开启,保证摄像头对象公用
 * <p>
 * Created by wuliang on 2017/12/7.
 * <p>
 * 打开前置摄像头的监听
 */

public class SurfaceHolderCaremaFont implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceHolderCallbackFo";

    private static Camera camera;
    Camera.Parameters parameters;
    CallBack callBack;

    private int width = 0;
    private int height = 0;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                camera.setPreviewDisplay(holder);
                return;
            }
            openCamera(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        Log.d(TAG, "width = [" + width + "], height = [" + height + "]");
        if (callBack != null) {
            callBack.callHeightAndWidth(width, height);
        }
        if (null != camera) {
            camera.autoFocus((success, camera1) -> {
                if (success) {
                    parameters = camera1.getParameters();
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
//                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                    Log.d("wuliang", "carema (FACING_BACK) FocusMode is " + parameters.getFocusMode());
                    parameters.setPictureSize(640, 480);
                    parameters.setPreviewSize(width, height);
                    camera1.setParameters(parameters);
                    camera1.startPreview();
                    camera1.cancelAutoFocus();
                }
            });
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface被干掉了！！！！！");
    }


    /**
     * 第一次进入页面，开始打开camera
     */
    private void openCamera(SurfaceHolder holder) throws IOException {
        // 获取camera对象
        int cameraCount = Camera.getNumberOfCameras();
        Log.d(TAG, cameraCount + "");
        if (cameraCount == 2) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (null != camera) {
            // 设置预览监听
            camera.setPreviewDisplay(holder);
            // 启动摄像头预览
            camera.startPreview();
            camera.setPreviewCallback((data, camera1) -> {
                if (callBack != null) {
                    callBack.callMessage(data, width, height);
                }
            });
        }
    }


    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    public interface CallBack {

        /**
         * 摄像头打开之后每一帧数据的回调
         */
        void callMessage(byte[] data, int width, int height);

        /**
         * 返回SurfaceHolder的宽高
         */
        void callHeightAndWidth(int width, int height);

    }


}