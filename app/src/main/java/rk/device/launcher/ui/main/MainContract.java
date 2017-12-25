package rk.device.launcher.ui.main;

import java.util.List;

import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseRequestView;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastReceiver;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainContract {
    interface View extends BaseRequestView {


        void showWeather(List<WeatherBO> weatherModel);


    }

    interface Presenter extends BasePresenter<View> {

        JniHandler initJni();

        ElectricBroadcastReceiver registerBatteryReceiver();


        NetChangeBroadcastReceiver registerNetReceiver();

        void registerNetOffReceiver();

    }
}
