package rk.device.launcher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.ui.fragment.RecoveryDialogFragment;

/**
 * Created by mundane on 2017/11/9 上午10:56
 */

public class SettingActivity extends BaseActivity {


	@Bind(R.id.ll_set_time)
	LinearLayout mLlSetTime;
	@Bind(R.id.iv_set_time)
	ImageView mIvSetTime;
	@Bind(R.id.iv_set_network)
	ImageView mIvSetNetwork;
	@Bind(R.id.iv_set_door)
	ImageView mIvSetDoor;
	@Bind(R.id.iv_set_system)
	ImageView mIvSetSystem;
	@Bind(R.id.iv_sys_info)
	ImageView mIvSysInfo;
	@Bind(R.id.iv_recovery)
	ImageView mIvRecovery;
	@Bind(R.id.tv_set_time)
	TextView mTvSetTime;
	@Bind(R.id.tv_set_network)
	TextView mTvSetNetwork;
	@Bind(R.id.tv_set_door)
	TextView mTvSetDoor;
	@Bind(R.id.tv_set_sys)
	TextView mTvSetSys;
	@Bind(R.id.tv_sys_info)
	TextView mTvSysInfo;
	@Bind(R.id.tv_recovery)
	TextView mTvRecovery;
	@Bind(R.id.iv_back)
	ImageView mIvBack;
	@Bind(R.id.ll_set_net)
	LinearLayout mLlSetNet;
	@Bind(R.id.ll_set_door)
	LinearLayout mLlSetDoor;
	@Bind(R.id.ll_set_sys)
	LinearLayout mLlSetSys;
	@Bind(R.id.ll_sys_info)
	LinearLayout mLlSysInfo;
	@Bind(R.id.ll_recovery)
	LinearLayout mLlRecovery;
	@Bind(R.id.tv_title)
	TextView mTvTitle;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ButterKnife.bind(this);
		hideNavigationBar();


		mTvTitle.setText("设置");
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		processOnTouchListener(mLlSetTime, mIvSetTime, mTvSetTime, R.drawable.set_time_normal, R.drawable.set_time_pressed);
		processOnTouchListener(mLlSetNet, mIvSetNetwork, mTvSetNetwork, R.drawable.set_network_normal, R.drawable.set_network_pressed);
		processOnTouchListener(mLlSetDoor, mIvSetDoor, mTvSetDoor, R.drawable.set_door_normal, R.drawable.set_door_pressed);
		processOnTouchListener(mLlSetSys, mIvSetSystem, mTvSetSys, R.drawable.set_system_normal, R.drawable.set_system_pressed);
		processOnTouchListener(mLlSysInfo, mIvSysInfo, mTvSysInfo, R.drawable.system_info_normal, R.drawable.set_system_pressed);
		processOnTouchListener(mLlRecovery, mIvRecovery, mTvRecovery, R.drawable.recovery_normal, R.drawable.recovery_pressed);
		mLlSetTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, SetTimeActivity.class);
				startActivity(intent);
			}
		});
		mLlSetNet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, SetNetWorkActivity.class);
				startActivity(intent);
			}
		});
		mLlSetDoor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, SetDoorGuardActivity.class);
				startActivity(intent);
			}
		});
		mLlSetSys.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, SetSysActivity.class);
				startActivity(intent);
			}
		});
		mLlSysInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, SystemInfoActivity.class);
				startActivity(intent);
			}
		});
		mLlRecovery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final RecoveryDialogFragment recoveryDialogFragment = RecoveryDialogFragment.newInstance();
				recoveryDialogFragment.setOnCancelClickListener(new RecoveryDialogFragment.onCancelClickListener() {
					@Override
					public void onCancelClick() {
						recoveryDialogFragment.dismiss();
					}
				})
                .setOnConfirmClickListener(new RecoveryDialogFragment.OnConfirmClickListener() {
					@Override
					public void onConfirmClick() {
						Intent intent = new Intent(SettingActivity.this, RecoveryActivity.class);
						startActivity(intent);
					}
				});
				recoveryDialogFragment.show(getSupportFragmentManager(), "");
			}
		});
	}

	private void processOnTouchListener(final LinearLayout ll, final ImageView iv, final TextView tv, final int normalResource, final int pressedResource) {
		ll.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						tv.setTextColor(getResources().getColor(R.color.white));
//						ll.getBackground().clearColorFilter();
//						ll.getBackground().setColorFilter(getResources().getColor(R.color.half_transparent_black), PorterDuff.Mode.SRC_ATOP);
						ll.setBackgroundResource(R.color.half_transparent_black);
						iv.setImageResource(pressedResource);
//						return true;
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_OUTSIDE:
						tv.setTextColor(getResources().getColor(R.color.blue_338eff));
						iv.setImageResource(normalResource);
//						ll.getBackground().clearColorFilter();
//						ll.setBackgroundResource(R.drawable.item_background_normal);
						ll.setBackgroundResource(R.color.transparent);
						break;
					case MotionEvent.ACTION_CANCEL:
						tv.setTextColor(getResources().getColor(R.color.blue_338eff));
						iv.setBackgroundResource(normalResource);
//						ll.getBackground().clearColorFilter();
//						ll.setBackgroundResource(R.drawable.item_background_normal);
						ll.setBackgroundResource(R.color.transparent);
						break;
				}
				return false;
			}
		});
	}

	// Used to load the 'native-lib' library on application startup.
	static {
		System.loadLibrary("native-lib");
	}

	/**
	 * A native method that is implemented by the 'native-lib' native library,
	 * which is packaged with this application.
	 */
	public native String stringFromJNI();


}
