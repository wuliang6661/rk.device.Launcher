package rk.device.launcher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.bean.SetDoorRvBean;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.DrawableUtil;
import rk.device.launcher.utils.SPUtils;
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
			mEtClientCode.setHint(clientCode);
		}

		// 读取保存的补光灯的开关状态
		boolean isLightTurnOn = SPUtils.getBoolean(Constant.KEY_LIGNT, false);
		mCbLight.setChecked(isLightTurnOn);

		DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
		// 设置或者获取uuid
		DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(this);
		mTvDeviceId.setText(deviceUuidFactory.getUuid().toString());

		// 读取保存的IP
		String ip = SPUtils.getString(Constant.KEY_IP);
		if (!TextUtils.isEmpty(ip)) {
			mEtIP.setHint(ip);
		}

		// 读取保存的端口号
		String port = SPUtils.getString(Constant.KEY_PORT);
		if (!TextUtils.isEmpty(port)) {
			mEtPort.setHint(port);
		}

		mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 保存待机时间
				SPUtils.putLong(Constant.KEY_SLEEP_TIME, getSleepTime());

				// 保存客户号
				String clientCode = mEtClientCode.getText().toString();
				if (!TextUtils.isEmpty(clientCode)) {
					SPUtils.putString(Constant.KEY_CLIENT_CODE, clientCode);
				}

				// 保存补光灯的开关状态
				SPUtils.putBoolean(Constant.KEY_LIGNT, mCbLight.isChecked());

				// 保存IP
				String ip = mEtIP.getText().toString();
				if (!TextUtils.isEmpty(ip)) {
					SPUtils.putString(Constant.KEY_IP, ip);
				}

				// 保存端口号
				String port = mEtPort.getText().toString();
				if (!TextUtils.isEmpty(port)) {
					SPUtils.putString(Constant.KEY_PORT, port);
				}

				boolean isFirstSetting = SPUtils.getBoolean(Constant.IS_FIRST_SETTING, true);    //是否第一次进入设置
				if (isFirstSetting) {
					SPUtils.putInt(Constant.SETTING_NUM, -1000);
					Toast.makeText(SetSysActivity.this, "完成设置", Toast.LENGTH_LONG).show();
					AppManager.getAppManager().goBackMain();
				} else {
					finish();
				}

			}
		});
	}

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
//			case 1:
//				if (checkIndex > -1) {
//					for (SetDoorRvBean setDoorRvBean : mLightValueDataList) {
//						setDoorRvBean.isChecked = false;
//					}
//					SetDoorRvBean checkedBean = mLightValueDataList.get(checkIndex);
//					checkedBean.isChecked = true;
//					mEtLightValue.setText(checkedBean.text);
//				}
//
//				break;
        }
    }

}
