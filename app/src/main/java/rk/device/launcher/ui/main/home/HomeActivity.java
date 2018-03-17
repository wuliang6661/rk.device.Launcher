package rk.device.launcher.ui.main.home;


import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.donkingliang.banner.CustomBanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.SetPageContentBO;
import rk.device.launcher.bean.event.ADupdateEvent;
import rk.device.launcher.bean.event.DestoryEvent;
import rk.device.launcher.bean.event.IpHostEvent;
import rk.device.launcher.db.AdHelper;
import rk.device.launcher.db.entity.AD;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.service.NetChangeBroadcastReceiver;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.service.VerifyService;
import rk.device.launcher.ui.bbs.BbsActivity;
import rk.device.launcher.ui.call.CallActivity;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.ui.key.KeyActivity;
import rk.device.launcher.ui.numpassword.NumpasswordActivity;
import rk.device.launcher.ui.setting.SetBasicInfoActivity;
import rk.device.launcher.ui.setting.SetDoorGuardActivity;
import rk.device.launcher.ui.setting.SetNetWorkActivity;
import rk.device.launcher.ui.setting.SetSysActivity;
import rk.device.launcher.ui.setting.SettingActivity;
import rk.device.launcher.ui.settingmangerpwd.SettingMangerPwdActivity;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.SoundPlayUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.key.KeyUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.widget.ArcMenu;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaBack;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;
import rk.device.launcher.zxing.decode.CaptureActivity;
import rk.device.server.service.AppHttpServerService;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HomeActivity extends MVPBaseActivity<HomeContract.View, HomePresenter> implements HomeContract.View,
        View.OnClickListener, NetChangeBroadcastReceiver.CallBack {

    @Bind(R.id.device_time)
    TextView deviceTime;
    @Bind(R.id.device_name)
    TextView deviceName;
    @Bind(R.id.gonggao_text)
    TextView gonggaoText;
    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.menu_message)
    LinearLayout menuMessage;
    @Bind(R.id.menu_manager)
    LinearLayout menuManager;
    @Bind(R.id.menu_setting)
    LinearLayout menuSetting;
    @Bind(R.id.menu_pwd)
    LinearLayout menuPwd;
    @Bind(R.id.menu_call)
    LinearLayout menuCall;
    @Bind(R.id.menu_qrcode)
    LinearLayout menuQrcode;
    @Bind(R.id.menu)
    ArcMenu menu;
    @Bind(R.id.custom)
    CustomBanner custom;

    private InputWifiPasswordDialogFragment dialogFragment = null;
    SurfaceHolderCaremaFont callbackFont;

    /**
     * 定时器，每一秒运行一次
     */
    private static final int REFRESH_DELAY = 1000;
    private Handler mStaticHandler = new Handler();
    private boolean isNetWork = true;// 此状态保存上次网络是否连接，默认已连接

    RefreshTimeRunnable mRefreshTimeRunnable;

    private List<AD> beans;

    @Override
    protected int getLayout() {
        return R.layout.act_new_home;
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
        mPresenter.registerNetReceiver().setCallBack(this);
        mRefreshTimeRunnable = new RefreshTimeRunnable(this);
        SoundPlayUtils.init(this);
        initSurfaceViewOne();
        menuCall.setOnClickListener(this);
        menuManager.setOnClickListener(this);
        menuMessage.setOnClickListener(this);
        menuPwd.setOnClickListener(this);
        menuQrcode.setOnClickListener(this);
        menuSetting.setOnClickListener(this);
        showView();
        beans = new ArrayList<>();
        beans.add(new AD());
        beans.add(new AD());
        beans.add(new AD());
        List<AD> ads = AdHelper.queryAll();
        for (AD ad : ads) {
            beans.add(ad);
        }
        setAdAdapter(beans);
    }

    /**
     * 动态设置广告
     */
    private void setAdAdapter(List<AD> ads) {
        custom.setPages(new CustomBanner.ViewCreator<AD>() {
            @Override
            public View createView(Context context, int position) {
                //这里返回的是轮播图的项的布局 支持任何的布局
                //position 轮播图的第几个项
                LogUtil.e("new image");
                return new ImageView(context);
            }

            @Override
            public void updateUI(Context context, View view, int position, AD data) {
                ImageView imageView = (ImageView) view;
                if (StringUtils.isEmpty(data.getAdID())) {
                    switch (position) {
                        case 0:
                            imageView.setImageResource(R.drawable.guanggao01);
                            break;
                        case 1:
                            imageView.setImageResource(R.drawable.guanggao02);
                            break;
                        case 2:
                            imageView.setImageResource(R.drawable.guanggao03);
                            break;
                    }
                } else {
                    Glide.with(HomeActivity.this).load(data.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                }
            }
        }, ads);
        custom.startTurning(5000);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ADupdateEvent adupdateEvent) {
        List<AD> ads = AdHelper.queryAll();
        beans.clear();
        beans.add(new AD());
        beans.add(new AD());
        beans.add(new AD());
        for (AD ad : ads) {
            beans.add(ad);
        }
        setAdAdapter(beans);
    }


    /**
     * 注册各类事物
     */
    private void register() {
        mPresenter.initSO();
        mPresenter.registerNetOffReceiver();
        registerRxBus();
        registerIPHost();
        mPresenter.getData();
        mPresenter.getToken();
        mPresenter.startSocketService();
    }

    /**
     * 初始化布局显示
     */
    private void showView() {
        String declareContent = SPUtils.getString(Constant.KEY_FIRSTPAGE_CONTENT);
        if (!TextUtils.isEmpty(declareContent)) {
            gonggaoText.setText(String.format(getString(R.string.declare_content), declareContent, declareContent));
        } else {
            gonggaoText.setText(getResources().getString(R.string.declare));
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
        addSubscription(RxBus.getDefault().toObserverable(IpHostEvent.class).subscribe(ipHostEvent -> {
            mPresenter.initLocation();
            mPresenter.getData();
            mPresenter.getToken();
        }, throwable -> {
        }));
    }

    /**
     * 初始化摄像头显示
     */
    private void initSurfaceViewOne() {
        SurfaceHolder surfaceholder = surfaceview.getHolder();
        callbackFont = new SurfaceHolderCaremaFont(this);
        callbackFont.setCallBack(new SurfaceCallback());
        surfaceholder.addCallback(callbackFont);
    }

    private static class SurfaceCallback implements SurfaceHolderCaremaFont.CallBack {

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
    }


    @Override
    protected void onStart() {
        super.onStart();
        mStaticHandler.post(mRefreshTimeRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceName.setText(SPUtils.getString(Constant.DEVICE_NAME));
        new Handler().postDelayed(() -> {
            if (!KeyUtils.isHaveKey()) {
                setFirstLoder();
            }
        }, 500);
    }


    @Override
    protected void onStop() {
        LogUtil.e("onStop");
        mStaticHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        destory();
        super.onDestroy();
    }


    /**
     * 释放所有资源
     */
    private void destory() {
        LogUtil.d("wuliang", "home destory!!!");
        mPresenter.stopPer();
        mPresenter.unRegisterReceiver(this);
        SurfaceHolderCaremaFont.stopCarema();
        SurfaceHolderCaremaBack.stopCarema();
        JniHandler.getInstance().deInitJni();
        FaceUtils.getInstance().stopFaceFR();
        FaceUtils.getInstance().destory();
        callbackFont.setCallBack(null);
        callbackFont = null;
        stopService(new Intent(getApplicationContext(), AppHttpServerService.class));
        stopService(new Intent(getApplicationContext(), SocketService.class));
        stopService(new Intent(getApplicationContext(), VerifyService.class));
        mStaticHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().post(new DestoryEvent());
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
        showDialogFragment(getString(R.string.edit_person_pwd), content -> {
            if (StringUtils.isEmpty(content)) {
                dialogFragment.showError(getString(R.string.edit_person_pwd));
                return;
            }
            if (content.length() != 6) {
                dialogFragment.showError(getString(R.string.please_all_pwd));
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
                dialogFragment.showError(getString(R.string.password_error));
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
        dialogFragment.showHite(getString(R.string.edit_pwd_hint));
        dialogFragment.setMaxLength(6);
        if (isHideInput) {
            dialogFragment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);   //隐藏密码
        } else {
            dialogFragment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);   //显示密码
        }
        dialogFragment.setOnCancelClickListener(() -> dialogFragment.dismiss()).setOnConfirmClickListener(listener);
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
    public void hasPerson(boolean hasPerson) {
        if (hasPerson) {
            if (surfaceview != null && surfaceview.getVisibility() == View.GONE) {
                JniHandler handler = JniHandler.getInstance();
                Message message = Message.obtain();
                message.what = EventUtil.MEDIA_OPEN;
                handler.sendMessage(message);
                runOnUiThread(() -> surfaceview.setVisibility(View.VISIBLE));
            }
        } else {
            if (surfaceview != null && surfaceview.getVisibility() == View.VISIBLE) {
                JniHandler handler = JniHandler.getInstance();
                Message message = Message.obtain();
                message.what = EventUtil.MEDIA_CLOSE;
                handler.sendMessage(message);
                runOnUiThread(() -> surfaceview.setVisibility(View.GONE));
            }
        }
    }


    /**
     * 使用弱引用避免内存泄漏
     */
    private static class RefreshTimeRunnable implements Runnable {

        WeakReference<HomeActivity> weakReference;

        RefreshTimeRunnable(HomeActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            HomeActivity activity = weakReference.get();
            if (activity != null) {
                activity.deviceTime.setText(TimeUtils.getTime());
                activity.mStaticHandler.postDelayed(this, REFRESH_DELAY);
            }
        }
    }

    @Override
    public void onClick(View v) {
        menu.close();
        switch (v.getId()) {
            case R.id.menu_setting:
                setFirstLoder();
                break;
            case R.id.menu_manager:
//                if (!StringUtils.isEmpty(modilePhone)) {
//                    showMessageDialog("联系电话: " + modilePhone);
//                }
                break;
            case R.id.menu_pwd:    //密码开门
                gotoActivity(NumpasswordActivity.class, false);
                break;
            case R.id.menu_call:    //拨号
                gotoActivity(CallActivity.class, false);
                break;
            case R.id.menu_qrcode:    //二维码
//                gotoActivity(QrcodeActivity.class, false);
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 0x11);
                break;
            case R.id.menu_message:    //留言
                gotoActivity(BbsActivity.class, false);
                break;
        }
    }

    @Override
    public void onCallMessage(boolean isNoNet, int WifiorNetStatus, int scanLever) {
        if (!isNoNet) {
            isNetWork = false;
            return;
        }
        if (!isNetWork) {    //之前网络未连接，现在连接了
            mPresenter.getData();
            mPresenter.getToken();
            LogUtil.i("SocketService", "SocketService isConnect.");
            mPresenter.startSocketService();
        }
        isNetWork = true;
        if (WifiorNetStatus != 0) {
            mPresenter.startSocketService();
        }
    }
}
