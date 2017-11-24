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
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.utils.CommonUtils;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.ThreadUtils;
import rk.device.launcher.utils.carema.CameraInterface;
import rk.device.launcher.widget.CameraSurfaceView;
import rk.device.launcher.widget.UpdateManager;

import static rk.device.launcher.utils.DateUtil.getTime;

public class MainActivity extends BaseCompatActivity implements View.OnClickListener {

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
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
    @Bind(R.id.surface_view)
    CameraSurfaceView mSurfaceView;
    private Camera camera;
    private SurfaceHolder surfaceholder;
    Camera.Parameters parameters;

    private int PREVIEW_WIDTH = 1280;
    private int PREVIEW_HEIGHT = 720;

    private LocationManager mLocationManager;
    private final String TAG = "MainActivity";
    private static final int REFRESH_DELAY = 1000;

    private StaticHandler mStaticHandler = new StaticHandler();
    // todo 内存泄漏这里需要处理
    private final Runnable mRefreshTimeRunnable = new Runnable() {

        @Override
        public void run() {
            mTvTime.setText(getTime());
            mTvDate.setText(DateUtil.getDate());
            mTvWeek.setText(DateUtil.getWeek());
            mStaticHandler.postDelayed(this, REFRESH_DELAY);
        }
    };


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        registerBatteryReceiver();
        initLocation();
        settingTv.setOnClickListener(this);
//        startGoogleFaceDetect();
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


    /**
     * 后置摄像头回调
     */
    class SurfaceHolderCallbackBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // 获取camera对象
            int cameraCount = Camera.getNumberOfCameras();
            if (cameraCount > 0) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                if (null != camera) {
                    try {
                        // 设置预览监听
                        camera.setPreviewDisplay(holder);
                        // 启动摄像头预览
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                        camera.release();
                    }
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (null != camera) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            parameters = camera.getParameters();
//                            parameters.setPictureFormat(PixelFormat.JPEG);
                            parameters.setPictureFormat(ImageFormat.NV21);
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                            parameters.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            // 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
//                            parameters.setRotation(90);
                            camera.setParameters(parameters);
                            camera.setDisplayOrientation(180);
                            camera.startPreview();
                            camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                        }
                    }
                });
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    private InputWifiPasswordDialogFragment dialogFragment = null;

    private void showDialogFragment(String title, InputWifiPasswordDialogFragment.OnConfirmClickListener listener) {
        if (dialogFragment == null) {
            dialogFragment = InputWifiPasswordDialogFragment.newInstance();
        }
        dialogFragment.setTitle(title);
        dialogFragment.setOnCancelClickListener(new InputWifiPasswordDialogFragment.onCancelClickListener() {
            @Override
            public void onCancelClick() {
                dialogFragment.dismiss();
            }
        }).setOnConfirmClickListener(listener);
    }

    @Override
    protected void initData() {

        UpdateManager.getUpdateManager().checkAppUpdate(this, getSupportFragmentManager(), false);
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
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    LogUtil.d("充电中");
                    break;
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
        }
    };

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
                    Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        Log.d(TAG, "gps.location = null");
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    Log.d(TAG, "network.location = " + location);
                    Geocoder geocoder = new Geocoder(CommonUtils.getContext(), Locale.getDefault());
                    if (location == null) {
                        return;
                    }
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.size() > 0) {
                            Address address = addresses.get(0);
                            String city = address.getLocality();
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
                final String password = SPUtils.getString(Constant.KEY_PASSWORD);

                if (TextUtils.isEmpty(password)) {
                    showDialogFragment("设置管理员密码", new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick(String content) {
                            dialogFragment.dismiss();
                            if (!TextUtils.isEmpty(content)) {
                                SPUtils.putString(Constant.KEY_PASSWORD, content);
                                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    showDialogFragment("请输入管理员密码", new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick(String content) {
                            if (TextUtils.equals(password, content)) {
                                dialogFragment.dismiss();
                                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                startActivity(intent);
                            } else {
                                dialogFragment.showError();
                            }
                        }
                    });
                }
                dialogFragment.show(getSupportFragmentManager(), "");
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
            if (StringUtils.isEmpty(password)) {              //设置管理员密码，判断为第一次进入
                if (!TextUtils.isEmpty(content)) {
                    SPUtils.putString(Constant.KEY_PASSWORD, content);    //缓存密码
                    gotoActivity(SetBasicInfoActivity.class, false);
                    //进入基础设置
                }
            } else {                 //本地存在密码,则按缓存序号，跳入未设置页面
                if (TextUtils.equals(password, content)) {    //密码输入正确
                    int type = SPUtils.getInt(Constant.SETTING_NUM, 1);
                    switch (type) {
                        case 1:         //进入基础设置
                            gotoActivity(SetBasicInfoActivity.class, false);
                            break;
                        case 2:           //蓝牙设置

                            break;
                        case 3:         //网络设置
                            gotoActivity(SetNetWorkActivity.class, false);
                            break;
                        case 4:       //门禁设置
                            gotoActivity(SetDoorGuardActivity.class, false);
                            break;
                        case 5:     //系统设置

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


    private static class StaticHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        mStaticHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mBatteryReceiver);
        super.onDestroy();
        if (null != camera) {
//            closeCamera(camera);
        }
    }

    private void closeCamera(Camera camera) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
    }

    @Override
    protected void onStop() {
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

}
