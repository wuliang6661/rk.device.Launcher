package rk.device.launcher.ui.main.home;

import rk.device.launcher.base.JniHandler;
import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseView;
import rk.device.launcher.service.NetChangeBroadcastReceiver;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HomeContract {
    interface View extends BaseView {

//        void showWeather(List<WeatherBO> weatherModel);


        void hasPerson(boolean hasPerson);

    }

    interface Presenter extends BasePresenter<View> {


        JniHandler initJni();

        NetChangeBroadcastReceiver registerNetReceiver();

        void registerNetOffReceiver();

        void getToken();

    }
}
