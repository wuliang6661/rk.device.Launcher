package rk.device.launcher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SetSysActivity extends BaseActivity {

	@Bind(R.id.iv_back)
	ImageView mIvBack;
	@Bind(R.id.tv_title)
	TextView mTvTitle;
	@Bind(R.id.ll_sleep_time)
	LinearLayout mLlSleepTime;
	@Bind(R.id.ll_light_value)
	LinearLayout mLlLightValue;
	@Bind(R.id.tv_sleep_time)
	TextView mTvSleepTime;
	@Bind(R.id.tv_light_value)
	EditText mEtLightValue;
	@Bind(R.id.btn_finish_setting)
	Button mBtnFinishSetting;

	private ArrayList<SetDoorRvBean> mSleepTimeDataList;
//	private ArrayList<SetDoorRvBean> mLightValueDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_sys);
		mSleepTimeDataList = new ArrayList<>();
		mSleepTimeDataList.add(new SetDoorRvBean(true, "30秒"));
		mSleepTimeDataList.add(new SetDoorRvBean("1分钟"));
		mSleepTimeDataList.add(new SetDoorRvBean("2分钟"));
		mSleepTimeDataList.add(new SetDoorRvBean("5分钟"));
		mSleepTimeDataList.add(new SetDoorRvBean("10分钟"));
		mSleepTimeDataList.add(new SetDoorRvBean("不休眠"));
//		for (int i = 0; i < 101; i++) {
//			if (i == 0) {
//				mSleepTimeDataList.add(new SetDoorRvBean(true, i + ""));
//			} else {
//				mSleepTimeDataList.add(new SetDoorRvBean(i + ""));
//			}
//		}

//		mLightValueDataList = new ArrayList<>();
//		for (int i = 0; i < 101; i++) {
//			if (i == 0) {
//				mLightValueDataList.add(new SetDoorRvBean(true, i + ""));
//			} else {
//				mLightValueDataList.add(new SetDoorRvBean(i + ""));
//			}
//		}

		ButterKnife.bind(this);
		hideNavigationBar();
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTvTitle.setText("系统设置");
		mTvSleepTime.setText(mSleepTimeDataList.get(0).text);
//		mEtLightValue.setText(mLightValueDataList.get(0).text);
		DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
		mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
//			case R.id.ll_light_value:
//				requestcode = 1;
//				bundle.putParcelableArrayList(Constant.KEY_BUNDLE, mLightValueDataList);
//				break;
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
