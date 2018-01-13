package rk.device.launcher.ui.key;

import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class KeyContract {
    interface View extends BaseRequestView {

        /**
         * 激活成功
         */
        void onSuress();

    }

    interface Presenter extends BasePresenter<View> {

        /**
         * 激活设备
         */
        void activationDiveces(String uuid, String mac, String license);


    }
}
