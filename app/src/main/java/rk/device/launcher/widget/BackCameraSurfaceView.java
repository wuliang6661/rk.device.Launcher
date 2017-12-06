package rk.device.launcher.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 后置摄像头 Created by hanbin on 2017/11/28.
 */

public class BackCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "BackCameraSurfaceView";

    Context mContext;
    SurfaceHolder mSurfaceHolder;
    private Camera camera;
    Camera.Parameters parameters;

    public BackCameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                camera.setPreviewDisplay(holder);
                return;
            }
            // 获取camera对象
            int cameraCount = Camera.getNumberOfCameras();
            Log.d(TAG, cameraCount + "");
            if (cameraCount > 0) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                if (null != camera) {
                    // 设置预览监听
                    camera.setPreviewDisplay(holder);
                    // 启动摄像头预览
                    camera.startPreview();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
//                    camera.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (null != camera) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        parameters = camera.getParameters();
                        parameters.setPictureFormat(PixelFormat.JPEG);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                        setDisplay(parameters, camera);
                        camera.setParameters(parameters);
                        camera.startPreview();
                        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                    }
                }
            });
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface后置被干掉了！！！！！");
//        closeCamera();
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

    private void closeCamera() {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }
}
