package rk.device.launcher.widget.carema;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

import rk.device.launcher.utils.verify.FaceUtils;

/**
 * 此类和后置摄像头一起主管全局所有的摄像头开启,保证摄像头对象公用
 * <p>
 * Created by wuliang on 2017/12/7.
 * <p>
 * 打开前置摄像头的监听
 */

public class SurfaceHolderCaremaFont implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceHolderCallbackFo";

    public static Camera camera;
    private Camera.Parameters parameters;
    private CallBack callBack;

    private int mWidth = 0;
    private int mHeight = 0;


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("wuliang", "surfaceview create");
        try {
            if (camera != null) {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                setFaceSize();
                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera1) {
                        try {
                            if (callBack != null) {
                                callBack.callMessage(data, mWidth, mHeight);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                return;
            }
            openCamera(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "width = [" + width + "], height = [" + height + "]");
        if (callBack != null) {
            callBack.callHeightAndWidth(width, height);
        }
        if (null != camera) {
            camera.autoFocus((success, camera1) -> {
                if (success) {
                    parameters = camera1.getParameters();
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.setPreviewFormat(ImageFormat.NV21);
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
    private void openCamera(SurfaceHolder holder) {
        int cameraCount = Camera.getNumberOfCameras();
        Log.d(TAG, cameraCount + "");
        if (cameraCount == 2) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (null != camera) {
            // 设置预览监听
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 启动摄像头预览
            camera.startPreview();
            setFaceSize();
            camera.setPreviewCallback((data, camera1) -> {
                if (callBack != null) {
                    callBack.callMessage(data, mWidth, mHeight);
                }
            });
        }
    }


    /**
     * 为人脸识别初始化宽高
     */
    private void setFaceSize() {
        if (mHeight == 0 || mWidth == 0) {
            mWidth = camera.getParameters().getPreviewSize().width;
            mHeight = camera.getParameters().getPreviewSize().height;
        }
        FaceUtils.getInstance().setCaremaSize(mWidth, mHeight);
    }


    /**
     * 停止数据流的输出
     */
    public static void stopCarema() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


    public static void closeSteram() {
        if (camera != null) {
            camera.stopPreview();
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