package rk.device.launcher.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cvc.CvcHelper;
import cvc.CvcRect;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.SetPageContentBO;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.bean.event.IpHostEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastReceiver;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.ui.activity.SetBasicInfoActivity;
import rk.device.launcher.ui.activity.SetDoorGuardActivity;
import rk.device.launcher.ui.activity.SetNetWorkActivity;
import rk.device.launcher.ui.activity.SetSysActivity;
import rk.device.launcher.ui.activity.SettingActivity;
import rk.device.launcher.ui.call.CallActivity;
import rk.device.launcher.ui.fragment.InitErrorDialogFragmen;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.SoundPlayUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.widget.BatteryView;
import rk.device.launcher.widget.GifView;
import rk.device.launcher.widget.carema.DetectedFaceView;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;


/**
 * MVPPlugin
 * <p>
 * 主页面的界面处理
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements
        MainContract.View, JniHandler.OnInitListener, JniHandler.OnBioAssay, View.OnClickListener, ElectricBroadcastReceiver.CallBack,
        NetChangeBroadcastReceiver.CallBack {


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
    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.tv_tem)
    TextView temTv;
    @Bind(R.id.tv_weather)
    TextView weatherTv;
    @Bind(R.id.face_view)
    DetectedFaceView faceView;
    @Bind(R.id.tv_declare)
    TextView mTvDeclare;
    @Bind(R.id.tv_place_name)
    TextView tvPlaceName;
    @Bind(R.id.init_error)
    ImageView initError;
    @Bind(R.id.carema_bg)
    GifView caremaBg;
    @Bind(R.id.device_name_bg)
    GifView deviceNameBg;
    @Bind(R.id.suress_text)
    TextView suressText;
    @Bind(R.id.suress_layout)
    LinearLayout suressLayout;

    JniHandler mHandler;
    private boolean isNetWork = true;// 此状态保存上次网络是否连接，默认已连接
    private InitErrorDialogFragmen initDialog;
    private InputWifiPasswordDialogFragment dialogFragment = null;
    SurfaceHolderCaremaFont callbackFont;

    /**
     * 记录每五帧调取一次人脸识别
     */
    int faceCount = 0;
    /**
     * 物管电话
     */
    private String modilePhone;
    /**
     * 定时器，每一秒运行一次
     */
    private static final int REFRESH_DELAY = 1000;
    private Handler mStaticHandler = new Handler();
    /**
     * 是否开启人脸框
     */
    private static final boolean isFaceCode = false;


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        invition();
        register();
    }

    /**
     * 初始化界面
     */
    private void invition() {
        initDialog = InitErrorDialogFragmen.newInstance();
        SoundPlayUtils.init(this);
        settingTv.setOnClickListener(this);
        setOnClick(R.id.rl_contact_manager, R.id.init_error, R.id.num_pass_layout, R.id.call_layout, R.id.qr_code_layout, R.id.liuyan_layout);
        initSurfaceViewOne();
        showView();
    }


    /**
     * 注册各类事物
     */
    private void register() {
        mHandler = mPresenter.initJni();
        mHandler.setOnInitListener(this);
        mPresenter.registerBatteryReceiver().setCallBack(this);
        mPresenter.registerNetReceiver().setCallBack(this);
        mPresenter.registerNetOffReceiver();
        registerRxBus();
        registerIPHost();
        mPresenter.initLocation(this);
        mPresenter.getData();
        startService(new Intent(this, SocketService.class));
    }


    /**
     * 初始化布局显示
     */
    private void showView() {
        caremaBg.setMovieResource(R.raw.camera_bg);
        deviceNameBg.setMovieResource(R.raw.device_name_bg);
        String declareContent = SPUtils.getString(Constant.KEY_FIRSTPAGE_CONTENT);
        if (!TextUtils.isEmpty(declareContent)) {
            mTvDeclare.setVisibility(View.VISIBLE);
            mTvDeclare.setText(String.format(getString(R.string.declare_content), declareContent, declareContent));
        } else {
            mTvDeclare.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mStaticHandler.post(mRefreshTimeRunnable);
    }


    @Override
    protected void onResume() {
        super.onResume();
        tvPlaceName.setText(SPUtils.getString(Constant.DEVICE_NAME));
//        if (isIpError) {
//            showMessageDialog("服务器数据获取出错！\n\n请检查网络或IP地址是否可用！");
//        }
    }


    @Override
    protected void onStop() {
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mBatteryReceiver);
//        unregisterReceiver(netChangeBroadcastRecever);
//        unregisterReceiver(netOffReceiver);
        mStaticHandler.removeCallbacksAndMessages(null);
        CvcHelper.CVC_deinit();
    }


    @Override
    public void onRequestError(String msg) {

    }

    @Override
    public void onRequestEnd() {

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
                faceView.setRoomHeight(width, height);
            }
        });
    }


    /**
     * Jni初始化的返回值
     */
    @Override
    public void initCallBack(int cvcStatus, int LedStatus, int MdStatus, int NfcStatus) {
        runOnUiThread(() -> {
            if (cvcStatus == 0 && LedStatus == 0 && NfcStatus == 0) {
                initError.setVisibility(View.GONE);
                if (initDialog != null && initDialog.isVisible()) {
                    initDialog.dismiss();
                }
            } else {
                initError.setVisibility(View.VISIBLE);
                initDialog.setStatus(cvcStatus, LedStatus, MdStatus, NfcStatus, mHandler);
                if (initDialog != null && initDialog.isVisible()) {
                    T.showShort("部分设备初始化失败！建议重启设备！");
                    initDialog.setInitFinish();
                }
            }
        });
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
        battryNum.setText(String.valueOf(leverPercent + "%"));
        battryView.setProgress(leverPercent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.num_pass_layout:    //密码开门

                break;
            case R.id.call_layout:    //拨号
                gotoActivity(CallActivity.class, false);
                break;
            case R.id.qr_code_layout:    //二维码

                break;
            case R.id.liuyan_layout:    //留言

                break;
        }
    }


    /**
     * 初始设置流程加载
     */
    private void settingLoad() {
        if (dialogFragment != null && dialogFragment.isVisible()) {
            return;
        }
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
     * 处理网络状态变化
     */
    @Override
    public void onCallMessage(boolean isNoNet, int WifiorNetStatus, int scanLever) {
        if (!isNoNet) {
            mIvSignal.setImageResource(R.drawable.wifi_signal_disconnect);
            isNetWork = false;
            return;
        }
        if (!isNetWork) {    //之前网络未连接，现在连接了
            mPresenter.initLocation(this);
            mPresenter.getData();
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
     * 接收推送通知的公告并显示
     */
    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(SetPageContentBO.class).subscribe(setPageContentBean -> {
            if (!TextUtils.isEmpty(setPageContentBean.content)) {
                mTvDeclare.setVisibility(View.VISIBLE);
                mTvDeclare.setText(String.format(getString(R.string.declare_content), setPageContentBean.content, setPageContentBean.content));
            } else {
                mTvDeclare.setVisibility(View.INVISIBLE);
            }
        }, throwable -> {

        }));
    }


    /**
     * 接收服务器IP地址更改，所有数据重新请求
     */
    private void registerIPHost() {
        RxBus.getDefault().toObserverable(IpHostEvent.class).subscribe(ipHostEvent -> {
            mPresenter.initLocation(this);
            mPresenter.getData();
        }, throwable -> {

        });
    }


    /**
     * 显示天气
     */
    @Override
    public void showWeather(List<WeatherBO> weatherModel) {
        if (weatherModel.size() > 0 && weatherModel.get(0).getDaily().size() > 0) {
            WeatherBO.DailyBean detailBean = weatherModel.get(0).getDaily()
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
    public void setAnimationIp(String AnimationIp) {
        this.modilePhone = AnimationIp;
    }

    /**
     * 检测到人脸，返回人脸数据
     */
    @Override
    public void setOnBioFace(CvcRect cvcRect1, int[] rectWidth, int[] rectHeight) {
        Message msg = new Message();
        msg.what = EventUtil.CVC_LIVINGFACE;
        mHandler.sendMessage(msg);
        if (!isFaceCode) {
            return;
        }
        runOnUiThread(() -> {
            //T.showShort("检测到人脸");
            if (faceView != null) {
                faceView.setFaces(cvcRect1, cvcRect1.w, cvcRect1.h, rectWidth[0], rectHeight[0]);
            }
        });
    }

    /**
     * 活体检测通过,调用阿里云识别人脸
     */
    @Override
    public void setOnBioAssay(int[] possibilityCode, byte[] faces, int[] lenght) {
        byte[] result = new byte[lenght[0]];
        synchronized (faces) {
            System.arraycopy(faces, 0, result, 0, lenght[0]);
        }
        mPresenter.httpUploadPic(result);
    }


    /**
     * 身份验证成功，文字显示
     */
    public void showSuress(String text) {
        suressText.setText("欢迎" + text + "回家");
        suressLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> suressLayout.setVisibility(View.GONE), 2000);
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
}