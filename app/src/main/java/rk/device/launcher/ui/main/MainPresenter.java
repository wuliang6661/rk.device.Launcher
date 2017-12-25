package rk.device.launcher.ui.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cvc.EventUtil;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.AddressBO;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastReceiver;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.gps.GpsUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {


    GpsUtils gpsUtils;

    /**
     * 初始化jni
     */
    @Override
    public JniHandler initJni() {
        JniHandler mHandler = JniHandler.getInstance();
        Message msg = new Message();
        msg.what = EventUtil.INIT_JNI;
        mHandler.sendMessageDelayed(msg, 10);
        return mHandler;
    }

    /**
     * 注册电量监听
     */
    @Override
    public ElectricBroadcastReceiver registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        ElectricBroadcastReceiver mBatteryReceiver = new ElectricBroadcastReceiver();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mView.getContext().registerReceiver(mBatteryReceiver, intentFilter);
        return mBatteryReceiver;
    }


    /**
     * 注册网络变化监听
     */
    public NetChangeBroadcastReceiver registerNetReceiver() {
        NetChangeBroadcastReceiver netChangeBroadcastRecever = new NetChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // "android.net.wifi.SCAN_RESULTS"
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // "android.net.conn.CONNECTIVITY_CHANGE"
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // "android.net.wifi.STATE_CHANGE"
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // "android.net.wifi.WIFI_STATE_CHANGED"
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.setPriority(1000); // 设置优先级，最高为1000
        mView.getContext().registerReceiver(netChangeBroadcastRecever, intentFilter);
        return netChangeBroadcastRecever;
    }


    /**
     * 注册网络断开监听
     */
    public void registerNetOffReceiver() {
        NetBroadcastReceiver netOffReceiver = new NetBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // "android.net.wifi.SCAN_RESULTS"
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // "android.net.conn.CONNECTIVITY_CHANGE"
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // "android.net.wifi.STATE_CHANGE"
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mView.getContext().registerReceiver(netOffReceiver, intentFilter);
    }


    /**
     * 获取地理位置
     */
    void initLocation(BaseActivity activity) {
        if (gpsUtils == null) {
            gpsUtils = new GpsUtils(mView.getContext());
        }
        if (gpsUtils.isLoactionAvailable()) { // 定位可用, 通过定位获取地址
            gpsUtils.initLocation(address -> {
                if (address.size() > 0) {
                    String area = address.get(0).getSubAdminArea();
                    SPUtils.putString(Constant.KEY_ADDRESS, area);
                    httpGetWeather(area);
                }
            });
        } else { // 定位不可用, 通过IP获取地址
            getIPLocation(activity);
        }
    }

    /**
     * 定位不可用，通过IP获取地址
     */
    private void getIPLocation(BaseActivity activity) {
        ApiService.address("js").compose(activity.bindUntilEvent(ActivityEvent.DESTROY)).subscribeOn(Schedulers.io())
                .flatMap(s -> {
                    int start = s.indexOf("{");
                    int end = s.indexOf("}");
                    String json = s.substring(start, end + 1);
                    AddressBO addressModel = JSON.parseObject(json, AddressBO.class);
                    Map<String, Object> params = new HashMap<>();
                    params.put("city", addressModel.city);
                    Observable<List<WeatherBO>> observable;
                    try {
                        observable = ApiService.weather(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("error throw ip");
                    }
                    return observable;
                }).observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Subscriber<List<WeatherBO>>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
//                                   isIpError = true;
                               }

                               @Override
                               public void onNext(List<WeatherBO> weatherModel) {
//                                   isIpError = false;
//                                   showWeather(weatherModel);
                               }
                           }
                );
    }


    /**
     * 天气Api
     */
    private void httpGetWeather(String area) {
        Map<String, Object> params = new HashMap<>();
        params.put("city", area);
        ApiService.weather(params).subscribe(new Subscriber<List<WeatherBO>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                T.showShort(e.getMessage());
            }

            @Override
            public void onNext(List<WeatherBO> weatherModel) {
//                showWeather(weatherModel);

            }
        });
    }

}
