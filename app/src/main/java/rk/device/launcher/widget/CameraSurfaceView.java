package rk.device.launcher.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import rk.device.launcher.utils.carema.CameraInterface;

/**
 * Created by hanbin on 2017/11/23.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraSurfaceView";
    Context mContext;
    SurfaceHolder mSurfaceHolder;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated...");
        CameraInterface.getInstance().doOpenCamera(null, Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.i(TAG, "surfaceChanged...");
        CameraInterface.getInstance().doStartPreview(mSurfaceHolder, 1.333f);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed...");
        CameraInterface.getInstance().doStopCamera();
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

}
