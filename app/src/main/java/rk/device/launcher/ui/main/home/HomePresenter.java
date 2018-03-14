package rk.device.launcher.ui.main.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.guo.android_extend.java.AbsLoop;

import java.lang.ref.WeakReference;
import java.util.List;

import cvc.EventUtil;
import peripherals.MdHelper;
import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.TokenBo;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.FaceHelper;
import rk.device.launcher.db.entity.Face;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastReceiver;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.service.VerifyService;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.AppUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StatSoFiles;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.gps.GpsUtils;
import rk.device.launcher.utils.key.KeyUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.utils.verify.OpenUtils;
import rx.Subscriber;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HomePresenter extends BasePresenterImpl<HomeContract.View> implements HomeContract.Presenter, JniHandler.OnInitListener {


    private GpsUtils gpsUtils;
    private ElectricBroadcastReceiver mBatteryReceiver;
    private NetChangeBroadcastReceiver netChangeBroadcastRecever;
    private NetBroadcastReceiver netOffReceiver;


    /**
     * 初始化jni
     */
    @Override
    public JniHandler initJni() {
        JniHandler mHandler = JniHandler.getInstance();
        Message msg = Message.obtain();
        msg.what = EventUtil.INIT_JNI;
        mHandler.sendMessage(msg);
        mHandler.setOnInitListener(this);
        return mHandler;
    }


    /**
     * 反注册所有JNI
     */
    void deInitJni() {
        JniHandler mHandler = JniHandler.getInstance();
        Message msg = Message.obtain();
        msg.what = EventUtil.DEINIT_JNI;
        mHandler.sendMessage(msg);
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
//        return null;
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

    @Override
    public void getToken() {
        BaseApiImpl.postToken(new DeviceUuidFactory(mView.getContext()).getUuid().toString(), KeyUtils.getKey())
                .subscribe(new Subscriber<TokenBo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TokenBo tokenBo) {
                        SPUtils.put(Constant.ACCENT_TOKEN, tokenBo.getAccess_token());
                    }
                });
    }


    /**
     * 注销各类服务
     */
    void unRegisterReceiver(Activity activity) {
        if (mBatteryReceiver != null) {
            activity.unregisterReceiver(mBatteryReceiver);
        }
        if (netChangeBroadcastRecever != null) {
            activity.unregisterReceiver(netChangeBroadcastRecever);
        }
        if (netOffReceiver != null) {
            activity.unregisterReceiver(netOffReceiver);
        }
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
                }
            });
        } else { // 定位不可用, 通过IP获取地址
//            getIPLocation(activity);
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
//        BaseApiImpl.address("js").subscribeOn(Schedulers.io())
//                .flatMap(s -> {
//                    int start = s.indexOf("{");
//                    int end = s.indexOf("}");
//                    String json = s.substring(start, end + 1);
//                    AddressBO addressModel = JSON.parseObject(json, AddressBO.class);
//                    Map<String, Object> params = new HashMap<>();
//                    params.put("city", addressModel.city);
//                    Observable<List<WeatherBO>> observable;
//                    try {
//                        observable = BaseApiImpl.weather(params);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        throw new RuntimeException("error throw ip");
//                    }
//                    return observable;
//                }).observeOn(AndroidSchedulers.mainThread())
//                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
//                .subscribe(new Subscriber<List<WeatherBO>>() {
//                               @Override
//                               public void onCompleted() {
//
//                               }
//
//                               @Override
//                               public void onError(Throwable e) {
////                                   isIpError = true;
//                               }
//
//                               @Override
//                               public void onNext(List<WeatherBO> weatherModel) {
////                                   isIpError = false;
////                                   mView.showWeather(weatherModel);
//                               }
//                           }
//                );
    }


    /**
     * 获取配置接口
     */
    void getData() {
        BaseApiImpl.deviceConfiguration(AppUtils.getAppVersionCode(mView.getContext()) + "", null).subscribe(new Subscriber<DeviceInfoBO>() {
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


    /**
     * 启动人脸检测
     */
    private void registerFace() {
        FaceUtils faceUtils = FaceUtils.getInstance();
        faceUtils.setFaceFeature((name, max_score) -> {
            if (mView != null) {
                mView.hasPerson(true);
            }
            List<Face> faces = FaceHelper.getListByfaceId(name);
            if (!faces.isEmpty()) {
                openDoor(DbHelper.queryUserById(faces.get(0).getPersonId()).get(0));
            }
        });
    }


    /**
     * 验证开门
     */
    private void openDoor(User user) {
        long time = System.currentTimeMillis();
        if (user.getStartTime() < time && user.getEndTime() > time) {    //在有效时间内，则开门
            if (AppManager.getAppManager().curremtActivity() instanceof HomeActivity) {
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
            handler.sendEmptyMessage(0x11);
        }).start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FaceUtils.getInstance().init(Utils.getContext());
            FaceUtils.getInstance().loadFaces();
            registerFace();
            initJni();
//            mView.getContext().bindService(new Intent(mView.getContext(), VerifyService.class), connection, Context.BIND_AUTO_CREATE);
            mView.getContext().startService(new Intent(mContext.getApplicationContext(), VerifyService.class));
        }
    };


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.e("connect service" + name.getClassName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e("Disconnect service" + name.getClassName());
        }
    };


    /**
     * 开启socketService
     */
    void startSocketService() {
        Intent socketService = new Intent(mContext.getApplicationContext(), SocketService.class);
        mView.getContext().startService(socketService);
//        mContext.bindService(socketService, connection, Context.BIND_AUTO_CREATE);
    }

    private int isHasPerson = 0;   //连续5次检测到没人，关闭摄像头
    private boolean isStopThread = false;
    static MdThread mdThread;

    /**
     * 启动人体红外检测
     */
    @Override
    public void initCallBack(int cvcStatus, int LedStatus, int NfcStatus, int fingerStatus) {
        mdThread = new MdThread(this);
        mdThread.start();
    }


    /**
     * 红外停止
     */
    public static void stopPer() {
        if (mdThread != null) {
            mdThread.shutdown();
        }
    }

    private static class MdThread extends AbsLoop {

        WeakReference<HomePresenter> weakReference;
        int[] mdStaus;

        MdThread(HomePresenter presenter) {
            weakReference = new WeakReference<>(presenter);
            mdStaus = new int[1];
        }

        @Override
        public void setup() {

        }

        @Override
        public void loop() {
            while (true) {
                threadSleep(500);
                HomePresenter presenter = weakReference.get();
                if (presenter == null || presenter.isStopThread) {
                    return;
                }
                int mdStatus = MdHelper.PER_mdGet(1, mdStaus);
                if (mdStatus == 0 && mdStaus[0] == 1 && presenter.mView != null) {
                    presenter.isHasPerson = 0;
                    presenter.mView.hasPerson(true);
                    threadSleep(5000);
                } else {
                    presenter.isHasPerson++;
                    if (presenter.isHasPerson == 5 && presenter.mView != null) {
                        presenter.mView.hasPerson(false);
                    }
                }
            }
        }


        private void threadSleep(long time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void over() {

        }
    }
}