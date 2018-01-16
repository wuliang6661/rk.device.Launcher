package rk.device.launcher.widget.carema;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import rk.device.launcher.widget.carema.SurfaceHolderCaremaBack;

/**
 * 后置摄像头 Created by hanbin on 2017/11/28.
 */

public class BackCameraSurfaceView extends SurfaceView {


    Context mContext;
    SurfaceHolder mSurfaceHolder;
    SurfaceHolderCaremaBack surface;

    public BackCameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        surface = new SurfaceHolderCaremaBack();
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(surface);
    }


    public void setDisplay(boolean isDisplay) {
        if(!isDisplay){
            surface.setCloseDisplay();
        }
    }

}
