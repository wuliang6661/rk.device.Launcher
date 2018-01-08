package rk.device.launcher.ui.person_add;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rk.device.launcher.R;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.widget.EasyPickView;


public class SetFullTimeDialogFragment extends DialogFragment {
    private static final String DIALOG_LEFT = "dialog_left";
    private static final String DIALOG_RIGHT = "dialog_right";
    private static final String DIALOG_WHEEL = "dialog_wheel";
    private static final String DIALOG_BACK = "dialog_back";
    private static final String DIALOG_CANCELABLE = "dialog_cancelable";
	private static final String DIALOG_CANCELABLE_TOUCH_OUT_SIDE = "dialog_cancelable_touch_out_side";

	private final int MIN_YEAR = 1900;
	private final int MAX_YEAR = 2100;
	private int mSelectedColor;
	private int mDividerColor;

    private Activity activity;



    private String[] dialogWheel;
    private String dialogLeft, dialogRight;
    private boolean isCancelableTouchOutSide, isCancelable, isBack;
    private EasyPickView mWheelViewYear;
    private EasyPickView mWheelViewMonth;
	private EasyPickView mWheelViewDay;
    private EasyPickView mWheelViewHour;
    private EasyPickView mWheelViewMin;
	private Calendar mCalendar;
	private TextView mTvCancel;
	private TextView mTvConfirm;
	private Integer mSelectedYear;
	private Integer mSelectedMonth;
	private Integer mSelectedDay;
    private Integer mSelectedHour;
    private Integer mSelectedMin;


    /**
     * 滑动选择对话框
     *
     * @return
     */
    public static SetFullTimeDialogFragment newInstance() {
        SetFullTimeDialogFragment wheelDialog = new SetFullTimeDialogFragment();

        Bundle bundle = new Bundle();
        wheelDialog.setArguments(bundle);

        return wheelDialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
//        //设置对话框显示在底部
//        getDialog().getWindow().setGravity(Gravity.BOTTOM);
//        //设置让对话框宽度充满屏幕
//		getDialog().getWindow().setLayout(ScreenUtil.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);

		Window window = getDialog().getWindow();
		WindowManager.LayoutParams attributes = window.getAttributes();
		attributes.gravity = Gravity.BOTTOM;
		attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(attributes);
	}


	@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mCalendar = Calendar.getInstance();
		mCalendar.setTime(new Date());
		if (mSelectedYear == null) {
			mSelectedYear = mCalendar.get(Calendar.YEAR);
		}
		if (mSelectedMonth == null) {
			mSelectedMonth = mCalendar.get(Calendar.MONTH) + 1;
		}
		if (mSelectedDay == null) {
			mSelectedDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		}
        if (mSelectedHour == null) {
            mSelectedHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        }
        if (mSelectedMin == null) {
            mSelectedMin = mCalendar.get(Calendar.MINUTE);
        }
        Bundle bundle = getArguments();
        dialogWheel = bundle.getStringArray(DIALOG_WHEEL);
        dialogLeft = bundle.getString(DIALOG_LEFT);
        dialogRight = bundle.getString(DIALOG_RIGHT);
        isBack = bundle.getBoolean(DIALOG_BACK, false);
        isCancelable = bundle.getBoolean(DIALOG_CANCELABLE, false);
        isCancelableTouchOutSide = bundle.getBoolean(DIALOG_CANCELABLE_TOUCH_OUT_SIDE, false);
    }

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mSelectedColor = getResources().getColor(R.color.blue_338eff);
//		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.fragment_dialog_set_full_time, container, false);

		initView(view);

		//设置窗口以对话框样式显示
//		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog);
		//设置对话框背景色，否则有虚框
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//设置对话框弹出动画，从底部滑入，从底部滑出
		getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//		getDialog().setCancelable(true);
