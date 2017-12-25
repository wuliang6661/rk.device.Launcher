package rk.device.launcher.ui.main;

import rk.device.launcher.base.JniHandler;
import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainContract {
    interface View extends BaseRequestView {

    }

    interface Presenter extends BasePresenter<View> {


        /**
         * 初始化所有JNI外设
         */

        JniHandler initJni();


    }
}
