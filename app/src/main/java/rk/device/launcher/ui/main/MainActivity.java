package rk.device.launcher.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.SetPageContentBO;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.bean.event.IpHostEvent;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastReceiver;
import rk.device.launcher.service.SocketService;
import rk.device.launcher.ui.fragment.InitErrorDialogFragmen;
import rk.device.launcher.utils.SoundPlayUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.widget.BatteryView;
import rk.device.launcher.widget.GifView;
import rk.device.launcher.widget.carema.DetectedFaceView;


/**
 * MVPPlugin
 * <p>
 * 主页面的界面处理
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements
        MainContract.View, JniHandler.OnInitListener, View.OnClickListener, ElectricBroadcastReceiver.CallBack,
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

    JniHandler mHander;
    private boolean isNetWork = true;// 此状态保存上次网络是否连接，默认已连接


    private InitErrorDialogFragmen initDialog;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        initDialog = InitErrorDialogFragmen.newInstance();
        SoundPlayUtils.init(this);
        settingTv.setOnClickListener(this);
        setOnClick(R.id.rl_contact_manager, R.id.init_error);
        startService(new Intent(this, SocketService.class));
    }


    @Override
    protected void initData() {
        mHander = mPresenter.initJni();
        mHander.setOnInitListener(this);
        mPresenter.registerBatteryReceiver().setCallBack(this);
        mPresenter.registerNetReceiver().setCallBack(this);
        mPresenter.registerNetOffReceiver();
        registerRxBus();
        registerIPHost();
        mPresenter.initLocation(this);
    }


    @Override
    public void onRequestError(String msg) {

    }

    @Override
    public void onRequestEnd() {

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
                initDialog.setStatus(cvcStatus, LedStatus, MdStatus, NfcStatus, mHander);
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
        battryNum.setText(leverPercent + "%");
        battryView.setProgress(leverPercent);
    }


    @Override
    public void onClick(View view) {

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
//            initLocation();
//            getData();
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
//            getData();
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
}