//		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);

		setSubView();
		initEvent();

		return view;
	}

    private void initEvent() {
		mWheelViewMonth.setOnScrollChangedListener(new EasyPickView.OnScrollChangedListener() {
			@Override
			public void onScrollChanged(int curIndex) {
				fixDayOfMonth();
			}

			@Override
			public void onScrollFinished(int curIndex) {
				fixDayOfMonth();
			}
		});
		mWheelViewYear.setOnScrollChangedListener(new EasyPickView.OnScrollChangedListener() {
			@Override
			public void onScrollChanged(int curIndex) {
				fixDayOfMonth();
			}

			@Override
			public void onScrollFinished(int curIndex) {
				fixDayOfMonth();
			}
		});
		mTvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SetFullTimeDialogFragment.this.dismiss();
			}
		});

		mTvConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnConfirmDialogListener != null) {
					mOnConfirmDialogListener.onClickConfirm(
					        mWheelViewYear.getValue(),
                            mWheelViewMonth.getValue(),
                            mWheelViewDay.getValue(),
                            mWheelViewHour.getValue(),
                            mWheelViewMin.getValue()
                    );
					SetFullTimeDialogFragment.this.dismiss();
				}
			}
		});

    }

	private boolean isBigMonth(int index) {
		int[] bigMonthIndex = {0, 2, 4, 6, 7, 9, 11};
		for (int i : bigMonthIndex) {
			if (i == index) {
				return true;
			}
		}
		return false;
	}

	public interface OnConfirmDialogListener {
		void onClickConfirm(String year, String month, String day, String hour, String minute);
	}

	private OnConfirmDialogListener mOnConfirmDialogListener;

	public void setOnConfirmDialogListener(OnConfirmDialogListener listener) {
		mOnConfirmDialogListener = listener;
	}

    private void setSubView() {
		mWheelViewYear.setDataList(genrateData(MIN_YEAR, MAX_YEAR - MIN_YEAR + 1));
		mWheelViewYear.moveToImmidiatly(mSelectedYear - MIN_YEAR);

		mWheelViewMonth.setDataList(genrateData(1, 12));
		mWheelViewMonth.moveToImmidiatly(mSelectedMonth - 1);

		fixDayOfMonth();
		mWheelViewDay.moveToImmidiatly(mSelectedDay - 1);

        mWheelViewHour.setDataList(genrateData(0, 24));
        mWheelViewHour.moveToImmidiatly(mSelectedHour);

        mWheelViewMin.setDataList(genrateData(0, 60));
        mWheelViewMin.moveToImmidiatly(mSelectedMin);

//		setWheelView(mWheelViewYear);
//		setWheelView(mWheelViewMonth);
//		setWheelView(mWheelViewDay);

	}


	private static final String TAG = "SetFullTimeDialogFragme";
	
	public void setSelectedTime(int year, int month, int day, int hour, int min) {
		Log.d(TAG, "setSelectedTime() called with: year = [" + year + "], month = [" + month + "], day = [" + day + "], hour = [" + hour + "], min = [" + min + "]");
		mSelectedYear = year;
		mSelectedMonth = month;
		mSelectedDay = day;
        mSelectedHour = hour;
        mSelectedMin = min;
    }

	private void fixDayOfMonth() {
		int currentYear = MIN_YEAR + mWheelViewYear.getIndex();
		int monthIndex = mWheelViewMonth.getIndex();
		int maxDayOfMonth = DateUtil.getMaxDayOfMonth(currentYear, monthIndex);
		mWheelViewDay.updateDataList(genrateData(1, maxDayOfMonth));
	}

	private void setWheelView(EasyPickView easyPickView) {
		//设置是否可以上下无限滑动
//		wheelView.setWrapSelectorWheel(true);
//		wheelView.setDividerColor(ResUtil.getColor(R.color.colorPrimary));
//		wheelView.setSelectedTextColor(mSelectedColor);
//		wheelView.setNormalTextColor(ResUtil.getColor(android.R.color.white));
	}

	private void initView(View view) {
//        tvLeft = (TextView) view.findViewById(R.id.tv_wheel_dialog_left);
//        tvRight = (TextView) view.findViewById(R.id.tv_wheel_dialog_right);
		mTvCancel = view.findViewById(R.id.tv_cancel);
		mTvConfirm = view.findViewById(R.id.tv_confirm);
		mWheelViewYear = view.findViewById(R.id.wheelView_year);
		mWheelViewMonth = view.findViewById(R.id.wheelView_month);
		mWheelViewDay = view.findViewById(R.id.wheelView_day);
        mWheelViewHour = view.findViewById(R.id.wheelView_hour);
        mWheelViewMin = view.findViewById(R.id.wheelView_min);
    }


	/**
	 * 将数字传化为集合，并且补充0
	 *
	 * @param startNum 数字起点
	 * @param count    数字个数
	 * @return
	 */
	private static List<String> genrateData(int startNum, int count) {
		String[] values = new String[count];
		for (int i = startNum; i < startNum + count; i++) {
			String tempValue = (i < 10 ? "0" : "") + i;
			values[i - startNum] = tempValue;
		}
		return Arrays.asList(values);
	}
}
