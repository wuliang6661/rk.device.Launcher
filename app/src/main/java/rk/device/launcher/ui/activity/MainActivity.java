package rk.device.launcher.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.utils.CommonUtils;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.ThreadUtils;
import rk.device.launcher.widget.UpdateManager;
import rx.Subscriber;

public class MainActivity extends BaseActivity {

    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_week)
    TextView mTvWeek;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.iv_signal)
    ImageView mIvSignal;
    @Bind(R.id.iv_setting)
    ImageView mIvSetting;
    @Bind(R.id.iv_arrow)
    ImageView mIvArrow;
    private Calendar mCalendar;
    private LocationManager mLocationManager;
    private final String TAG = "MainActivity";
    private static final int REFRESH_DELAY = 1000;

    private StaticHandler mStaticHandler = new StaticHandler();
    // todo 内存泄漏这里需要处理
    private final Runnable mRefreshTimeRunnable = new Runnable() {

        @Override
        public void run() {
            mTvTime.setText(getTime());
            mTvDate.setText(getDate());
            mTvWeek.setText(DateUtil.getWeek(mCalendar));
            mStaticHandler.postDelayed(this, REFRESH_DELAY);
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideNavigationBar();
        mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        initView();
        getLocation();
        registerBatteryReceiver();
        UpdateManager.getUpdateManager().checkAppUpdate(this, getSupportFragmentManager(), false);
    }

    private void initView() {
        if (mIvSetting != null) {
            mIvSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String password = SPUtils.getString(Constant.KEY_PASSWORD);
                    final InputWifiPasswordDialogFragment dialogFragment = InputWifiPasswordDialogFragment.newInstance();
                    if (TextUtils.isEmpty(password)) {
                        dialogFragment.setTitle("设置管理员密码");
                        dialogFragment.setOnCancelClickListener(new InputWifiPasswordDialogFragment.onCancelClickListener() {
                            @Override
                            public void onCancelClick() {
                                dialogFragment.dismiss();
                            }
                        })
                                .setOnConfirmClickListener(new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
                                    @Override
                                    public void onConfirmClick(String content) {
                                        dialogFragment.dismiss();
                                        if (!TextUtils.isEmpty(content)) {
                                            SPUtils.putString(Constant.KEY_PASSWORD, content);
                                            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    } else {
                        dialogFragment.setTitle("请输入管理员密码");
                        dialogFragment.setOnCancelClickListener(new InputWifiPasswordDialogFragment.onCancelClickListener() {
                            @Override
                            public void onCancelClick() {
                                dialogFragment.dismiss();
                            }
                        })
                                .setOnConfirmClickListener(new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
                                    @Override
                                    public void onConfirmClick(String content) {
                                        if (TextUtils.equals(password, content)) {
                                            dialogFragment.dismiss();
                                            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                            startActivity(intent);
                                        } else {
                                            dialogFragment.showError();
                                        }
                                    }
                                });

                    }
                    dialogFragment.show(getSupportFragmentManager(), "");
                }
            });
        }
    }

    private void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, intentFilter);
    }

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int levelPercent = (int) (level * 100f / scale);
            LogUtil.d("电池电量百分比 = " + levelPercent);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    LogUtil.d("充电中");
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    LogUtil.d("未充电");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    LogUtil.d("充电完成");
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    LogUtil.d("放电中");
                    break;
            }
        }
    };

    private void getLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 既没有打开gps也没有打开网络
        if (!isGpsOpened() && !isNewWorkOpen()) {
            Toast.makeText(this, "请打开网络或GPS定位功能!", Toast.LENGTH_SHORT).show();
//			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			startActivityForResult(intent, 0);
			return;
		}
		ThreadUtils.newThread(new Runnable() {
			@Override
			public void run() {
				try {
					Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location == null) {
						Log.d(TAG, "gps.location = null");
						location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
					Log.d(TAG, "network.location = " + location);
					Geocoder geocoder = new Geocoder(CommonUtils.getContext(), Locale.getDefault());
					if (location == null) {
						return;
					}
					try {
						List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
						if (addresses.size() > 0) {
							Address address = addresses.get(0);
							String city = address.getLocality();
							LogUtil.d("city = " + city);

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		});


	}

	private boolean isGpsOpened() {
		boolean isOpen = true;
		// 没有开启GPS
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			isOpen = false;
		}
		return isOpen;
	}

	private boolean isNewWorkOpen() {
		boolean isOpen = true;
		// 没有开启网络定位
		if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			isOpen = false;
		}
		return isOpen;
	}

	@Override
	protected void onStart() {
		super.onStart();
		mStaticHandler.post(mRefreshTimeRunnable);
	}

	private static class StaticHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}

	@Override
	protected void onDestroy() {
		mStaticHandler.removeCallbacksAndMessages(null);
		unregisterReceiver(mBatteryReceiver);
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		mStaticHandler.removeCallbacksAndMessages(null);
		super.onStop();
	}

	private void start() {
		mStaticHandler.post(mRefreshTimeRunnable);
	}

	private void stop() {
		mStaticHandler.removeCallbacksAndMessages(null);
		mStaticHandler = null;
	}

	private String getTime() {
		final Date date = new Date();
		mCalendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat("HH : mm");
		return sdf.format(date);
	}

	private String getDate() {
		final Date date = new Date();
		mCalendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(date);
	}

	private void alpha(View view) {
//		Animator animator = AnimatorInflater.loadAnimator(this, R.animator.anim_set);
//        animator.setTarget(view);
//		animator.start();
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_translate);
		view.startAnimation(animation);
	}
}
