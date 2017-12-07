package rk.device.launcher.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cvc.CvcHandler;
import cvc.CvcHelper;
import cvc.CvcRect;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.base.utils.TypeTranUtils;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.AddressModel;
import rk.device.launcher.bean.SetPageContentBean;
import rk.device.launcher.bean.VerifyBean;
import rk.device.launcher.bean.WeatherModel;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.LauncherApplication;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.utils.gps.GpsCallback;
import rk.device.launcher.utils.gps.GpsUtils;
import rk.device.launcher.utils.oss.AliYunOssUtils;
import rk.device.launcher.utils.oss.OssUploadListener;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.widget.BatteryView;
import rk.device.launcher.widget.DetectedFaceView;
import rk.device.launcher.widget.UpdateManager;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseCompatActivity implements View.OnClickListener {

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
    private Camera camera;
    private SurfaceHolder surfaceholder;
    Camera.Parameters parameters;
    @Bind(R.id.face_view)
    DetectedFaceView faceView;
    @Bind(R.id.et_width)
    EditText widthEt;
    @Bind(R.id.et_height)
    EditText heightEt;
	@Bind(R.id.tv_declare)
	TextView mTvDeclare;

    private CvcHandler mHandler = null;

    private static final int REFRESH_DELAY = 1000;

    private StaticHandler mStaticHandler = new StaticHandler();
    private DeviceUuidFactory uuidFactory = null;
    private String uUid;
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
    private WifiHelper mWifiHelper;
    private GpsUtils gpsUtils = null;
	private Subscription mSubscription;
	
	@Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setOnClick(R.id.tv_sure, R.id.tv_finish);
        mWifiHelper = new WifiHelper(this);
        registerBatteryReceiver();
        registerNetReceiver();
        initLocation();
        settingTv.setOnClickListener(this);
        initSurfaceViewOne();
	    registerRxBus();
	    startService(new Intent(this, SocketService.class));
    }
	
	private void registerRxBus() {
		mSubscription = RxBus.getDefault().toObserverable(SetPageContentBean.class).subscribe(new Action1<SetPageContentBean>() {
			@Override
			public void call(SetPageContentBean setPageContentBean) {
				if (mTvDeclare != null) {
					mTvDeclare.setText(setPageContentBean.content);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				
			}
		});
	}

    private void initSurfaceViewOne() {
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(new SurfaceHolderCallbackFont());
    }

    private CvcRect cvcRect = new CvcRect();
    private Rect[] rect = new Rect[1];


    class SurfaceHolderCallbackFont implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (camera != null) {
                    camera.setPreviewDisplay(holder);
                    return;
                }
                openCamera(holder);
            } catch (IOException e) {
                e.printStackTrace();
//                    camera.release();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "width = [" + width + "], height = [" + height + "]");
            faceView.setRoomHeight(height);
            if (null != camera) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            parameters = camera.getParameters();
                            parameters.setPictureFormat(PixelFormat.JPEG);
                            parameters
                                    .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                            //                        setDisplay(parameters, camera);\
                            parameters.setPictureSize(640, 480);
                            parameters.setPreviewSize(width, height);
                            camera.setParameters(parameters);
                            camera.startPreview();
                            camera.cancelAutoFocus();
                        }
                    }
                });
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surface被干掉了！！！！！");
        }
    }


    int faceCount = 0;//记录摄像头采集的画面帧数,每5帧调取一次人脸识别

    /**
     * 第一次进入页面，开始打开camera
     */
    private void openCamera(SurfaceHolder holder) throws IOException {
        // 获取camera对象
        int cameraCount = Camera.getNumberOfCameras();
        Log.d(TAG, cameraCount + "");
        if (cameraCount == 2) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (null != camera) {
            // 设置预览监听
            camera.setPreviewDisplay(holder);
            // 启动摄像头预览
            camera.startPreview();
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    faceCount++;
                    if (faceCount % 5 != 0) {
                        return;
                    }
                    Message message = new Message();
                    message.what = EventUtil.CVC_DETECTFACE;
                    message.obj = cvcRect;
                    mHandler.setOnBioAssay(new CvcHandler.OnBioAssay() {
                        @Override
                        public void setOnBioFace(CvcRect cvcRect, int[] rectWidth, int[] rectHeight) {
                            Rect oneRect = new Rect();
                            oneRect.set(cvcRect.x, cvcRect.y, cvcRect.x + cvcRect.w,
                                    cvcRect.y + cvcRect.h);
                            rect[0] = oneRect;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //T.showShort("检测到人脸");
                                    faceView.setFaces(rect, cvcRect.w, cvcRect.h, rectWidth[0], rectHeight[0]);
                                }
                            });
                            Log.i("CVC_detectFace", "success:" + cvcRect.x + ":" + cvcRect.y
                                    + ";width:" + rectWidth[0] + ",height:" + rectHeight[0]);
                            Message msg = new Message();
                            msg.what = EventUtil.CVC_LIVINGFACE;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void setOnBioAssay(int[] possibilityCode, byte[] faces, int[] lenght) {
                            byte[] result = new byte[lenght[0]];
                            synchronized (faces) {
                                System.arraycopy(faces, 0, result, 0, lenght[0]);
                            }
                            httpUploadPic(result);
                        }
                    });
                    mHandler.sendMessage(message);
                    faceCount = faceCount == 25 ? 0 : faceCount;
                }
            });
        }
    }


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

    /**
     * 显示输入管理员密码弹窗
     */
    private InputWifiPasswordDialogFragment dialogFragment = null;

    private void showDialogFragment(String title,
                                    InputWifiPasswordDialogFragment.OnConfirmClickListener listener) {
        dialogFragment = InputWifiPasswordDialogFragment.newInstance();
        dialogFragment.setTitle(title);
        dialogFragment.setOnCancelClickListener(
                new InputWifiPasswordDialogFragment.onCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        dialogFragment.dismiss();
                    }
                }).setOnConfirmClickListener(listener);
    }

    @Override
    protected void initData() {
	    String declareContent = SPUtils.getString(Constant.KEY_FIRSTPAGE_CONTENT);
	    mTvDeclare.setText(declareContent);
	    //检测App更新
        UpdateManager.getUpdateManager().checkAppUpdate(this, getSupportFragmentManager(), false);
        initHandlerThread();
    }

    /**
     * 初始化Cvc
     */
    private void initHandlerThread() {
        HandlerThread thread = new HandlerThread("new_thread");
        thread.start();
        Looper looper = thread.getLooper();
        mHandler = new CvcHandler(looper);
        int code = CvcHelper.CVC_init();
        Log.d(TAG, code + "    ------cvcCode");
        if (code != 0) {
            T.showShort("load cvc error...");
        }
    }

    /**
     * 注册网络监听
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

    private BroadcastReceiver mNetChangeBroadcastReceiver = new BroadcastReceiver() {
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

    /**
     * wifi变化设置不同的wifi图标
     *
     * @param scanResult
     */
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

    /**
     * 获取地理位置
     */
    private void initLocation() {
        if (gpsUtils == null) {
            gpsUtils = new GpsUtils(this);
        }
	    if (gpsUtils.isLoactionAvailable()) { // 定位可用, 通过定位获取地址
		    gpsUtils.initLocation(new GpsCallback() {
			    @Override
			    public void onResult(List<Address> address) {
				    if (address.size() > 0) {
					    String area = address.get(0).getSubAdminArea();
					    LogUtil.d(TAG, "area = " + area);
					    SPUtils.putString(Constant.KEY_ADDRESS, area);
					    httpGetWeather(area);
				    }
			    }
		    });
	    } else { // 定位不可用, 通过IP获取地址
		    addSubscription(
		        ApiService.address("js")
		            .subscribeOn(Schedulers.io())
		            .flatMap(new Func1<String, Observable<WeatherModel>>() {
			            @Override
			            public Observable<WeatherModel> call(String s) {
				            int start = s.indexOf("{");
				            int end = s.indexOf("}");
				            String json = s.substring(start, end + 1);
				            AddressModel addressModel = JSON.parseObject(json, AddressModel.class);
				            Map params = new HashMap();
				            params.put("city", addressModel.city);
				            return ApiService.weather(params);
			            }
		            })
		            .observeOn(AndroidSchedulers.mainThread())
		            .subscribe(new Subscriber<WeatherModel>() {
			            @Override
			            public void onCompleted() {
				
			            }
			
			            @Override
			            public void onError(Throwable e) {
				
			            }
			
			            @Override
			            public void onNext(WeatherModel weatherModel) {
				            showWeather(weatherModel);
			            }
		            })
		    );
	    }
	    
    }

    /**
     * 天气Api
     *
     * @param area
     */
    private void httpGetWeather(String area) {
        Map params = new HashMap();
        params.put("city", area);
        addSubscription(ApiService.weather(params).subscribe(new Subscriber<WeatherModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(WeatherModel weatherModel) {
	            showWeather(weatherModel);
            }
        }));

    }
	
	private void showWeather(WeatherModel weatherModel) {
		List<WeatherModel.ResultsBean> beanList = weatherModel.getResults();
		if (beanList.size() > 0 && beanList.get(0).getDaily().size() > 0) {
		    WeatherModel.ResultsBean.DailyBean detailBean = beanList.get(0).getDaily()
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
            case R.id.tv_sure:
                String width = widthEt.getText().toString();
                String height = heightEt.getText().toString();
                if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
                    Log.i("CVC_determineLivingFace", "请设置宽高");
                    return;
                }
                if (mHandler != null) {
                    Message msg = new Message();
                    msg.what = EventUtil.START_CVC;
                    msg.arg1 = TypeTranUtils.str2Int(width);
                    msg.arg2 = TypeTranUtils.str2Int(height);
                    mHandler.sendMessageDelayed(msg, 10);
                }
                break;
            case R.id.tv_finish:
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(EventUtil.START_CALIBRATION, 10);
                }
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
	        LauncherApplication.sLevel = levelPercent;
	        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);
	        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
		        LauncherApplication.sIsCharge = 1;
	        } else {
		        LauncherApplication.sIsCharge = 0;
	        }
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

    private static class StaticHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != camera) {
            closeCamera(camera);
        }
	    if (!mSubscription.isUnsubscribed()) {
		    mSubscription.unsubscribe();
	    }
        unregisterReceiver(mBatteryReceiver);
        unregisterReceiver(mNetChangeBroadcastReceiver);
        mStaticHandler.removeCallbacksAndMessages(null);
        CvcHelper.CVC_deinit();
    }

    private void closeCamera(Camera camera) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStop() {
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }
}
