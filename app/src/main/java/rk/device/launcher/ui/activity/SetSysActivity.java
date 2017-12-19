package rk.device.launcher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import peripherals.LedHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.SetDoorRvBean;
import rk.device.launcher.event.IpHostEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.DrawableUtil;
import rk.device.launcher.utils.EditUtil;
import rk.device.launcher.utils.NetWorkUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;

/**
 * 系统设置
 */
public class SetSysActivity extends BaseCompatActivity {

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

    private ArrayList<SetDoorRvBean> mSleepTimeDataList;
//	private ArrayList<SetDoorRvBean> mLightValueDataList;

    private Thread thread;
    private String mip, mport, mclientCode;

    @Override
    protected int getLayout() {
        return R.layout.activity_set_sys;
    }

    @Override
    protected void initView() {
        goBack();
        setTitle("系统设置");
    }

    @Override
    protected void initData() {
        mSleepTimeDataList = new ArrayList<>();
        mSleepTimeDataList.add(new SetDoorRvBean("30秒", 30000));
        mSleepTimeDataList.add(new SetDoorRvBean("1分钟", 60000));
        mSleepTimeDataList.add(new SetDoorRvBean("2分钟", 120000));
        mSleepTimeDataList.add(new SetDoorRvBean("5分钟", 300000));
        mSleepTimeDataList.add(new SetDoorRvBean("10分钟", 600000));
        mSleepTimeDataList.add(new SetDoorRvBean("不休眠", -1));

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
                    showMessageDialog("请填写端口号！");
                    return;
                }
                mclientCode = mEtClientCode.getText().toString();
                if (!TextUtils.isEmpty(mclientCode)) {
                    if (mclientCode.length() < 6) {
                        showMessageDialog("客户号输入错误！！");
                        return;
                    }
                }
                if (!StringUtils.isEmpty(mip) || !StringUtils.isEmpty(mport)) {
                    if (NetWorkUtil.isNetConnected(SetSysActivity.this)) {
                        showWaitProgress("正在连接服务器...");
                        thread = new Thread(runnable);
                        thread.start();
                    } else {
                        showMessageDialog("更换IP请先连接网络！");
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
        ApiService.clearIP();
        RxBus.getDefault().post(new IpHostEvent(true));
        // 保存客户号
        SPUtils.putString(Constant.KEY_CLIENT_CODE, clientCode);
        // 保存待机时间
        SPUtils.putLong(Constant.KEY_SLEEP_TIME, getSleepTime());
        // 保存补光灯的开关状态
        SPUtils.putBoolean(Constant.KEY_LIGNT, mCbLight.isChecked());
        if (mCbLight.isChecked()) {
            LedHelper.PER_ledToggle(1);
        } else {
            LedHelper.PER_ledToggle(0);
        }
        boolean isFirstSetting = SPUtils.getBoolean(Constant.IS_FIRST_SETTING, true);    //是否第一次进入设置
        if (isFirstSetting) {
            SPUtils.putInt(Constant.SETTING_NUM, -1000);
            SPUtils.putBoolean(Constant.IS_FIRST_SETTING, false);
            Toast.makeText(SetSysActivity.this, "完成设置", Toast.LENGTH_LONG).show();
            AppManager.getAppManager().goBackMain();
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
                    showMessageDialog("服务器IP地址不可用！");
                    break;
                case 0x22:
                    hintWaitProgress();
                    break;
            }
        }
    };


    private long getSleepTime() {
        for (SetDoorRvBean setDoorRvBean : mSleepTimeDataList) {
            if (setDoorRvBean.isChecked) {
                return setDoorRvBean.sleepTime;
            }
        }
        return 30000;
    }

    private void setSleepTimeText(long sleepTime) {
        for (SetDoorRvBean setDoorRvBean : mSleepTimeDataList) {
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
                    for (SetDoorRvBean setDoorRvBean : mSleepTimeDataList) {
                        setDoorRvBean.isChecked = false;
                    }
                    SetDoorRvBean checkedBean = mSleepTimeDataList.get(checkIndex);
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
