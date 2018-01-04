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
        setCallback1(surfaceholder);
    }

    private void setCallback1(SurfaceHolder surfaceholder) {
        SurfaceHolderCaremaFont1 callbackFont = new SurfaceHolderCaremaFont1();
        surfaceholder.addCallback(callbackFont);
    }

    private void setCallback(SurfaceHolder surfaceholder) {
        SurfaceHolderCaremaFont callbackFont = new SurfaceHolderCaremaFont();
//        callbackFont.setCallBack(new SurfaceHolderCaremaFont.CallBack() {
//            @Override
//            public void callMessage(byte[] data, int width, int height) {
//
//            }
//
//            @Override
//            public void callHeightAndWidth(int width, int height) {
//
//            }
//        });
        surfaceholder.addCallback(callbackFont);
    }
}
