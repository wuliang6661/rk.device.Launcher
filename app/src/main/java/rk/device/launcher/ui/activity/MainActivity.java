package rk.device.launcher.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.WeatherModel;
import rk.device.launcher.event.HomeInfoEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.utils.CommonUtils;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.SizeUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.ThreadUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.utils.carema.CameraInterface;
import rk.device.launcher.widget.BatteryView;
import rk.device.launcher.widget.CameraSurfaceView;
import rk.device.launcher.widget.UpdateManager;
import rx.Subscriber;

import static rk.device.launcher.utils.DateUtil.getTime;

public class MainActivity extends BaseCompatActivity implements View.OnClickListener {

    @Bind(R.id.battry_num)
    TextView                 battryNum;
    @Bind(R.id.battry_view)
    BatteryView              battryView;
    @Bind(R.id.battry_plug)
    ImageView                battryPlug;
    private int              mCameraId            = Camera.CameraInfo.CAMERA_FACING_FRONT;
    @Bind(R.id.tv_time)
    TextView                 mTvTime;
    @Bind(R.id.tv_week)
    TextView                 mTvWeek;
    @Bind(R.id.tv_date)
    TextView                 mTvDate;
    @Bind(R.id.iv_signal)
    ImageView                mIvSignal;
    @Bind(R.id.iv_setting)
    ImageView                settingTv;
    @Bind(R.id.iv_arrow)
    ImageView                mIvArrow;
    @Bind(R.id.surface_view)
    CameraSurfaceView        mSurfaceView;
    //地区名称
    @Bind(R.id.tv_place_name)
    TextView                 placeNameTv;
    //温度
    @Bind(R.id.tv_tem)
    TextView                 temTv;
    @Bind(R.id.tv_weather)
    TextView                 weatherTv;

    private int              PREVIEW_WIDTH        = 1280;
    private int              PREVIEW_HEIGHT       = 720;

    private LocationManager  mLocationManager;
    private final String     TAG                  = "MainActivity";
    private static final int REFRESH_DELAY        = 1000;

    private boolean          isVoice              = true;

