package rk.device.launcher.ui.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;

import java.io.IOException;
import java.util.ArrayList;

import peripherals.LedHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.SetDoorRvBO;
import rk.device.launcher.bean.event.IpHostEvent;
import rk.device.launcher.crash.CrashUtils;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.DrawableUtil;
import rk.device.launcher.utils.EditUtil;
import rk.device.launcher.utils.NetWorkUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;

/**
 * 系统设置
 */
public class SetSysActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.ll_sleep_time)
    LinearLayout mLlSleepTime;
    @Bind(R.id.tv_sleep_time)
    TextView mTvSleepTime;
    @Bind(R.id.btn_finish_setting)
    Button mBtnFinishSetting;
    @Bind(R.id.tv_device_id)
    TextView mTvDeviceId;
    @Bind(R.id.et_client_code)
    EditText mEtClientCode;
    @Bind(R.id.cb_light)
    CheckBox mCbLight;
    @Bind(R.id.et_ip)
    EditText mEtIP;
    @Bind(R.id.et_port)
    EditText mEtPort;

    private ArrayList<SetDoorRvBO> mSleepTimeDataList;
//	private ArrayList<SetDoorRvBO> mLightValueDataList;

    private Thread thread;
    private String mip, mport, mclientCode;

    @Override
    protected int getLayout() {
        return R.layout.activity_set_sys;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        goBack();
        setTitle(getString(R.string.sys_setting));
    }

    protected void initData() {
        mSleepTimeDataList = new ArrayList<>();
        mSleepTimeDataList.add(new SetDoorRvBO(getString(R.string.tirty_second), 30000));
        mSleepTimeDataList.add(new SetDoorRvBO(getString(R.string.one_minutes), 60000));
        mSleepTimeDataList.add(new SetDoorRvBO(getString(R.string.two_minutes), 120000));
        mSleepTimeDataList.add(new SetDoorRvBO(getString(R.string.five_minutes), 300000));
        mSleepTimeDataList.add(new SetDoorRvBO(getString(R.string.ten_minutes), 600000));
        mSleepTimeDataList.add(new SetDoorRvBO(getString(R.string.dont_sleep), -1));

        // 读取保存的待机时间
        // 没有设置待机时间的话, 默认是30秒
        long sleepTime = SPUtils.getLong(Constant.KEY_SLEEP_TIME, 30000);
        setSleepTimeText(sleepTime);
        // 读取保存的客户号
        String clientCode = SPUtils.getString(Constant.KEY_CLIENT_CODE);
        if (!TextUtils.isEmpty(clientCode)) {
            mEtClientCode.setText(clientCode);
        }
        mEtClientCode.setSelection(mEtClientCode.getText().length());

        // 读取保存的补光灯的开关状态
        boolean isLightTurnOn = SPUtils.getBoolean(Constant.KEY_LIGNT, false);
        mCbLight.setChecked(isLightTurnOn);

        DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
        // 设置或者获取uuid
        DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(this);
        mTvDeviceId.setText(deviceUuidFactory.getUuid().toString());

        EditUtil.limitInput(mEtIP);
        // 读取保存的IP
        String ip = SPUtils.getString(Constant.KEY_IP);
        if (!TextUtils.isEmpty(ip)) {
            mEtIP.setText(ip);
        }
        mEtIP.setSelection(mEtIP.getText().length());

        // 读取保存的端口号
        String port = SPUtils.getString(Constant.KEY_PORT);
        if (!TextUtils.isEmpty(port)) {
            mEtPort.setText(port);
        }
        mEtPort.setSelection(mEtPort.getText().length());
        mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存IP
                mip = mEtIP.getText().toString().trim();
                // 保存端口号
                mport = mEtPort.getText().toString().trim();
                if (!StringUtils.isEmpty(mip) && StringUtils.isEmpty(mport)) {
                    showMessageDialog(getString(R.string.edit_post));
                    return;
                }
                mclientCode = mEtClientCode.getText().toString();
                if (!TextUtils.isEmpty(mclientCode)) {
                    if (mclientCode.length() < 6) {
                        showMessageDialog(getString(R.string.cid_error));
                        return;
                    }
                }
                if (!StringUtils.isEmpty(mip) || !StringUtils.isEmpty(mport)) {
                    if (NetWorkUtil.isNetConnected(SetSysActivity.this)) {
                        showWaitProgress(getString(R.string.connect_server_loading));
                        thread = new Thread(runnable);
                        thread.start();
                    } else {
                        showMessageDialog(getString(R.string.ip_select_net));
                    }
                    return;
                }
                saveSession(mip, mport, mclientCode);
            }
        });
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!pingIpAddress(mip)) {
                handler.sendEmptyMessage(0x11);
            } else {
                handler.sendEmptyMessage(0x22);
                saveSession(mip, mport, mclientCode);
            }
        }
    };


    /**
     * 处理保存并退出页面的操作
     */
    private void saveSession(String ip, String port, String clientCode) {
        SPUtils.putString(Constant.KEY_IP, ip);
        SPUtils.putString(Constant.KEY_PORT, port);
        BaseApiImpl.clearIP();
        RxBus.getDefault().post(new IpHostEvent(true));
        // 保存客户号
        SPUtils.putString(Constant.KEY_CLIENT_CODE, clientCode);
        // 保存待机时间
        SPUtils.putLong(Constant.KEY_SLEEP_TIME, getSleepTime());
//        SleepTaskServer.getSleepHandler(this).sendEmptyMessage(0x44);
        // 保存补光灯的开关状态
        SPUtils.putBoolean(Constant.KEY_LIGNT, mCbLight.isChecked());
        if (mCbLight.isChecked()) {
            int status = LedHelper.PER_ledToggle(1);
            new CrashUtils().LedCrash(status, 1);
        } else {
            int status = LedHelper.PER_ledToggle(0);
            new CrashUtils().LedCrash(status, 0);
        }
        //判断是否是第一次
        boolean isFirst = (boolean) SPUtils.get(Constant.IS_FIRST_SETTING, true);
//        syncBlueTime();
        if (isFirst) {
            SPUtils.put(Constant.SETTING_NUM, Constant.SETTING_TYPE6);
            gotoActivity(SetBasicInfoActivity.class, true);
        } else {
            finish();
        }
    }


    /**
     * 处理界面变化
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    hintWaitProgress();
                    showMessageDialog(getString(R.string.ip_no_error));
                    break;
                case 0x22:
                    hintWaitProgress();
                    break;
            }
        }
    };


    private long getSleepTime() {
        for (SetDoorRvBO setDoorRvBean : mSleepTimeDataList) {
            if (setDoorRvBean.isChecked) {
                return setDoorRvBean.sleepTime;
            }
        }
        return 30000;
    }

    private void setSleepTimeText(long sleepTime) {
        for (SetDoorRvBO setDoorRvBean : mSleepTimeDataList) {
            if (setDoorRvBean.sleepTime == sleepTime) {
                setDoorRvBean.isChecked = true;
                mTvSleepTime.setText(setDoorRvBean.text);
                return;
            }
        }

    }

    @OnClick({R.id.ll_sleep_time})
    public void onViewClicked(View view) {
        Intent intent = new Intent(this, SelectItemListActivity.class);
        Bundle bundle = new Bundle();

        String title = null;
        if (view instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) view;
            View firstChild = ll.getChildAt(0);
            if (firstChild instanceof TextView) {
                title = ((TextView) firstChild).getText().toString();
            }
        }
        bundle.putString(Constant.KEY_TITLE, title);
        int requestcode = -1;
        switch (view.getId()) {
            case R.id.ll_sleep_time:
                requestcode = 0;
                bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mSleepTimeDataList);
                break;
            default:
                break;
        }
        intent.putExtra(Constant.KEY_INTENT, bundle);
        startActivityForResult(intent, requestcode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        int checkIndex = data.getIntExtra(SelectItemListActivity.KEY_CHECKED_INDEX, -1);
        switch (requestCode) {
            case 0:
                if (checkIndex > -1) {
                    for (SetDoorRvBO setDoorRvBean : mSleepTimeDataList) {
                        setDoorRvBean.isChecked = false;
                    }
                    SetDoorRvBO checkedBean = mSleepTimeDataList.get(checkIndex);
                    checkedBean.isChecked = true;
                    mTvSleepTime.setText(checkedBean.text);
                }
                break;
        }
    }

    /**
     * ping一个地址是否可用
     */
    private boolean pingIpAddress(String ipAddress) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 10 " + ipAddress);
            int status = process.waitFor();
            if (status == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


}
