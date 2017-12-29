package rk.device.launcher.ui.numpassword;

import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class NumpasswordContract {


    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<View> {

        /**
         * 开门
         */
        void openDoor();

    }
}
