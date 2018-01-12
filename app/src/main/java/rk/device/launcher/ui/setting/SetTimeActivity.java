package rk.device.launcher.ui.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.bean.event.TimeEvent;
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
	@Bind(R.id.ll_set_time_zone)
	LinearLayout mLlSetTimeZone;
	@Bind(R.id.tv_time_zone)
	TextView mTvTimeZone;
	private int mSelectedYear;
	private int mSelectedMonth;
	private int mSelectedDay;
	private int mSelectedHour;
	private int mSelectedMin;
	private Calendar mDummyDate;
	
	
	@Override
	protected int getLayout() {
		return R.layout.activity_set_time;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	protected void initView() {
		setTitle("时间设置");
		goBack();
//		Calendar calendar = Calendar.getInstance();
//		Date date = new Date();
//		calendar.setTime(date);
//		mSelectedYear = calendar.get(Calendar.YEAR);
//		mSelectedMonth = calendar.get(Calendar.MONTH) + 1;
//		mSelectedDay = calendar.get(Calendar.DAY_OF_MONTH);
//		mSelectedHour = calendar.get(Calendar.HOUR_OF_DAY);
//		mSelectedMin = calendar.get(Calendar.MINUTE);
		mDummyDate = Calendar.getInstance();
		updateTimeAndDateDisplay(this);
		
//		mTvDate.setText(DateUtil.formatDate(date, "yyyy-MM-dd"));
//		mTvTime.setText(DateUtil.formatDate(date, "HH:mm"));
	}
	
	protected void initData() {
		setListener();
	}
	
	private final int REQUEST_CODE = 0X11;
	
	/**
	 * 设置监听器
	 */
	private void setListener() {
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
		
		mLlSetTimeZone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetTimeActivity.this, TimeZoneListActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimeEvent timeEvent = new TimeEvent(mSelectedYear, mSelectedMonth - 1, mSelectedDay, mSelectedHour, mSelectedMin);
				RxBus.getDefault().post(timeEvent);
				finish();
			}
		});
		DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case REQUEST_CODE:
				updateTimeAndDateDisplay(SetTimeActivity.this);
				break;
		}
		
	}
	
	public void updateTimeAndDateDisplay(Context context) {
		final Calendar now = Calendar.getInstance();
		mDummyDate.setTimeZone(now.getTimeZone());
		// We use December 31st because it's unambiguous when demonstrating the date format.
		// We use 13:00 so we can demonstrate the 12/24 hour options.
		mDummyDate.set(now.get(Calendar.YEAR), 11, 31, 13, 0, 0);
		Date dummyDate = mDummyDate.getTime();
//		mTvDate.setText(DateFormat.getLongDateFormat(context).format(now.getTime()));
		Date date = new Date();
		now.setTime(date);
		mSelectedYear = now.get(Calendar.YEAR);
		mSelectedMonth = now.get(Calendar.MONTH) + 1;
		mSelectedDay = now.get(Calendar.DAY_OF_MONTH);
		mSelectedHour = now.get(Calendar.HOUR_OF_DAY);
		mSelectedMin = now.get(Calendar.MINUTE);
		mTvDate.setText(DateUtil.formatDate(date, "yyyy-MM-dd"));
//		mTvTime.setText(DateFormat.getTimeFormat(context).format(now.getTime()));
		mTvTime.setText(DateUtil.formatDate(date, "HH:mm"));
		mTvTimeZone.setText(getTimeZoneText(now.getTimeZone(), true));
	}
	
	public static String getTimeZoneText(TimeZone tz, boolean includeName) {
		Date now = new Date();
		
		// Use SimpleDateFormat to format the GMT+00:00 string.
		SimpleDateFormat gmtFormatter = new SimpleDateFormat("ZZZZ");
		gmtFormatter.setTimeZone(tz);
		// gmtString = GMT+10:00
		String gmtString = gmtFormatter.format(now);
		
		// Ensure that the "GMT+" stays with the "00:00" even if the digits are RTL.
		BidiFormatter bidiFormatter = BidiFormatter.getInstance();
		Locale l = Locale.getDefault();
		boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
		gmtString = bidiFormatter.unicodeWrap(gmtString,
		isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR);
		
		// attention
		if (!includeName) {
			return gmtString;
		}
		
		// Optionally append the time zone name.
		SimpleDateFormat zoneNameFormatter = new SimpleDateFormat("zzzz");
		zoneNameFormatter.setTimeZone(tz);
		// zoneNameString = 日本标准时间
		String zoneNameString = zoneNameFormatter.format(now);
		
		// We don't use punctuation here to avoid having to worry about localizing that too!
		return gmtString + " " + zoneNameString;
	}
	
}
