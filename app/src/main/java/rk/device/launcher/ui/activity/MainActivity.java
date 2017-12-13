package rk.device.launcher.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cvc.CvcHelper;
import cvc.CvcRect;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.SurfaceHolderCaremaFont;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.AddressModel;
import rk.device.launcher.bean.DeviceInfoBean;
import rk.device.launcher.bean.SetPageContentBean;
import rk.device.launcher.bean.VerifyBean;
import rk.device.launcher.bean.WeatherModel;
import rk.device.launcher.event.IpHostEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.LauncherApplication;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastRecever;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.ui.fragment.InitErrorDialogFragmen;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.utils.AppUtils;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.ShellUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.gps.GpsUtils;
import rk.device.launcher.utils.oss.AliYunOssUtils;
import rk.device.launcher.utils.oss.OssUploadListener;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.widget.BatteryView;
import rk.device.launcher.widget.DetectedFaceView;
import rk.device.launcher.widget.UpdateManager;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseCompatActivity implements View.OnClickListener,
        ElectricBroadcastReceiver.CallBack, NetChangeBroadcastRecever.CallBack, JniHandler.OnBioAssay, JniHandler.OnInitListener {

    private static final String TAG = "MainActivity";

    @Bind(R.id.battry_num)
    TextView battryNum;
    @Bind(R.id.battry_view)
    BatteryView battryView;
    @Bind(R.id.battry_plug)
    ImageView battryPlug;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_week)
    TextView mTvWeek;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.iv_signal)
    ImageView mIvSignal;
    @Bind(R.id.iv_setting)
    ImageView settingTv;
    @Bind(R.id.iv_arrow)
    ImageView mIvArrow;
    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.tv_tem)
    TextView temTv;                                                           //温度
    @Bind(R.id.tv_weather)
    TextView weatherTv;                                                       //天气
    @Bind(R.id.face_view)
    DetectedFaceView faceView;
    @Bind(R.id.tv_declare)
    TextView mTvDeclare;
    @Bind(R.id.tv_place_name)
    TextView tvPlaceName;
    @Bind(R.id.init_error)
    ImageView initError;

    int faceCount = 0;//记录摄像头采集的画面帧数,每5帧调取一次人脸识别
    private JniHandler mHandler = null;
    private static final int REFRESH_DELAY = 1000;
    SurfaceHolderCaremaFont callbackFont;
    Subscription mSubscription;
    private StaticHandler mStaticHandler = new StaticHandler();
    private DeviceUuidFactory uuidFactory = null;
    private String uUid;
    NetChangeBroadcastRecever netChangeBroadcastRecever;
    ElectricBroadcastReceiver mBatteryReceiver;
    private GpsUtils gpsUtils = null;
    private InputWifiPasswordDialogFragment dialogFragment = null;
    private String modilePhone;
    private InitErrorDialogFragmen initDialog;

    private boolean isNetWork = true;// 此状态保存上次网络是否连接，默认已连接


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initDialog = InitErrorDialogFragmen.newInstance();
        registerBatteryReceiver();
        registerNetReceiver();
        registerRxBus();
        registerIPHost();
        initLocation();
        settingTv.setOnClickListener(this);
        setOnClick(R.id.rl_contact_manager, R.id.init_error);
        initSurfaceViewOne();
        getData();
        startService(new Intent(this, SocketService.class));
    }


    @Override
    protected void initData() {
        ShellUtils.upgradeRootPermission("/data/rk_backup");
        String declareContent = SPUtils.getString(Constant.KEY_FIRSTPAGE_CONTENT);
        mTvDeclare.setText(declareContent);
        //检测App更新
        UpdateManager.getUpdateManager().checkAppUpdate(this, getSupportFragmentManager(), false);
        initHandlerThread();
    }


    /**
     * 初始化所有JNI外设
     */
    private void initHandlerThread() {
        HandlerThread thread = new HandlerThread("new_thread");
        thread.start();
        Looper looper = thread.getLooper();
        mHandler = new JniHandler(looper);
        mHandler.setOnInitListener(this);
        Message msg = new Message();
        msg.what = EventUtil.INIT_JNI;
        mHandler.sendMessageDelayed(msg, 10);
    }


    /**
     * 注册网络监听
     */
    private void registerNetReceiver() {
        netChangeBroadcastRecever = new NetChangeBroadcastRecever();
        IntentFilter labelIntentFilter = new IntentFilter();
        // "android.net.wifi.SCAN_RESULTS"
        labelIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // "android.net.conn.CONNECTIVITY_CHANGE"
        labelIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // "android.net.wifi.STATE_CHANGE"
        labelIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // "android.net.wifi.WIFI_STATE_CHANGED"
        labelIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        netChangeBroadcastRecever.setCallBack(this);
        labelIntentFilter.setPriority(1000); // 设置优先级，最高为1000
        registerReceiver(netChangeBroadcastRecever, labelIntentFilter);
    }


    /**
     * 注册电量监听
     */
    private void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        mBatteryReceiver = new ElectricBroadcastReceiver();
        mBatteryReceiver.setCallBack(this);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, intentFilter);
    }

    /**
     * 接收服务器IP地址更改，所有数据重新请求
     */
    private void registerIPHost() {
        RxBus.getDefault().toObserverable(IpHostEvent.class).subscribe(ipHostEvent -> {
            initLocation();
            getData();
        }, throwable -> {

        });
    }


    /**
     * 接收推送通知的公告并显示
     */
    private void registerRxBus() {
        mSubscription = RxBus.getDefault().toObserverable(SetPageContentBean.class).subscribe(setPageContentBean -> {
            if (mTvDeclare != null) {
                mTvDeclare.setText(setPageContentBean.content);
            }
        }, throwable -> {

        });
    }


    /**
     * 初始化摄像头显示
     */
    private void initSurfaceViewOne() {
        SurfaceHolder surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        callbackFont = new SurfaceHolderCaremaFont();
        openCamera();
        surfaceholder.addCallback(callbackFont);
    }


    /**
     * 第一次进入页面，开始打开camera
     */
    private void openCamera() {
        callbackFont.setCallBack(new SurfaceHolderCaremaFont.CallBack() {
            @Override
            public void callMessage() {
                faceCount++;
                if (faceCount % 5 != 0) {
                    return;
                }
                Message message = new Message();
                message.what = EventUtil.CVC_DETECTFACE;
                mHandler.setOnBioAssay(MainActivity.this);
                mHandler.sendMessage(message);
                faceCount = faceCount == 25 ? 0 : faceCount;
            }

            @Override
            public void callHeightAndWidth(int width, int height) {
                faceView.setRoomHeight(height);
            }
        });
    }


    /**
     * 显示输入管理员密码弹窗
     */
    private void showDialogFragment(String title, InputWifiPasswordDialogFragment.OnConfirmClickListener listener, boolean isHideInput) {
        dialogFragment = InputWifiPasswordDialogFragment.newInstance();
        dialogFragment.setTitle(title);
        dialogFragment.showHite("请输入6位数密码");
        dialogFragment.setMaxLength(6);
        if (isHideInput) {
            dialogFragment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);   //隐藏密码
        } else {
            dialogFragment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);   //显示密码
        }
        dialogFragment.setOnCancelClickListener(() -> dialogFragment.dismiss()).setOnConfirmClickListener(listener);
    }


    /**
     * 获取地理位置
     */
    private void initLocation() {
        if (gpsUtils == null) {
            gpsUtils = new GpsUtils(this);
        }
        if (gpsUtils.isLoactionAvailable()) { // 定位可用, 通过定位获取地址
            gpsUtils.initLocation(address -> {
                if (address.size() > 0) {
                    String area = address.get(0).getSubAdminArea();
                    LogUtil.d(TAG, "area = " + area);
                    SPUtils.putString(Constant.KEY_ADDRESS, area);
                    httpGetWeather(area);
                }
            });
        } else { // 定位不可用, 通过IP获取地址
            getIPLocation();
        }
    }

    /**
     * 定位不可用，通过IP获取地址
     */
    private void getIPLocation() {
        addSubscription(ApiService.address("js").subscribeOn(Schedulers.io())
                .flatMap(s -> {
                    int start = s.indexOf("{");
                    int end = s.indexOf("}");
                    String json = s.substring(start, end + 1);
                    AddressModel addressModel = JSON.parseObject(json, AddressModel.class);
                    Map<String, Object> params = new HashMap<>();
                    params.put("city", addressModel.city);
                    return ApiService.weather(params);
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<WeatherModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        T.showShort(e.getMessage());
                    }

                    @Override
                    public void onNext(List<WeatherModel> weatherModel) {
                        showWeather(weatherModel);
                    }
                })
        );
    }


    /**
     * 天气Api
     *
     * @param area
     */
    private void httpGetWeather(String area) {
        Map<String, Object> params = new HashMap<>();
        params.put("city", area);
        ApiService.weather(params).subscribe(new Subscriber<List<WeatherModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                T.showShort(e.getMessage());
            }

            @Override
            public void onNext(List<WeatherModel> weatherModel) {
                showWeather(weatherModel);
            }
        });
    }

    /**
     * 显示天气
     */
    private void showWeather(List<WeatherModel> weatherModel) {
        if (weatherModel.size() > 0 && weatherModel.get(0).getDaily().size() > 0) {
            WeatherModel.DailyBean detailBean = weatherModel.get(0).getDaily()
                    .get(0);
            temTv.setText(detailBean.getLow() + "~" + detailBean.getHigh() + "℃");
            //判断当前时间是晚上还是白天来显示天气
            if (TimeUtils.getHour(new Date(System.currentTimeMillis())) > 6
                    && TimeUtils.getHour(new Date(System.currentTimeMillis())) < 18) {
                weatherTv.setText(detailBean.getText_day());
            } else {
                weatherTv.setText(detailBean.getText_night());
            }
        }
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
            case R.id.rl_contact_manager:
                if (!StringUtils.isEmpty(modilePhone)) {
                    showMessageDialog("联系电话: " + modilePhone);
                }
                break;
            case R.id.init_error:     //有外设初始化失败
                initDialog.show(getSupportFragmentManager(), "");
                break;
        }
    }


    /**
     * 获取关联设备的配置
     */
    public void getData() {
        ApiService.deviceConfiguration(AppUtils.getAppVersionCode(this) + "", null).subscribe(new Subscriber<DeviceInfoBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(DeviceInfoBean s) {
                modilePhone = s.getMobile();
            }
        });
    }


    /**
     * 初始设置流程加载
     */
    private void settingLoad() {
        boolean isHideInput;
        final String password = SPUtils.getString(Constant.KEY_PASSWORD);
        String message;
        if (StringUtils.isEmpty(password)) {
            isHideInput = false;
            message = "设置管理员密码";
        } else {
            message = "请输入管理员密码";
            isHideInput = true;
        }
        showDialogFragment(message, content -> {
            if (StringUtils.isEmpty(content)) {
                dialogFragment.showError("请输入管理员密码！");
                return;
            }
            if (content.length() != 6) {
                dialogFragment.showError("请输入完整密码！");
                return;
            }
            if (StringUtils.isEmpty(password)) { //设置管理员密码，判断为第一次进入
                if (!TextUtils.isEmpty(content)) {
                    dialogFragment.dismiss();
                    SPUtils.putString(Constant.KEY_PASSWORD, content); //缓存密码
                    gotoActivity(SetBasicInfoActivity.class, false);//进入基础设置
                } else {
                    dialogFragment.showError("请设置管理员密码！");
                }
            } else { //本地存在密码,则按缓存序号，跳入未设置页面
                if (TextUtils.equals(password, content)) { //密码输入正确
                    dialogFragment.dismiss();
                    int type = SPUtils.getInt(Constant.SETTING_NUM, Constant.SETTING_TYPE1);
                    switch (type) {
                        case Constant.SETTING_TYPE1:         //进入基础设置
                            gotoActivity(SetBasicInfoActivity.class, false);   //缓存一个2
                            break;
                        case Constant.SETTING_TYPE2:         //网络设置
                            gotoActivity(SetNetWorkActivity.class, false);    //缓存个4
                            break;
                        case Constant.SETTING_TYPE3:       //门禁设置
                            gotoActivity(SetDoorGuardActivity.class, false);    //缓存个5
                            break;
                        case Constant.SETTING_TYPE4:     //系统设置
                            gotoActivity(SetSysActivity.class, false);
                            break;
                        default:
                            gotoActivity(SettingActivity.class, false);
                            break;
                    }
                } else {
                    dialogFragment.showError();
                }
            }
        }, isHideInput);
        dialogFragment.show(getSupportFragmentManager(), "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        tvPlaceName.setText(SPUtils.getString(Constant.DEVICE_NAME));
    }

    /**
     * 处理电池变化
     */
    @Override
    public void onElectricMessage(boolean isPlugs, int leverPercent) {
        LauncherApplication.sLevel = leverPercent;
        if (isPlugs) {
            battryPlug.setVisibility(View.VISIBLE);
            LauncherApplication.sIsCharge = 1;
        } else {
            battryPlug.setVisibility(View.GONE);
            LauncherApplication.sIsCharge = 0;
        }
        battryNum.setText(leverPercent + "%");
        battryView.setProgress(leverPercent);
    }

    /**
     * @param isNoNet         网络是否连接
     * @param WifiorNetStatus 连接是Wifi还是网线   0:网线 1: wifi
     * @param scanLever       wifi信号强度
     */
    @Override
    public void onCallMessage(boolean isNoNet, int WifiorNetStatus, int scanLever) {
        if (!isNoNet) {
            mIvSignal.setImageResource(R.drawable.wifi_signal_disconnect);
            isNetWork = false;
            return;
        }
        if (!isNetWork) {    //之前网络未连接，现在连接了
            initLocation();
            getData();
            //检测App更新
            UpdateManager.getUpdateManager().checkAppUpdate(this, getSupportFragmentManager(), false);
        }
        isNetWork = true;
        if (WifiorNetStatus == 0) {
            mIvSignal.setImageResource(R.drawable.net_line);
        } else {
            if (Math.abs(scanLever) > 100) {
                mIvSignal.setImageResource(R.drawable.wifi_signal_1);
            } else if (Math.abs(scanLever) > 80) {
                mIvSignal.setImageResource(R.drawable.wifi_signal_1);
            } else if (Math.abs(scanLever) > 70) {
                mIvSignal.setImageResource(R.drawable.wifi_signal_1);
            } else if (Math.abs(scanLever) > 60) {
                mIvSignal.setImageResource(R.drawable.wifi_signal_2);
            } else if (Math.abs(scanLever) > 50) {
                mIvSignal.setImageResource(R.drawable.wifi_signal_3);
            } else {
                mIvSignal.setImageResource(R.drawable.wifi_signal_3);
            }
        }
    }

    /**
     * 检测到人脸，返回人脸数据
     */
    @Override
    public void setOnBioFace(CvcRect cvcRect1, int[] rectWidth, int[] rectHeight) {
        Log.d(TAG, "setOnBioFace() called with: cvcRect1 = [" + cvcRect1 + "], rectWidth = [" + rectWidth + "], rectHeight = [" + rectHeight + "]");
        runOnUiThread(() -> {
            //T.showShort("检测到人脸");
            if (faceView != null) {
                faceView.setFaces(cvcRect1, cvcRect1.w, cvcRect1.h, rectWidth[0], rectHeight[0]);
            }
        });
        Message msg = new Message();
        msg.what = EventUtil.CVC_LIVINGFACE;
        mHandler.sendMessage(msg);
    }

    /**
     * 活体检测通过,调用阿里云识别人脸
     */
    @Override
    public void setOnBioAssay(int[] possibilityCode, byte[] faces, int[] lenght) {
        byte[] result = new byte[lenght[0]];
        UIHandler.sendEmptyMessage(0x22);
        synchronized (faces) {
            System.arraycopy(faces, 0, result, 0, lenght[0]);
        }
        httpUploadPic(result);
    }


    /**
     * 初始化外设
     */
    @Override
    public void initCallBack(int cvcStatus, int LedStatus, int MdStatus, int NfcStatus) {
        if (cvcStatus == 0 && LedStatus == 0 && MdStatus == 0 && NfcStatus == 0) {
            initError.setVisibility(View.GONE);
            if (initDialog != null && initDialog.isVisible()) {
                initDialog.dismiss();
            }
        } else {
            initError.setVisibility(View.VISIBLE);
            initDialog.setStatus(cvcStatus, LedStatus, MdStatus, NfcStatus, mHandler);
            UIHandler.sendEmptyMessage(0x11);
        }
    }

    Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    if (initDialog != null && initDialog.isVisible()) {
                        T.showShort("部分设备初始化失败！建议重启设备！");
                        initDialog.setInitFinish();
                    }
                    break;
                case 0x22:
                    T.showShort("真人概率大于50%，开始认证人脸！");
                    break;
            }
        }
    };


    /**
     * 人脸数据上传到阿里云进行识别
     */
    private void httpUploadPic(byte[] result) {
        AliYunOssUtils.getInstance(this).putObjectFromByteArray(result, new OssUploadListener() {
            @Override
            public void onSuccess(String filePath) {
                //自定义人脸识别post数据
                if (uuidFactory == null) {
                    uuidFactory = new DeviceUuidFactory(MainActivity.this);
                }
                uUid = uuidFactory.getUuid() + "";
                httpFaceVerifyPath(filePath, uUid);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException,
                                  ServiceException serviceException) {
                Log.i("oss-upload-fail", clientException.getMessage());
                Log.i("oss-upload-fail",
                        serviceException.getErrorCode() + ":" + serviceException.getRawMessage());
            }
        });
    }

    /**
     * 判断返回数据是否成功，成功则开门
     */
    private void httpFaceVerifyPath(String filePath, String uuid) {
        String myType = String.valueOf(SPUtils.getInt(Constant.DEVICE_TYPE));
        Map<String, Object> params = new HashMap<>();
        params.put("image_url", filePath);
        params.put("uuid", uuid);
        params.put("type", myType);
        addSubscription(ApiService.verifyFace(params).subscribe(new Subscriber<VerifyBean>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(VerifyBean model) {
                //加上isVerified是为了防止出现多次验证成功
                if (model.isIsmatch()) {
                    //                    SoundPlayUtils.play(3);//播放声音
                    Log.i("wuliang", "face scusess!!!!");
                    Toast.makeText(MainActivity.this, "身份验证成功，请开门！", Toast.LENGTH_SHORT).show();
                    T.showShort("身份验证成功，请开门！");
                } else {
                    //                    SoundPlayUtils.play(1);//播放声音
                    //                        SoundPlayUtils.play(2);//播放声音
                    Log.i("wuliang", "face no  no   no!!!!");
                    Toast.makeText(MainActivity.this, "身份验证错误！", Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }


    private static class StaticHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    // todo 内存泄漏这里需要处理
    private final Runnable mRefreshTimeRunnable = new Runnable() {
        @Override
        public void run() {
            mTvTime.setText(DateUtil.getTime());
            mTvDate.setText(DateUtil.getDate());
            mTvWeek.setText(DateUtil.getWeek());
            mStaticHandler.postDelayed(this, REFRESH_DELAY);
        }
    };


    @Override
    protected void onStop() {
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        unregisterReceiver(mBatteryReceiver);
        unregisterReceiver(netChangeBroadcastRecever);
        mStaticHandler.removeCallbacksAndMessages(null);
        CvcHelper.CVC_deinit();
    }
}