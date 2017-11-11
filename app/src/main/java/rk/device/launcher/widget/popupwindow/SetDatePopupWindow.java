package rk.device.launcher.widget.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import rk.device.launcher.R;
import rk.device.launcher.widget.loopview.LoopListener;
import rk.device.launcher.widget.loopview.LoopView;

/**
 * @author : mundane
 * @time : 2017/4/11 9:03
 * @description :
 * @file : SelectCoursePopupWindow.java
 */

public class SetDatePopupWindow extends PopupWindow {

	private Integer selectDay;
	private Integer maxYear;
	private static  int MIN_YEAR = 1900;
	private static  int MAX_YEAR = 2020;
	private Integer minYear;
	private Integer selectYear;
	private Integer selectMonth;
	private Integer selectHour;
	private Integer selectMin;
	private Integer minMonth;
	private Integer maxMonth;
	private Integer minDay;
	private Integer maxDay;

	public SetDatePopupWindow(Context context, View view) {
		super(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		Calendar c = Calendar.getInstance();

		final LoopView loopDay = (LoopView) view.findViewById(R.id.loop_day);
		loopDay.setArrayList(d(1, 30));
		if(selectDay!=null){
			loopDay.setCurrentItem(selectDay - 1);
		}else{
			loopDay.setCurrentItem(c.get(Calendar.DAY_OF_MONTH) - 1);
		}
		//loopDay.setNotLoop();

		final LoopView loopYear = (LoopView) view.findViewById(R.id.loop_year);
		if (maxYear != null) {
			MAX_YEAR = maxYear;
		}
		if (minYear != null) {
			MIN_YEAR = minYear;
		}
		// 1900, -1988
		loopYear.setArrayList(d(MIN_YEAR, MAX_YEAR - MIN_YEAR + 1));
		if(selectYear!=null){
			loopYear.setCurrentItem(selectYear-MIN_YEAR);
		}else{
			loopYear.setCurrentItem(c.get(Calendar.YEAR) - MIN_YEAR);
		}
		loopYear.setNotLoop();

		final LoopView loopMonth = (LoopView) view.findViewById(R.id.loop_month);
		loopMonth.setArrayList(d(1, 12));
		if(selectMonth!=null){
			loopMonth.setCurrentItem(selectMonth - 1);
		}else{
			loopMonth.setCurrentItem(c.get(Calendar.MONTH));
		}
		loopMonth.setNotLoop();


		final LoopListener maxDaySyncListener = new LoopListener() {
			@Override
			public void onItemSelect(int item) {
				Calendar c = Calendar.getInstance();
				boolean needFixed=true;
				if(minYear!=null){
					if(Integer.parseInt(loopYear.getCurrentItemValue())==minYear ){
						if(minMonth!=null){
							if(Integer.parseInt(loopMonth.getCurrentItemValue())<minMonth){
								loopMonth.setCurrentItem(minMonth - 1);
							}
						}
					}else if(Integer.parseInt(loopYear.getCurrentItemValue())<minYear){
						loopYear.setCurrentItem(minYear-MIN_YEAR);
					}
				}

				if(maxYear!=null){
					if(Integer.parseInt(loopYear.getCurrentItemValue())==maxYear ){
						if(maxMonth!=null){
							if(Integer.parseInt(loopMonth.getCurrentItemValue())>maxMonth){
								loopMonth.setCurrentItem(maxMonth - 1);
							}
						}
					}else if(Integer.parseInt(loopYear.getCurrentItemValue())>maxYear){
						loopYear.setCurrentItem(maxYear-MIN_YEAR);
					}
				}

				c.set(Integer.parseInt(loopYear.getCurrentItemValue()), Integer.parseInt(loopMonth.getCurrentItemValue()) - 1, 1);
				c.roll(Calendar.DATE, false);

				if(needFixed){
					int maxDayOfMonth = c.get(Calendar.DATE);
					int fixedCurr = loopDay.getCurrentItem();
					loopDay.setArrayList(d(1, maxDayOfMonth));
					// 修正被选中的日期最大值
					if (fixedCurr > maxDayOfMonth) fixedCurr = maxDayOfMonth - 1;
					loopDay.setCurrentItem(fixedCurr);
				}
			}
		};

		final LoopListener dayLoopListener=new LoopListener() {
			@Override
			public void onItemSelect(int item) {
				if(minYear!=null && minMonth!=null && minDay!=null
						&& Integer.parseInt(loopYear.getCurrentItemValue())==minYear
						&& Integer.parseInt(loopMonth.getCurrentItemValue())==minMonth
						&& Integer.parseInt(loopDay.getCurrentItemValue())<minDay
						){
					loopDay.setCurrentItem(minDay-1);
				}

				if(maxYear!=null && maxMonth!=null && maxDay!=null
						&& Integer.parseInt(loopYear.getCurrentItemValue())==maxYear
						&& Integer.parseInt(loopMonth.getCurrentItemValue())==maxMonth
						&& Integer.parseInt(loopDay.getCurrentItemValue())>maxDay
						){
					loopDay.setCurrentItem(maxDay-1);
				}
			}
		};
		loopYear.setListener(maxDaySyncListener);
		loopMonth.setListener(maxDaySyncListener);
		loopDay.setListener(dayLoopListener);

	}

	/**
	 * 将数字传化为集合，并且补充0
	 *
	 * @param startNum 数字起点
	 * @param count    数字个数
	 * @return
	 */
	private static List<String> d(int startNum, int count) {
		String[] values = new String[count];
		for (int i = startNum; i < startNum + count; i++) {
			String tempValue = (i < 10 ? "0" : "") + i;
			values[i - startNum] = tempValue;
		}
		return Arrays.asList(values);
	}

}
