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

import rk.device.launcher.SurfaceHolderCaremaBack;

/**
 * 后置摄像头 Created by hanbin on 2017/11/28.
 */

public class BackCameraSurfaceView extends SurfaceView {


    Context mContext;
    SurfaceHolder mSurfaceHolder;

    public BackCameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceHolderCaremaBack());
    }

}
