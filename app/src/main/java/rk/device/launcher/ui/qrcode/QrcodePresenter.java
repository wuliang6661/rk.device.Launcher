package rk.device.launcher.ui.qrcode;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class QrcodePresenter extends BasePresenterImpl<QrcodeContract.View> implements QrcodeContract.Presenter{

    private static final String TAG = "QrcodePresenter";

    /**
     * 初始化摄像头
     */
    @Override
    public void initCamera() {
        SurfaceView surfaceView = mView.getSurfaceView();
        SurfaceHolder surfaceholder = surfaceView.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        setCallback(surfaceholder);
    }


    private void setCallback(SurfaceHolder surfaceholder) {
        SurfaceHolderCaremaFont callbackFont = new SurfaceHolderCaremaFont();
        surfaceholder.addCallback(callbackFont);
    }
}