    private StaticHandler    mStaticHandler       = new StaticHandler();
    // todo 内存泄漏这里需要处理
    private final Runnable   mRefreshTimeRunnable = new Runnable() {

                                                      @Override
                                                      public void run() {
                                                          mTvTime.setText(getTime());
                                                          mTvDate.setText(DateUtil.getDate());
                                                          mTvWeek.setText(DateUtil.getWeek());
                                                          mStaticHandler.postDelayed(this,
                                                                  REFRESH_DELAY);
                                                      }
                                                  };
    private WifiHelper       mWifiHelper;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        registerRxBus();
        mWifiHelper = new WifiHelper(this);
        registerBatteryReceiver();
        registerNetReceiver();
        initLocation();
        settingTv.setOnClickListener(this);
        //        startGoogleFaceDetect();
        setLocalInfo();
    }

    /**
     * 注册RxBus
     */
    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(HomeInfoEvent.class)
                .subscribe(new Subscriber<HomeInfoEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HomeInfoEvent homeInfoEvent) {
                        placeNameTv.setText(homeInfoEvent.name);
                    }
                }));
    }

    private void setLocalInfo() {
        isVoice = (boolean) SPUtils.get(Constant.DEVICE_MP3, true);
        String deviceName = (String) SPUtils.get(Constant.DEVICE_NAME, "");
        placeNameTv.setText(deviceName);
    }

    private void startGoogleFaceDetect() {
        Camera mCamera = CameraInterface.getInstance().getCameraDevice();
        if (mCamera == null) {
            CameraInterface.getInstance().doOpenCamera(null, mCameraId);
            mCamera = CameraInterface.getInstance().getCameraDevice();
        }
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        mCamera.setParameters(params);

        // 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
        mCamera.setDisplayOrientation(90);
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {

                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 显示输入管理员密码弹窗
     */
    private InputWifiPasswordDialogFragment dialogFragment = null;

    private void showDialogFragment(String title,
                                    InputWifiPasswordDialogFragment.OnConfirmClickListener listener) {
        // FIXME: 2017/11/25 不要复用这个dialogFragment
        if (dialogFragment == null) {
            dialogFragment = InputWifiPasswordDialogFragment.newInstance();
        }
        dialogFragment.setTitle(title);
        dialogFragment.setOnCancelClickListener(
                new InputWifiPasswordDialogFragment.onCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        dialogFragment.dismiss();
                    }
                }).setOnConfirmClickListener(listener);
    }

    /**
     * 检测App更新
     */
    @Override
    protected void initData() {
        UpdateManager.getUpdateManager().checkAppUpdate(this, getSupportFragmentManager(), false);
    }

    /**
     * 注册网络变化监听
     */
    private void registerNetReceiver() {
        IntentFilter labelIntentFilter = new IntentFilter();
        // "android.net.wifi.SCAN_RESULTS"
        labelIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // "android.net.conn.CONNECTIVITY_CHANGE"
        labelIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // "android.net.wifi.STATE_CHANGE"
        labelIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // "android.net.wifi.WIFI_STATE_CHANGED"
        labelIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        labelIntentFilter.setPriority(1000); // 设置优先级，最高为1000
        registerReceiver(mNetChangeBroadcastReceiver, labelIntentFilter);
    }

    private final BroadcastReceiver mNetChangeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isScanResultChange = action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            boolean isNetworkStateChange = action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            boolean isWifiNetworkStateChange = action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION);
            boolean isConnectStateChange = action.equals(ConnectivityManager.CONNECTIVITY_ACTION);
            if (isScanResultChange || isNetworkStateChange || isWifiNetworkStateChange) {

                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null) {
                    // 是有线的连接方式并且处于可用、可连接的状态
                    if (info.getType() == ConnectivityManager.TYPE_ETHERNET
                            && NetworkInfo.State.CONNECTED == info.getState()
                            && info.isAvailable()) {
                        mIvSignal.setImageResource(R.drawable.net_line);
                        return;
                    }
                }

                //				LogUtil.d("wifi列表刷新或者wifi状态发生了改变");
                ScanResult scanResult = mWifiHelper.getConnectedScanResult();
                // 先判断wifi是否可用
                if (scanResult != null && mWifiHelper.checkWifiState()) {
                    //判断信号强度，显示对应的指示图标
                    changeSignalState(scanResult);
                } else { // wifi不可用
                    mIvSignal.setImageResource(R.drawable.wifi_signal_disconnect);
                }
            } else if (isConnectStateChange) { // 有线或者无线的连接方式发生了改变
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent
                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                //				ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info == null) {
                    LogUtil.d("没有可用的网络连接");
                    mIvSignal.setImageResource(R.drawable.wifi_signal_disconnect);
                    return;
                }
                // 是有线的连接方式并且处于可用、可连接的状态
                if (info.getType() == ConnectivityManager.TYPE_ETHERNET
                        && NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    mIvSignal.setImageResource(R.drawable.net_line);
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI // wifi可用
                        && NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    ScanResult scanResult = mWifiHelper.getConnectedScanResult();
                    // 先判断wifi是否可用
                    if (scanResult != null && mWifiHelper.checkWifiState()) {
                        //判断信号强度，显示对应的指示图标
                        changeSignalState(scanResult);
                    } else { // wifi不可用
                        mIvSignal.setImageResource(R.drawable.wifi_signal_disconnect);
                    }
                } else { // 剩余的是连接不可用的状态
                    mIvSignal.setImageResource(R.drawable.wifi_signal_disconnect);
                }
            }
        }
    };

    private void changeSignalState(ScanResult scanResult) {
        if (Math.abs(scanResult.level) > 100) {
            mIvSignal.setImageResource(R.drawable.wifi_signal_1);
        } else if (Math.abs(scanResult.level) > 80) {
            mIvSignal.setImageResource(R.drawable.wifi_signal_1);
        } else if (Math.abs(scanResult.level) > 70) {
            mIvSignal.setImageResource(R.drawable.wifi_signal_1);
        } else if (Math.abs(scanResult.level) > 60) {
            mIvSignal.setImageResource(R.drawable.wifi_signal_2);
        } else if (Math.abs(scanResult.level) > 50) {
            mIvSignal.setImageResource(R.drawable.wifi_signal_3);
        } else {
            mIvSignal.setImageResource(R.drawable.wifi_signal_3);
        }
    }

    private void initLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 既没有打开gps也没有打开网络
        if (!isGpsOpened() && !isNewWorkOpen()) {
            Toast.makeText(this, "请打开网络或GPS定位功能!", Toast.LENGTH_SHORT).show();
            //			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //			startActivityForResult(intent, 0);
            return;
        }
        ThreadUtils.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Location location = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        Log.d(TAG, "gps.location = null");
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    Log.d(TAG, "network.location = " + location);
                    Geocoder geocoder = new Geocoder(CommonUtils.getContext(), Locale.getDefault());
                    if (location == null) {
                        return;
                    }
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        if (addresses.size() > 0) {
                            Address address = addresses.get(0);
                            String city = address.getLocality();
                            httpGetWeatherInfo(address.getSubAdminArea());
                            LogUtil.d("city = " + city);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void httpGetWeatherInfo(String city) {
        JSONObject params = new JSONObject();
        params.put("location", city);
        addSubscription(ApiService.weather(params).subscribe(new Subscriber<WeatherModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(WeatherModel weatherModel) {
                if (weatherModel.getResults().size() > 0
                        && weatherModel.getResults().get(0).getDaily().size() > 0) {
                    WeatherModel.ResultsBean.DailyBean bean = weatherModel.getResults().get(0)
                            .getDaily().get(0);
                    temTv.setText(bean.getLow() + "~" + bean.getHigh() + "℃");
                    if (TimeUtils.getHour(new Date(System.currentTimeMillis())) > 6
                            && TimeUtils.getHour(new Date(System.currentTimeMillis())) < 18) {
                        weatherTv.setText(bean.getText_day());
                    } else {
                        weatherTv.setText(bean.getText_night());
                    }
                }
            }
        }));
    }

    private boolean isGpsOpened() {
        boolean isOpen = true;
        // 没有开启GPS
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isOpen = false;
        }
        return isOpen;
    }

    private boolean isNewWorkOpen() {
        boolean isOpen = true;
        // 没有开启网络定位
        if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            isOpen = false;
        }
        return isOpen;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStaticHandler.post(mRefreshTimeRunnable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_setting:
                settingLoad();
                break;
        }
    }

    /**
     * 初始设置流程加载
     */
    private void settingLoad() {
        final String password = SPUtils.getString(Constant.KEY_PASSWORD);
        String message;
        if (StringUtils.isEmpty(password)) {
            message = "设置管理员密码";
        } else {
            message = "请输入管理员密码";
        }
        showDialogFragment(message, content -> {
            dialogFragment.dismiss();
            if (StringUtils.isEmpty(password)) { //设置管理员密码，判断为第一次进入
                if (!TextUtils.isEmpty(content)) {
                    SPUtils.putString(Constant.KEY_PASSWORD, content); //缓存密码
                    gotoActivity(SetBasicInfoActivity.class, false);//进入基础设置
                }
            } else { //本地存在密码,则按缓存序号，跳入未设置页面
                if (TextUtils.equals(password, content)) { //密码输入正确
                    int type = SPUtils.getInt(Constant.SETTING_NUM, Constant.SETTING_TYPE1);
                    switch (type) {
                        //                        case Constant.SETTING_TYPE1:         //进入基础设置
                        //                            gotoActivity(SetBasicInfoActivity.class, false);   //缓存一个2
                        //                            break;
                        //                        case Constant.SETTING_TYPE2:         //网络设置
                        //                            gotoActivity(SetNetWorkActivity.class, false);    //缓存个4
                        //                            break;
                        //                        case Constant.SETTING_TYPE3:       //门禁设置
                        //                            gotoActivity(SetDoorGuardActivity.class, false);    //缓存个5
                        //                            break;
                        //                        case Constant.SETTING_TYPE4:     //系统设置
                        //                            gotoActivity(SetSysActivity.class, false);
                        //                            break;
                        default:
                            gotoActivity(SettingActivity.class, false);
                            break;
                    }
                } else {
                    dialogFragment.showError();
                    T.showShort(getString(R.string.password_error));
                }
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    private void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, intentFilter);
    }

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int levelPercent = (int) (level * 100f / scale);
            LogUtil.d("电池电量百分比 = " + levelPercent);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    LogUtil.d("充电中");
                    setBattryListener(levelPercent, true);
                    return;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    LogUtil.d("未充电");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    LogUtil.d("充电完成");
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    LogUtil.d("放电中");
                    break;
            }
            setBattryListener(levelPercent, false);
        }
    };

    /**
     * 处理电池变化
     */
    private void setBattryListener(int leverPercent, boolean isPlugs) {
        if (isPlugs) {
            battryPlug.setVisibility(View.VISIBLE);
        } else {
            battryPlug.setVisibility(View.GONE);
        }
        battryNum.setText(leverPercent + "%");
        battryView.setProgress(leverPercent);
    }

    /**
     * 根据自己的电池图标做响应的调整
     *
     * @param progress 0-100
     * @return 0-10000
     */
    private int calculateLevel(int progress) {
        int leftOffest = SizeUtils.dp2px(2);
        int powerLength = SizeUtils.dp2px(26.5f);// 40 px in hdpi
        int totalLength = SizeUtils.dp2px(32.5f);// 49 px in hdpi
        int level = (leftOffest + powerLength * progress / 100) * 10000 / totalLength;
        return level;
    }

    private static class StaticHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

}
