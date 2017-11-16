package rk.device.launcher.ui.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.R;
import rk.device.launcher.ui.fragment.SetDateDialogFragment;
import rk.device.launcher.ui.fragment.SetTimeDialogFragment;
import rk.device.launcher.utils.DateUtil;

public class SetTimeActivity extends BaseActivity {

	@BindView(R.id.iv_back)
	ImageView mIvBack;
	@BindView(R.id.tv_title)
	TextView mTvTitle;
	@BindView(R.id.ll_set_date)
	LinearLayout mLlSetDate;
	@BindView(R.id.btn_finish_setting)
	Button mBtnFinishSetting;
	@BindView(R.id.tv_date)
	TextView mTvDate;
	@BindView(R.id.ll_set_time)
	LinearLayout mLlSetTime;
	@BindView(R.id.tv_time)
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
				finish();
			}
		});
		mBtnFinishSetting.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mBtnFinishSetting.getBackground().clearColorFilter();
						mBtnFinishSetting.getBackground().setColorFilter(getResources().getColor(R.color.half_transparent_black), PorterDuff.Mode.SRC_ATOP);
						break;
					case MotionEvent.ACTION_UP:
						mBtnFinishSetting.getBackground().clearColorFilter();
						mBtnFinishSetting.setBackgroundResource(R.drawable.shape_btn_finish_setting);
						break;
				}
				return false;
			}
		});
	}

}
