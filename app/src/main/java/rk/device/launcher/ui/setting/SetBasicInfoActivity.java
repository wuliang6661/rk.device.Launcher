package rk.device.launcher.ui.setting;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.event.BlueToothEvent;
import rk.device.launcher.bean.event.HomeInfoEvent;
import rk.device.launcher.bean.event.TimeEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.detection.CaremaDetection;
import rk.device.launcher.ui.detection.HardwareAct;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.bluetools.MoreManager;
import rk.device.launcher.utils.rxjava.RxBus;
import rx.Subscriber;

import static rk.device.launcher.utils.SPUtils.get;

/**
 * 基础设置 Created by hanbin on 2017/11/24.
 */

public class SetBasicInfoActivity extends BaseActivity implements View.OnClickListener {

    private long when_time = 0;

    @Bind(R.id.eyes_verify)
    LinearLayout eyesVerify;
    @Bind(R.id.tv_time)
    TextView timeTv;
    @Bind(R.id.tv_blue_tooth_name)
    TextView blueToothNameTv;
    @Bind(R.id.et_device_name)
    EditText deviceNameEt;
    @Bind(R.id.checkbox_voice)
    CheckBox voiceCheckBox;
    private boolean isVoice = false;                           //是否语音提示
    private BlueToothEvent blueEvent = null;
    private BluetoothClient mClient = MoreManager.getBluetoothClient();

    @Override
    protected int getLayout() {
        return R.layout.activity_basic_info;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        registerRxBus();
        setOnClick(R.id.ll_set_time, R.id.ll_set_blue_tooth, R.id.btn_finish_setting, R.id.eyes_verify, R.id.ceshi);
        goBack();
    }

    /**
     * 注册数据回调监听
     */
    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(TimeEvent.class)
                .subscribe(new Subscriber<TimeEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TimeEvent timeEvent) {
                        try {
                            int realMonth = timeEvent.month + 1;
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.YEAR, timeEvent.year);
                            c.set(Calendar.MONTH, timeEvent.month);
                            c.set(Calendar.DAY_OF_MONTH, timeEvent.day);
                            c.set(Calendar.HOUR_OF_DAY, timeEvent.hour);
                            c.set(Calendar.MINUTE, timeEvent.minute);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);
                            when_time = c.getTimeInMillis();
                            timeTv.setText(
                                    timeEvent.year + "-" + realMonth + "-" + timeEvent.day
                                            + " " + timeEvent.hour + ":" + timeEvent.minute);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
        addSubscription(RxBus.getDefault().toObserverable(BlueToothEvent.class)
                .subscribe(new Subscriber<BlueToothEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BlueToothEvent blueToothEvent) {
                        blueEvent = blueToothEvent;
                        blueToothNameTv.setText(blueToothEvent.name);
                    }
                }));
    }

    protected void initData() {
        setTitle(getString(R.string.basic_settting));
        isVoice = (boolean) get(Constant.DEVICE_MP3, true);
        deviceNameEt.setText((String) get(Constant.DEVICE_NAME, ""));
        deviceNameEt.setSelection(deviceNameEt.getText().length());
        String mac = (String) get(Constant.BLUE_TOOTH, "");
        String name = (String) get(Constant.BLUE_NAME, "");
        blueToothNameTv.setText((String) get(Constant.BLUE_NAME, ""));
        timeTv.setText(TimeUtils.getTime());
        when_time = new Date().getTime();
        if (!StringUtils.isEmpty(mac)) {
            blueEvent = new BlueToothEvent(mac, name);
        }
        voiceCheckBox.setChecked(isVoice);
        voiceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> isVoice = isChecked);
    }


    /**
     * 设置监听
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_set_time:
                gotoActivity(SetTimeActivity.class, false);
                break;
            case R.id.ll_set_blue_tooth:
                gotoActivity(BlueToothActivity.class, false);
                break;
            case R.id.eyes_verify:    //双目校验
                gotoActivity(EyesCorrectActivity.class, false);
                break;
            case R.id.btn_finish_setting:
                String deviceName = deviceNameEt.getText().toString();
                if (TextUtils.isEmpty(deviceName)) {
                    T.showShort(getString(R.string.name_setting_illeagel));
                    return;
                }
                if (blueEvent == null) {
                    saveDataFinish(deviceName);
                    return;
                }
                connectDevice(deviceName);
                break;
            case R.id.ceshi:
                gotoActivity(HardwareAct.class, false);
                break;
        }
    }

    /**
     * 连接蓝牙
     */
    private boolean connectDevice(String deviceName) {
        showMessageDialog("正在连接蓝牙锁...");
        mClient.connect(blueEvent.mac, (code, data) -> {
            MoreManager.setBlueToothEvent(blueEvent);
            MoreManager.setProfile(data);
            dissmissMessageDialog();
            MoreManager.blueReadClient();   //开启蓝牙数据监听
            if (code == Constants.REQUEST_SUCCESS) {
                //蓝牙
                SPUtils.put(Constant.BLUE_TOOTH, blueEvent.mac);
                SPUtils.put(Constant.BLUE_NAME, blueEvent.name);
                MoreManager.openLock((int) (when_time / 1000));
                saveDataFinish(deviceName);
                RxBus.getDefault().post(new HomeInfoEvent(deviceName));
            } else {
                T.showShort(getString(R.string.blue_tooth_error));
            }
        });
        return false;
    }


    /**
     * 调取蓝牙同步系统时间
     */
    private void syncBlueTime() {
        int time = (int) (when_time / 1000);
        MoreManager.syncBlueTime(time);
    }


    /**
     * 保存必需参数并退出页面
     */
    private void saveDataFinish(String deviceName) {
        //下面把相关的参数保存起来
        //设备名称
        SPUtils.put(Constant.DEVICE_NAME, deviceName);
        //语音设置
        SPUtils.put(Constant.DEVICE_MP3, isVoice);
        // 设置系统时间
        if (when_time / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when_time);
        }
        boolean isFirstSetting = SPUtils.getBoolean(Constant.IS_FIRST_SETTING, true);    //是否第一次进入设置
        if (isFirstSetting) {
            SPUtils.putInt(Constant.SETTING_NUM, -1000);
            SPUtils.putBoolean(Constant.IS_FIRST_SETTING, false);
            Toast.makeText(SetBasicInfoActivity.this, "完成设置", Toast.LENGTH_LONG).show();
            AppManager.getAppManager().goBackMain();
        } else {
            finish();
        }
    }
}