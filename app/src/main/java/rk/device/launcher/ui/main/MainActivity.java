package rk.device.launcher.ui.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.fragment.InitErrorDialogFragmen;
import rk.device.launcher.widget.BatteryView;
import rk.device.launcher.widget.GifView;
import rk.device.launcher.widget.carema.DetectedFaceView;


/**
 * MVPPlugin
 * <p>
 * 主页面的界面处理
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements
        MainContract.View, JniHandler.OnInitListener {


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
    @Bind(R.id.carema_bg)
    GifView caremaBg;
    @Bind(R.id.device_name_bg)
    GifView deviceNameBg;
    @Bind(R.id.suress_text)
    TextView suressText;
    @Bind(R.id.suress_layout)
    LinearLayout suressLayout;

    JniHandler mHander;

    private InitErrorDialogFragmen initDialog;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHander = mPresenter.initJni();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }
}
