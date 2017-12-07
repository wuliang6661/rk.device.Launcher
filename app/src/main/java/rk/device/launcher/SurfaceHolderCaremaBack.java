package rk.device.launcher;

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

    SurfaceHolderCaremaFont.CallBack callBack;

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
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        if (null != camera) {
            // 设置预览监听
            camera.setPreviewDisplay(holder);
            // 启动摄像头预览
            camera.startPreview();
            camera.setPreviewCallback((data, camera1) -> {
                if (callBack != null) {
                    callBack.callMessage();
                }
            });
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
