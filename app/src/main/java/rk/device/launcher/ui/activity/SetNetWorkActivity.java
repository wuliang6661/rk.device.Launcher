package rk.device.launcher.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.ui.fragment.AutoObtainNetworkConfigFragment;
import rk.device.launcher.ui.fragment.ManualConfigFragment;
import rk.device.launcher.ui.fragment.WifiListFragment;
import rk.device.launcher.utils.DrawableUtil;

public class SetNetWorkActivity extends BaseActivity implements View.OnClickListener {

	@Bind(R.id.iv_back)
	ImageView mIvBack;
	@Bind(R.id.tv_title)
	TextView mTvTitle;
	@Bind(R.id.ll_auto)
	LinearLayout mLlAuto;
	@Bind(R.id.ll_manul)
	LinearLayout mLlManul;
	@Bind(R.id.ll_wifi)
	LinearLayout mLlWifi;
	@Bind(R.id.iv_auto)
	ImageView mIvAuto;
	@Bind(R.id.iv_manul)
	ImageView mIvManul;
	@Bind(R.id.iv_wifi)
	ImageView mIvWifi;
	@Bind(R.id.btn_finish_setting)
	Button mBtnFinishSetting;
	private FragmentManager mFragmentManager;
	private String mFragmentTag = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_net_work);
		ButterKnife.bind(this);
		hideNavigationBar();
		mFragmentManager = getSupportFragmentManager();
		mTvTitle.setText("网络设置");
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mLlAuto.setOnClickListener(this);
		mLlManul.setOnClickListener(this);
		mLlWifi.setOnClickListener(this);

		DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
		mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentFragment instanceof ManualConfigFragment) {
					ManualConfigFragment fragment = (ManualConfigFragment) mCurrentFragment;
					fragment.saveIpConfig();
				}
				finish();
			}
		});


		mCurrentCheckedIv = mIvAuto;
		mCurrentCheckedLl = mLlAuto;
		turnToFragment("1");
	}

	private Fragment mCurrentFragment;

	private void turnToFragment(String fragmentTag) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.setCustomAnimations(R.anim.enter, R.anim.exit);
		Fragment toFragment = mFragmentManager.findFragmentByTag(fragmentTag);
		if (mCurrentFragment != null) {
			ft.hide(mCurrentFragment);
		}
		if (toFragment != null && !toFragment.isHidden()) {
			return;
		}
		if (toFragment == null) {
			toFragment = initateFragmentByTag(fragmentTag);
			ft.add(R.id.fl_container, toFragment, fragmentTag);
		} else if (toFragment.isHidden()) {
			ft.show(toFragment);
		}
		ft.commit();
		mCurrentFragment = toFragment;


	}

	private Fragment initateFragmentByTag(String fragmentTag) {
		switch (fragmentTag) {
			case "1":
				return AutoObtainNetworkConfigFragment.newInstance();
			case "2":
				return ManualConfigFragment.newInstance();
			case "3":
				return WifiListFragment.newInstance();
		}
		return null;
	}

	private ImageView mCurrentCheckedIv;
	private LinearLayout mCurrentCheckedLl;

	@Override
	public void onClick(View v) {
		if (mCurrentCheckedIv != null) {
			mCurrentCheckedIv.setImageResource(R.drawable.circle_uncheck);
		}
		if (mCurrentCheckedLl != null) {
			mCurrentCheckedLl.setBackgroundResource(R.color.transparent);
		}
		switch (v.getId()) {
			case R.id.ll_auto:
				mIvAuto.setImageResource(R.drawable.circle_checked);
				mLlAuto.setBackgroundResource(R.color.half_transparent_white);
				mCurrentCheckedLl = mLlAuto;
				mCurrentCheckedIv = mIvAuto;
				turnToFragment("1");
				break;
			case R.id.ll_manul:
				mIvManul.setImageResource(R.drawable.circle_checked);
				mLlManul.setBackgroundResource(R.color.half_transparent_white);
				mCurrentCheckedLl = mLlManul;
				mCurrentCheckedIv = mIvManul;
				turnToFragment("2");
				break;
			case R.id.ll_wifi:
				mIvWifi.setImageResource(R.drawable.circle_checked);
				mLlWifi.setBackgroundResource(R.color.half_transparent_white);
				mCurrentCheckedLl = mLlWifi;
				mCurrentCheckedIv = mIvWifi;
				turnToFragment("3");
				break;
		}

	}
}
