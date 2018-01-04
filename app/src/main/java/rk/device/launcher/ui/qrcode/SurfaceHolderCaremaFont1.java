package rk.device.launcher.ui.qrcode;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;


public class SurfaceHolderCaremaFont1 implements SurfaceHolder.Callback {

    private static final String TAG = SurfaceHolderCaremaFont1.class.getSimpleName();

    private Camera camera;
    Camera.Parameters parameters;

    public SurfaceHolderCaremaFont1() {
        this.camera = SurfaceHolderCaremaFont.camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera == null) {
                camera = openCamera(holder);
            }
            setPreview(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPreview(SurfaceHolder holder) throws IOException {
        // 设置预览监听
        camera.setPreviewDisplay(holder);
        // 启动摄像头预览
        camera.startPreview();
        camera.setPreviewCallback((data, camera1) -> {

        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (null != camera) {
            camera.autoFocus((success, camera1) -> {
                if (success) {
                    parameters = camera1.getParameters();
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.setPreviewFormat(ImageFormat.NV21);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
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
        Log.d(TAG, "surfaceDestroyed: ");
    }


    /**
     * 第一次进入页面，开始打开camera
     */
    private Camera openCamera(SurfaceHolder holder) throws IOException {
        int cameraCount = Camera.getNumberOfCameras();
        if (cameraCount == 2) {
            return Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        return null;
//        if (null != camera) {
//            // 设置预览监听
//            camera.setPreviewDisplay(holder);
//            // 启动摄像头预览
//            camera.startPreview();
//            camera.setPreviewCallback((data, camera1) -> {
//
//            });
//        }
//        return null;
    }




}