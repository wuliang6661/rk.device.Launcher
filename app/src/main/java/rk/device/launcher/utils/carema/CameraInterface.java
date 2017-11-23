package rk.device.launcher.utils.carema;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

import rk.device.launcher.utils.carema.utils.CamParaUtil;
import rk.device.launcher.utils.carema.utils.FileUtil;
import rk.device.launcher.utils.carema.utils.ImageUtil;

public class CameraInterface {
    private static final String TAG = "arronzhouInterface";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private int mCameraId = -1;
    private int PREVIEW_WIDTH = 1280;
    private int PREVIEW_HEIGHT = 720;
    private static CameraInterface mCameraInterface;

    public interface CamOpenOverCallback {
        void cameraHasOpened();
    }

    private CameraInterface() {

    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * 打开Camera
     *
     * @param callback
     */
    public void doOpenCamera(CamOpenOverCallback callback, int cameraId) {
        Log.i(TAG, "Camera open....");
        if(mCamera==null){
            mCamera = Camera.open(cameraId);
        }
        mCameraId = cameraId;
        if (callback != null) {
            callback.cameraHasOpened();
        }
    }

    public void doOpenCamera(CamOpenOverCallback callback, Camera camera) {
        Log.i(TAG, "Camera open....");
        mCamera = camera;
        if (callback != null) {
            callback.cameraHasOpened();
        }
    }

    /**
     * doStartPreview
     *
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            mParams.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            mParams.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);

            mCamera.setDisplayOrientation(90);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            for (int i = 0; i < focusModes.size(); i++) {
                Log.d("focusModes", "focusModes" + i + ":" + focusModes.get(i));
            }
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;

            mParams = mCamera.getParameters();
        }
    }

    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    public Camera.Parameters getCameraParams() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            return mParams;
        }
        return null;
    }

    public Camera getCameraDevice() {
        return mCamera;
    }

    public int getCameraId() {
        return mCameraId;
    }

    ShutterCallback mShutterCallback = new ShutterCallback() {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data,
                                   Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0,
                        data.length);
                mCamera.stopPreview();
                isPreviewing = false;
            }

            if (null != b) {
                Bitmap rotaBitmap = ImageUtil
                        .getRotateBitmap(b, 90.0f);
                FileUtil.saveBitmap(rotaBitmap);
            }
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

}
