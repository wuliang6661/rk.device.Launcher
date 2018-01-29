package rk.device.launcher.ui.main.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.List;

import butterknife.Bind;
import cvc.CvcHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.SetPageContentBO;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.bean.event.IpHostEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.service.VerifyService;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.ui.key.KeyActivity;
import rk.device.launcher.ui.setting.SetBasicInfoActivity;
import rk.device.launcher.ui.setting.SetDoorGuardActivity;
import rk.device.launcher.ui.setting.SetNetWorkActivity;
import rk.device.launcher.ui.setting.SetSysActivity;
import rk.device.launcher.ui.setting.SettingActivity;
import rk.device.launcher.ui.settingmangerpwd.SettingMangerPwdActivity;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.SoundPlayUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.key.KeyUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.utils.video.SampleListener;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaBack;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HomeActivity extends MVPBaseActivity<HomeContract.View, HomePresenter> implements HomeContract.View {

    @Bind(R.id.default_video)
    StandardGSYVideoPlayer defaultVideo;
    @Bind(R.id.device_time)
    TextView deviceTime;
    @Bind(R.id.device_name)
    TextView deviceName;
    @Bind(R.id.gonggao_text)
    TextView gonggaoText;
    @Bind(R.id.guanggao01)
    ImageView guanggao01;
    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.frame_layout)
    FrameLayout frameLayout;
    @Bind(R.id.guanggao02)
    ImageView guanggao02;
    @Bind(R.id.guanggao03)
    ImageView guanggao03;


    JniHandler mHandler;

    private InputWifiPasswordDialogFragment dialogFragment = null;
    SurfaceHolderCaremaFont callbackFont;

    /**
     * 定时器，每一秒运行一次
     */
    private static final int REFRESH_DELAY = 1000;
    private Handler mStaticHandler = new Handler();

    private OrientationUtils orientationUtils;
    private boolean isPlay;

    @Override
    protected int getLayout() {
        return R.layout.act_new_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        invition();
        invitionVideo();
        register();
        resolveNormalVideoUI();
    }

    /**
     * 初始化界面
     */
    private void invition() {
        SoundPlayUtils.init(this);
        initSurfaceViewOne();
//        showView();
    }


    /**
     * 注册各类事物
     */
    private void register() {
        mHandler = JniHandler.getInstance();
        mPresenter.initSO();
        mPresenter.registerNetOffReceiver();
        registerRxBus();
        registerIPHost();
        mPresenter.getData();
        startSocketService();
    }

    /**
     * 初始化布局显示
     */
    private void showView() {
        String declareContent = SPUtils.getString(Constant.KEY_FIRSTPAGE_CONTENT);
        if (!TextUtils.isEmpty(declareContent)) {
            gonggaoText.setText(String.format(getString(R.string.declare_content), declareContent, declareContent));
        } else {
            gonggaoText.setText("");
        }
    }

    /**
     * 接收推送通知的公告并显示
     */
    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(SetPageContentBO.class).subscribe(setPageContentBean -> {
            if (!TextUtils.isEmpty(setPageContentBean.content)) {
                gonggaoText.setText(String.format(getString(R.string.declare_content), setPageContentBean.content, setPageContentBean.content));
            } else {
                gonggaoText.setText("");
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
            public void callMessage(byte[] data, int width, int height) {
                if (width != 0 && height != 0) {
                    if (FaceUtils.getInstance().isStartFace()) {
                        FaceUtils.getInstance().caremeDataToFace(data, width, height);
                    }
                }
            }

            @Override
            public void callHeightAndWidth(int width, int height) {
            }
        });
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        deviceName.setText(SPUtils.getString(Constant.DEVICE_NAME));
        new Handler().postDelayed(() -> {
            if (!KeyUtils.isHaveKey()) {
                setFirstLoder();
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        if (isPlay) {
            getCurPlay().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        mStaticHandler.removeCallbacksAndMessages(null);
        CvcHelper.CVC_deinit();
        mPresenter.unRegisterReceiver(this);
        SurfaceHolderCaremaFont.stopCarema();
        SurfaceHolderCaremaBack.stopCarema();
        FaceUtils.getInstance().stopFaceFR();
        stopService(new Intent(this, SocketService.class));
        stopService(new Intent(this, VerifyService.class));
        super.onDestroy();
    }

    private void resolveNormalVideoUI() {
        //增加title
        defaultVideo.getTitleTextView().setVisibility(View.GONE);
        defaultVideo.getBackButton().setVisibility(View.GONE);
    }

    private GSYVideoPlayer getCurPlay() {
        if (defaultVideo.getFullWindowPlayer() != null) {
            return defaultVideo.getFullWindowPlayer();
        }
        return defaultVideo;
    }

    /**
     * 设置初始流程（改）
     */
    private void setFirstLoder() {
        int type = SPUtils.getInt(Constant.SETTING_NUM, Constant.SETTING_TYPE1);
        final String password = SPUtils.getString(Constant.KEY_PASSWORD);
        if (!StringUtils.isEmpty(password)) {
            showManagerDialog(type);
        } else {     //如果密码为空，设置流程一定没走完
            goToSetting(type);
        }
    }


    /**
     * 显示输入管理员密码进入设置
     */
    private void showManagerDialog(int type) {
        showDialogFragment("请输入管理员密码", content -> {
            if (StringUtils.isEmpty(content)) {
                dialogFragment.showError("请输入管理员密码！");
                return;
            }
            if (content.length() != 6) {
                dialogFragment.showError("请输入完整密码！");
                return;
            }
            final String password = SPUtils.getString(Constant.KEY_PASSWORD);
            if (TextUtils.equals(password, content)) { //密码输入正确
                dialogFragment.dismiss();
                if (type == -1000) {
                    gotoActivity(SettingActivity.class, false);
                } else {
                    goToSetting(type);
                }
            } else {
                dialogFragment.showError();
            }
        }, true);
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    /**
     * 页面跳转
     */
    private void goToSetting(int type) {
        switch (type) {
            case Constant.SETTING_TYPE1:    //网络设置
                gotoActivity(SetNetWorkActivity.class, false);
                break;
            case Constant.SETTING_TYPE2:   //设置激活码
                gotoActivity(KeyActivity.class, false);
                break;
            case Constant.SETTING_TYPE3:   //设置管理员密码
                gotoActivity(SettingMangerPwdActivity.class, false);
                break;
            case Constant.SETTING_TYPE4:   //门禁设置
                gotoActivity(SetDoorGuardActivity.class, false);
                break;
            case Constant.SETTING_TYPE5:   //系统设置
                gotoActivity(SetSysActivity.class, false);
                break;
            case Constant.SETTING_TYPE6:    //基础设置
                gotoActivity(SetBasicInfoActivity.class, false);
                break;
        }
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
     * 初始化视频控制
     */
    private void invitionVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, defaultVideo);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setIsTouchWiget(false)
                .setHideKey(true)
                .setLooping(true)
                .setRotateViewAuto(false)
                .setShowFullAnimation(false)
                .setNeedShowWifiTip(false)
                .setNeedLockFull(true)
                .setSeekRatio(1)
                .setUrl("http://dxm.happydoit.com:80/upload/20171108/237QeWW.mp4")
                .setCacheWithPlay(false)
                .setVideoTitle("课程介绍")
                .setStandardVideoAllCallBack(new SampleListener() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        isPlay = true;
                    }
                })
                .build(defaultVideo);
        defaultVideo.startPlayLogic();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            if (!TextUtils.isEmpty(scanResult)) {
                T.showShort(scanResult);
            }
        }
    }


    @Override
    public void showWeather(List<WeatherBO> weatherModel) {

    }

    @Override
    public void setAnimationIp(String AnimationIp) {

    }

    @Override
    public void showSuress(String text) {

    }
}
