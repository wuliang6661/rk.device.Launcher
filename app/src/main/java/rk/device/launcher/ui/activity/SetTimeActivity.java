package rk.device.launcher.ui.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.ui.fragment.SetDateDialogFragment;
import rk.device.launcher.ui.fragment.SetTimeDialogFragment;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.DrawableUtil;

public class SetTimeActivity extends BaseActivity {

	@Bind(R.id.iv_back)
	ImageView mIvBack;
	@Bind(R.id.tv_title)
	TextView mTvTitle;
	@Bind(R.id.ll_set_date)
	LinearLayout mLlSetDate;
	@Bind(R.id.btn_finish_setting)
	Button mBtnFinishSetting;
	@Bind(R.id.tv_date)
	TextView mTvDate;
	@Bind(R.id.ll_set_time)
	LinearLayout mLlSetTime;
	@Bind(R.id.tv_time)
	TextView mTvTime;
	private int mSelectedYear;
	private int mSelectedMonth;
	private int mSelectedDay;
	private int mSelectedHour;
	private int mSelectedMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time);
		ButterKnife.bind(this);
		hideNavigationBar();
		mTvTitle.setText("时间设置");
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		mSelectedYear = calendar.get(Calendar.YEAR);
		mSelectedMonth = calendar.get(Calendar.MONTH) + 1;
		mSelectedDay = calendar.get(Calendar.DAY_OF_MONTH);
		mSelectedHour = calendar.get(Calendar.HOUR_OF_DAY);
		mSelectedMin = calendar.get(Calendar.MINUTE);


		mTvDate.setText(DateUtil.formatDate(date, "yyyy-MM-dd"));
		mTvTime.setText(DateUtil.formatDate(date, "HH:mm"));
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mLlSetDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SetDateDialogFragment dialogFragment = SetDateDialogFragment.newInstance();
				dialogFragment.setSelectedDate(mSelectedYear, mSelectedMonth, mSelectedDay);
				dialogFragment.setOnConfirmDialogListener(new SetDateDialogFragment.OnConfirmDialogListener() {
					@Override
					public void onClickConfirm(String year, String month, String day) {
						mSelectedYear = Integer.parseInt(year);
						mSelectedMonth = Integer.parseInt(month);
						mSelectedDay = Integer.parseInt(day);
						mTvDate.setText(String.format("%s-%s-%s", year, month, day));
					}
				});
				dialogFragment.show(getSupportFragmentManager(), "");
			}
		});
		mLlSetTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SetTimeDialogFragment setTimeDialogFragment = SetTimeDialogFragment.newInstance();
				setTimeDialogFragment.setSelectedTime(mSelectedHour, mSelectedMin);
				setTimeDialogFragment.setOnConfirmDialogListener(new SetTimeDialogFragment.OnConfirmDialogListener() {
					@Override
					public void onClickConfirm(String hour, String min) {
						mSelectedHour = Integer.parseInt(hour);
						mSelectedMin = Integer.parseInt(min);
						mTvTime.setText(String.format("%s:%s", hour, min));
					}
				});
				setTimeDialogFragment.show(getSupportFragmentManager(), "");
			}
		});

		mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, mSelectedYear);
					c.set(Calendar.MONTH, mSelectedMonth - 1);
					c.set(Calendar.DAY_OF_MONTH, mSelectedDay);
					c.set(Calendar.HOUR_OF_DAY, mSelectedHour);
					c.set(Calendar.MINUTE, mSelectedMin);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);
					long when_time = c.getTimeInMillis();
					if (when_time / 1000 < Integer.MAX_VALUE) {
						SystemClock.setCurrentTimeMillis(when_time);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				finish();
			}
		});
		DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
//		mBtnFinishSetting.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//					case MotionEvent.ACTION_DOWN:
//						mBtnFinishSetting.getBackground().clearColorFilter();
//						mBtnFinishSetting.getBackground().setColorFilter(getResources().getColor(R.color.half_transparent_black), PorterDuff.Mode.SRC_ATOP);
//						break;
//					case MotionEvent.ACTION_UP:
//						mBtnFinishSetting.getBackground().clearColorFilter();
//						mBtnFinishSetting.setBackgroundResource(R.drawable.shape_btn_finish_setting);
//						break;
//				}
//				return false;
//			}
//		});
	}

//	public void testDate(){
//		try {
//			Process process = Runtime.getRuntime().exec("su");
//			String datetime="20131023.112800"; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
//			DataOutputStream os = new DataOutputStream(process.getOutputStream());
//			os.writeBytes("setprop persist.sys.timezone GMT\n");
//			os.writeBytes("/system/bin/date -s "+datetime+"\n");
//			os.writeBytes("clock -w\n");
//			os.writeBytes("exit\n");
//			os.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}
