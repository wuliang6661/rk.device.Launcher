package rk.device.launcher.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cvc.EventUtil;
import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.AddressBO;
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastReceiver;
import rk.device.launcher.service.VerifyService;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.AppUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StatSoFiles;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.gps.GpsUtils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.utils.verify.OpenUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {


    private GpsUtils gpsUtils;
    private static final String TAG = "MainPresenter";
    private ElectricBroadcastReceiver mBatteryReceiver;
    private NetChangeBroadcastReceiver netChangeBroadcastRecever;
    private NetBroadcastReceiver netOffReceiver;


    /**
     * 初始化jni
     */
    @Override
    public JniHandler initJni() {
        JniHandler mHandler = JniHandler.getInstance();
        Message msg = new Message();
        msg.what = EventUtil.INIT_JNI;
        mHandler.sendMessage(msg);
        return mHandler;
    }

    /**
     * 注册电量监听
     */
    @Override
    public ElectricBroadcastReceiver registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        mBatteryReceiver = new ElectricBroadcastReceiver();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mView.getContext().registerReceiver(mBatteryReceiver, intentFilter);
        return mBatteryReceiver;
    }


    /**
     * 注册网络变化监听
     */
    public NetChangeBroadcastReceiver registerNetReceiver() {
        netChangeBroadcastRecever = new NetChangeBroadcastReceiver();
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
        netOffReceiver = new NetBroadcastReceiver();
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
     * 注销各类服务
     */
    void unRegisterReceiver(Activity activity) {
        activity.unregisterReceiver(mBatteryReceiver);
        activity.unregisterReceiver(netChangeBroadcastRecever);
        activity.unregisterReceiver(netOffReceiver);
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
        // mRetrofit.create(AddressAPI.class).getAddress("js")
        // 通常, mRetrofit.create(AddressAPI.class)这一段是包装起来的
        // 之前的做法是addSubscription(ApiService.deviceList(...))这样子
        // 现在的想法是在baseActivity里一开始就创建一个requestQueue,
        // requestQueue.register(observable).subscribe(subscriber)
        // 将所有的Subscription添加到一个CompositeSubscription里
        // activity在ondestroy的时候调用requestQueue.cancelAll()将CompositeSubscription.unsubscribe()

        // 我觉得可以把这个subscribeOn放在flatmap后面
        BaseApiImpl.address("js")
                .flatMap(new Func1<String, Observable<? extends List<WeatherBO>>>() {
                    @Override
                    public Observable<? extends List<WeatherBO>> call(String s) {
                        Log.d(TAG, "currentThread = " + Thread.currentThread().getName());
                        int start = s.indexOf("{");
                        int end = s.indexOf("}");
                        String json = s.substring(start, end + 1);
                        AddressBO addressModel = JSON.parseObject(json, AddressBO.class);
                        Map<String, Object> params = new HashMap<>();
                        params.put("city", addressModel.city);
                        Observable<List<WeatherBO>> observable;
                        try {
                            observable = BaseApiImpl.weather(params);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException("error throw ip");
                        }
                        return observable;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<WeatherBO>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<WeatherBO> weatherModel) {
                        mView.showWeather(weatherModel);
                    }
                });
    }


    /**
     * 天气Api
     */
    private void httpGetWeather(String area) {
        Map<String, Object> params = new HashMap<>();
        params.put("city", area);
        BaseApiImpl.weather(params).subscribe(new Subscriber<List<WeatherBO>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                T.showShort(e.getMessage());
            }

            @Override
            public void onNext(List<WeatherBO> weatherModel) {
                mView.showWeather(weatherModel);
            }
        });
    }


    /**
     * 获取配置接口
     */
    void getData() {
        BaseApiImpl.deviceConfiguration().subscribe(new Subscriber<DeviceInfoBO>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(DeviceInfoBO s) {
                if (mView != null)
                    mView.setAnimationIp(s.getMobile());
            }
        });
    }


    private int faceSuress = 0;   //活体检测通过次数，每5次请求一下人脸识别


    /**
     * 启动人脸检测
     */
    void registerFace() {
        FaceUtils faceUtils = FaceUtils.getInstance();
        faceUtils.setFaceFeature((name, max_score) -> {
//            List<User> users = DbHelper.queryByFaceId(name);
//            if (!users.isEmpty()) {
//                openDoor(users.get(0));
//            }
        });
    }

    /**
     * 验证开门
     */
    private void openDoor(User user) {
        long time = System.currentTimeMillis();
        if (user.getStartTime() < time && user.getEndTime() > time) {    //在有效时间内，则开门
            if (AppManager.getAppManager().curremtActivity() instanceof MainActivity) {
                OpenUtils.getInstance().open(VerifyTypeConstant.TYPE_FACE, user.getUniqueId(), user.getName());
            }
        }
    }


    /**
     * 初始化so
     */
    void initSO() {
        new Thread(() -> {
            StatSoFiles statSoFiles = new StatSoFiles(Utils.getContext());
            statSoFiles.verifyAndReleaseLibSo();
            statSoFiles.initNativeDirectory(Utils.getContext());
            FaceUtils.getInstance().init(Utils.getContext());
            FaceUtils.getInstance().loadFaces();
            registerFace();
            initJni();
            mView.getContext().startService(new Intent(mView.getContext(), VerifyService.class));
        }).start();
    }

}