package rk.device.launcher.ui.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.R;

/**
 * Created by mundane on 2017/11/9 上午10:56
 */

public class SettingActivity extends AppCompatActivity {


	@BindView(R.id.ll_set_time)
	LinearLayout mLlSetTime;
	@BindView(R.id.iv_set_time)
	ImageView mIvSetTime;
	@BindView(R.id.iv_set_network)
	ImageView mIvSetNetwork;
	@BindView(R.id.iv_set_door)
	ImageView mIvSetDoor;
	@BindView(R.id.iv_set_system)
	ImageView mIvSetSystem;
	@BindView(R.id.iv_sys_info)
	ImageView mIvSysInfo;
	@BindView(R.id.iv_recovery)
	ImageView mIvRecovery;
	@BindView(R.id.tv_set_time)
	TextView mTvSetTime;
	@BindView(R.id.tv_set_network)
	TextView mTvSetNetwork;
	@BindView(R.id.tv_set_door)
	TextView mTvSetDoor;
	@BindView(R.id.tv_set_sys)
	TextView mTvSetSys;
	@BindView(R.id.tv_sys_info)
	TextView mTvSysInfo;
	@BindView(R.id.tv_recovery)
	TextView mTvRecovery;
	@BindView(R.id.iv_back)
	ImageView mIvBack;
	@BindView(R.id.ll_set_net)
	LinearLayout mLlSetNet;
	@BindView(R.id.ll_set_door)
	LinearLayout mLlSetDoor;
	@BindView(R.id.ll_set_sys)
	LinearLayout mLlSetSys;
	@BindView(R.id.ll_sys_info)
	LinearLayout mLlSysInfo;
	@BindView(R.id.ll_recovery)
	LinearLayout mLlRecovery;
	@BindView(R.id.tv_title)
	TextView mTvTitle;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ButterKnife.bind(this);


		mTvTitle.setText("设置");
		processOnTouchListener(mLlSetTime, mIvSetTime, mTvSetTime, R.drawable.set_time_normal, R.drawable.set_time_pressed);
		processOnTouchListener(mLlSetNet, mIvSetNetwork, mTvSetNetwork, R.drawable.set_network_normal, R.drawable.set_network_pressed);
		processOnTouchListener(mLlSetDoor, mIvSetDoor, mTvSetDoor, R.drawable.set_door_normal, R.drawable.set_door_pressed);
		processOnTouchListener(mLlSetSys, mIvSetSystem, mTvSetSys, R.drawable.set_system_normal, R.drawable.set_system_pressed);
		processOnTouchListener(mLlSysInfo, mIvSysInfo, mTvSysInfo, R.drawable.system_info_normal, R.drawable.set_system_pressed);
		processOnTouchListener(mLlRecovery, mIvRecovery, mTvRecovery, R.drawable.recovery_normal, R.drawable.recovery_pressed);
		mLlSetTime.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
//				Toast.makeText(SettingActivity.this, "时间设置", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingActivity.this, SetTimeActivity.class);
				startActivity(intent);
			}
		});
		mLlSetNet.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
//				Toast.makeText(SettingActivity.this, "网络设置", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingActivity.this, SetNetWorkActivity.class);
				startActivity(intent);
			}
		});
		mLlSetDoor.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Toast.makeText(SettingActivity.this, "门禁设置", Toast.LENGTH_SHORT).show();
			}
		});
		mLlSetSys.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Toast.makeText(SettingActivity.this, "参数设置", Toast.LENGTH_SHORT).show();
			}
		});
		mLlSysInfo.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Toast.makeText(SettingActivity.this, "系统信息", Toast.LENGTH_SHORT).show();
			}
		});
		mLlRecovery.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Toast.makeText(SettingActivity.this, "恢复出厂", Toast.LENGTH_SHORT).show();
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
						ll.getBackground().clearColorFilter();
						ll.getBackground().setColorFilter(getResources().getColor(R.color.half_transparent_black), PorterDuff.Mode.SRC_ATOP);
						iv.setImageResource(pressedResource);
//						return true;
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_OUTSIDE:
						tv.setTextColor(getColor(R.color.blue_338eff));
						iv.setImageResource(normalResource);
						ll.getBackground().clearColorFilter();
						ll.setBackgroundResource(R.drawable.item_background_normal);
						break;
					case MotionEvent.ACTION_CANCEL:
						tv.setTextColor(getColor(R.color.blue_338eff));
						iv.setBackgroundResource(normalResource);
						ll.getBackground().clearColorFilter();
						ll.setBackgroundResource(R.drawable.item_background_normal);
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
