package rk.device.launcher.ui.qrcode;

import android.view.SurfaceView;

import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class QrcodeContract {
    interface View extends BaseView {
        SurfaceView getSurfaceView();
    }

    interface  Presenter extends BasePresenter<View> {
        void initCamera();
    }
}
