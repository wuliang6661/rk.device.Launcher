package rk.device.launcher.widget.carema;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 此类和前置摄像头一起主管全局所有的摄像头开启,保证摄像头对象公用
 * <p>
 * Created by wuliang on 2017/12/7.
 * <p>
 * 后置摄像头的调用
 */

public class SurfaceHolderCaremaBack implements SurfaceHolder.Callback {


    private static final String TAG = "SurfaceHolderCallbackFo";

    private static Camera camera;
    Camera.Parameters parameters;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
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
        if (null != camera) {
            camera.autoFocus((success, camera1) -> {
                if (success) {
                    parameters = camera1.getParameters();
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.setPreviewFormat(ImageFormat.NV21);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
//                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                    Log.d("wuliang", "carema (FACING_FRONT) FocusMode is " + parameters.getFocusMode());
                    parameters.setPictureSize(640, 480);
                    parameters.setPreviewSize(width, height);
                    setDisplay(parameters, camera1);
                    camera1.setParameters(parameters);
                    camera1.startPreview();
                    camera1.cancelAutoFocus();
                }
            });
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        if (camera != null) {
//            try {
//                camera.setPreviewDisplay(null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


    /**
     * 关闭显示
     */
    public void setCloseDisplay() {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 第一次进入页面，开始打开camera
     */
    private void openCamera(SurfaceHolder holder) {
        // 获取camera对象
        int cameraCount = Camera.getNumberOfCameras();
        Log.d(TAG, cameraCount + "");
        if (cameraCount == 2) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
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
        }
    }


    /**
     * 关闭carema
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
            camera.setPreviewCallback(null);
            camera.stopPreview();
        }
    }

    // 控制图像的正确显示方向
    private void setDisplay(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    // 实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
                    new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }
}
