package rk.device.launcher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.SetDoorRvBean;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.DrawableUtil;

public class SetDoorGuardActivity extends BaseActivity {

	@Bind(R.id.iv_back)
	ImageView mIvBack;
	@Bind(R.id.tv_title)
	TextView mTvTitle;
	@Bind(R.id.ll_connected_device)
	LinearLayout mLlConnectedDevice;
	@Bind(R.id.ll_turn_on_switch)
	LinearLayout mLlTurnOnSwitch;
	@Bind(R.id.ll_switch_time)
	LinearLayout mLlSwitchTime;
	@Bind(R.id.ll_door_input)
	LinearLayout mLlDoorInput;
	@Bind(R.id.ll_alert_overtime)
	LinearLayout mLlAlertOvertime;
	@Bind(R.id.ll_opendoor_input)
	LinearLayout mLlOpendoorInput;
	@Bind(R.id.ll_red_ray_sensor_input)
	LinearLayout mLlRedRaySensorInput;
	@Bind(R.id.tv_connected_device)
	TextView mTvConnectedDevice;
	@Bind(R.id.tv_lock_quantity)
	TextView mTvLockQuantity;
	@Bind(R.id.tv_lock_time)
	TextView mTvLockTime;
	@Bind(R.id.tv_alert_input)
	TextView mTvAlertInput;
	@Bind(R.id.tv_alert_overtime)
	TextView mTvAlertOvertime;
	@Bind(R.id.tv_open_door_input)
	TextView mTvOpenDoorInput;
	@Bind(R.id.tv_red_ray_input)
	TextView mTvRedRayInput;
	@Bind(R.id.btn_finish_setting)
	Button mBtnFinishSetting;
	// 这只是其中一个选项的选项数据, 还会有很多个
	private ArrayList<SetDoorRvBean> mConnectedDeviceDataList;
	private ArrayList<SetDoorRvBean> mLockQuantityDataList;
	private ArrayList<SetDoorRvBean> mLockTimeDataList;
	private ArrayList<SetDoorRvBean> mAlertInputDataList;
	private ArrayList<SetDoorRvBean> mAlertOverTimeDataList;
	private ArrayList<SetDoorRvBean> mOpenDoorInputDataList;
	private ArrayList<SetDoorRvBean> mRedRaySensorDataList;
	private SparseArrayCompat<Integer> mIntegerSparseArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_door_guard);
		ButterKnife.bind(this);
		hideNavigationBar();
		mIntegerSparseArray = new SparseArrayCompat<>();
		mIntegerSparseArray.put(0, R.id.ll_connected_device);
		mIntegerSparseArray.put(1, R.id.ll_turn_on_switch);
		mIntegerSparseArray.put(2, R.id.ll_switch_time);
		mIntegerSparseArray.put(3, R.id.ll_door_input);
		mIntegerSparseArray.put(4, R.id.ll_alert_overtime);
		mIntegerSparseArray.put(5, R.id.ll_opendoor_input);
		mIntegerSparseArray.put(6, R.id.ll_red_ray_sensor_input);

		mConnectedDeviceDataList = new ArrayList<>();
		mConnectedDeviceDataList.add(new SetDoorRvBean(true, "智能门锁"));
		mConnectedDeviceDataList.add(new SetDoorRvBean("智能门禁 (身份证)"));
		mConnectedDeviceDataList.add(new SetDoorRvBean("智能门禁"));
		mConnectedDeviceDataList.add(new SetDoorRvBean("电梯"));
		mTvConnectedDevice.setText(mConnectedDeviceDataList.get(0).text);

		mLockQuantityDataList = new ArrayList<>();
		mLockQuantityDataList.add(new SetDoorRvBean(true, "开关量1"));
		mLockQuantityDataList.add(new SetDoorRvBean("开关量2"));
		mLockQuantityDataList.add(new SetDoorRvBean("开关量3"));
		mTvLockQuantity.setText(mLockQuantityDataList.get(0).text);

		mLockTimeDataList = new ArrayList<>();
		for (int i = 0; i < 101; i++) {
			if (i == 0) {
				mLockTimeDataList.add(new SetDoorRvBean(true, i + "s"));
			} else {
				mLockTimeDataList.add(new SetDoorRvBean(i + "s"));
			}
		}
		mTvLockTime.setText(mLockTimeDataList.get(0).text);

		mAlertInputDataList = new ArrayList<>();
		mAlertInputDataList.add(new SetDoorRvBean(true, "DI1"));
		mAlertInputDataList.add(new SetDoorRvBean("DI2"));
		mAlertInputDataList.add(new SetDoorRvBean("DI3"));
		mTvAlertInput.setText(mAlertInputDataList.get(0).text);

		mAlertOverTimeDataList = new ArrayList<>();
		for (int i = 0; i < 101; i++) {
			if (i == 0) {
				mAlertOverTimeDataList.add(new SetDoorRvBean(true, i + "s"));
			} else {
				mAlertOverTimeDataList.add(new SetDoorRvBean(i + "s"));
			}
		}
		mTvAlertOvertime.setText(mAlertOverTimeDataList.get(0).text);

		mOpenDoorInputDataList = new ArrayList<>();
		mOpenDoorInputDataList.add(new SetDoorRvBean(true, "DI1"));
		mOpenDoorInputDataList.add(new SetDoorRvBean("DI2"));
		mOpenDoorInputDataList.add(new SetDoorRvBean("DI3"));
		mTvOpenDoorInput.setText(mOpenDoorInputDataList.get(0).text);

		mRedRaySensorDataList = new ArrayList<>();
		mRedRaySensorDataList.add(new SetDoorRvBean(true, "DI1"));
		mRedRaySensorDataList.add(new SetDoorRvBean("DI2"));
		mRedRaySensorDataList.add(new SetDoorRvBean("DI3"));
		mTvRedRayInput.setText(mRedRaySensorDataList.get(0).text);


		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTvTitle.setText("门禁设置");
		DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
		mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	@OnClick({R.id.ll_connected_device, R.id.ll_turn_on_switch, R.id.ll_switch_time, R.id.ll_door_input, R.id.ll_alert_overtime, R.id.ll_opendoor_input, R.id.ll_red_ray_sensor_input})
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
		int viewId = view.getId();
		switch (viewId) {
			case R.id.ll_connected_device:
				requestcode = 0;
				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mConnectedDeviceDataList);
				break;
			case R.id.ll_turn_on_switch:
				requestcode = 1;
				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mLockQuantityDataList);
				break;
			case R.id.ll_switch_time:
				requestcode = 2;
				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mLockTimeDataList);
				break;
			case R.id.ll_door_input:
				requestcode = 3;
				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mAlertInputDataList);
				break;
			case R.id.ll_alert_overtime:
				requestcode = 4;
				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mAlertOverTimeDataList);
				break;
			case R.id.ll_opendoor_input:
				requestcode = 5;
				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mOpenDoorInputDataList);
				break;
			case R.id.ll_red_ray_sensor_input:
				requestcode = 6;
				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mRedRaySensorDataList);
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
		int viewId = mIntegerSparseArray.get(requestCode);
		switch (viewId) {
			case R.id.ll_connected_device:
				if (checkIndex > -1) {
					for (SetDoorRvBean setDoorRvBean : mConnectedDeviceDataList) {
						setDoorRvBean.isChecked = false;
					}
					SetDoorRvBean checkedBean = mConnectedDeviceDataList.get(checkIndex);
					checkedBean.isChecked = true;
					mTvConnectedDevice.setText(checkedBean.text);
				}
				break;
			case R.id.ll_turn_on_switch:
				if (checkIndex > -1) {
					for (SetDoorRvBean setDoorRvBean : mLockQuantityDataList) {
						setDoorRvBean.isChecked = false;
					}
					SetDoorRvBean checkedBean = mLockQuantityDataList.get(checkIndex);
					checkedBean.isChecked = true;
					mTvLockQuantity.setText(checkedBean.text);
				}
				break;
			case R.id.ll_switch_time:
				if (checkIndex > -1) {
					for (SetDoorRvBean setDoorRvBean : mLockTimeDataList) {
						setDoorRvBean.isChecked = false;
					}
					SetDoorRvBean checkedBean = mLockTimeDataList.get(checkIndex);
					checkedBean.isChecked = true;
					mTvLockTime.setText(checkedBean.text);
				}

				break;
			case R.id.ll_door_input:
				if (checkIndex > -1) {
					for (SetDoorRvBean setDoorRvBean : mAlertInputDataList) {
						setDoorRvBean.isChecked = false;
					}
					SetDoorRvBean checkedBean = mAlertInputDataList.get(checkIndex);
					checkedBean.isChecked = true;
					mTvAlertInput.setText(checkedBean.text);
				}

				break;
			case R.id.ll_alert_overtime:
				if (checkIndex > -1) {
					for (SetDoorRvBean setDoorRvBean : mAlertOverTimeDataList) {
						setDoorRvBean.isChecked = false;
					}
					SetDoorRvBean checkedBean = mAlertOverTimeDataList.get(checkIndex);
					checkedBean.isChecked = true;
					mTvAlertOvertime.setText(checkedBean.text);
				}


				break;
			case R.id.ll_opendoor_input:
				if (checkIndex > -1) {
					for (SetDoorRvBean setDoorRvBean : mOpenDoorInputDataList) {
						setDoorRvBean.isChecked = false;
					}
					SetDoorRvBean checkedBean = mOpenDoorInputDataList.get(checkIndex);
					checkedBean.isChecked = true;
					mTvOpenDoorInput.setText(checkedBean.text);
				}

				break;
			case R.id.ll_red_ray_sensor_input:
				if (checkIndex > -1) {
					for (SetDoorRvBean setDoorRvBean : mRedRaySensorDataList) {
						setDoorRvBean.isChecked = false;
					}
					SetDoorRvBean checkedBean = mRedRaySensorDataList.get(checkIndex);
					checkedBean.isChecked = true;
					mTvRedRayInput.setText(checkedBean.text);
				}

				break;
		}

	}
}
