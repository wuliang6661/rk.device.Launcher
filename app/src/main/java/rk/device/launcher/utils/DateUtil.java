package rk.device.launcher.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by lmt on 16/7/6.
 */
public class DateUtil {

    public static final String ymdhms = "yyyy-MM-dd HH:mm:ss";
    public static final String ymd = "yyyy-MM-dd";

    public static String monthNumToMonthName(String month) {
        String m = month;
        if ("1".equals(month)) {
            m = "一月份";
        } else if ("2".equals(month)) {
            m = "二月份";
        } else if ("3".equals(month)) {
            m = "三月份";
        } else if ("4".equals(month)) {
            m = "四月份";
        } else if ("5".equals(month)) {
            m = "五月份";
        } else if ("6".equals(month)) {
            m = "六月份";
        } else if ("7".equals(month)) {
            m = "七月份";
        } else if ("8".equals(month)) {
            m = "八月份";
        } else if ("9".equals(month)) {
            m = "九月份";
        } else if ("10".equals(month)) {
            m = "十月份";
        } else if ("11".equals(month)) {
            m = "十一月份";
        } else if ("12".equals(month)) {
            m = "十二月份";
        }
        return m;
    }

    public static String getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month > 9 ? month : ("0" + month)) + "-" + day;
    }

    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month > 9 ? month : ("0" + month)) + "-" + day;
    }

    public static List<Integer> getDateForString(String date) {
        List<Integer> list = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(date)) {
                String[] dates = date.split("-");
                if (dates.length >= 3) {
                    if (null != dates[0]) {

                        list.add(Integer.parseInt(dates[0]));
                    }

                    if (null != dates[1]) {

                        list.add(Integer.parseInt(dates[1]));
                    }

                    if (null != dates[2]) {

                        list.add(Integer.parseInt(dates[2]));
                    }

                    return list;
                } else {
                    return null;
                }

            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            return list;
        }

    }

    public static List<Integer> getDateAndTimeForString(String date) {
        List<Integer> list = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(date)) {
                String[] dates = date.split("-|:| ");
                if (dates.length >= 5) {
                    if (null != dates[0])
                        list.add(Integer.parseInt(dates[0]));
                    if (null != dates[1])
                        list.add(Integer.parseInt(dates[1]));

                    if (null != dates[2])
                        list.add(Integer.parseInt(dates[2]));

                    if (null != dates[3])
                        list.add(Integer.parseInt(dates[3]));

                    if (null != dates[4])
                        list.add(Integer.parseInt(dates[4]));
                    return list;
                } else {
                    return null;
                }

            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }finally {
            return list;
        }

    }

    public static List<Integer> getTimeForString(String date) {
        List<Integer> list = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(date)) {
                String[] dates = date.split(":");
                if (dates.length >= 2) {
                    if (null != dates[0])
                        list.add(Integer.parseInt(dates[0]));
                    if (null != dates[1])
                        list.add(Integer.parseInt(dates[1]));
                    return list;
                } else {
                    return null;
                }

            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }finally {
            return list;
        }

    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    public static String formatDate(String date, String format) {
        String resultD = date;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(date);
            resultD = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultD;
    }

	public static String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

    public static String formatDate(long milliseconds, String format) {
        String resultD = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = new Date(milliseconds);
            resultD = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultD;
    }

    public static Date formatDateStr(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date1;
    }

	public static int getMaxDayOfMonth(int year, int month) {
		Calendar c=Calendar.getInstance(); // 实例化一个日历对象
		c.set(Calendar.YEAR, year); // 年设置为2015年
		c.set(Calendar.MONTH, month); // 7月的index是6
		return c.getActualMaximum(Calendar.DATE);
	}

    /**
     * 通过年份和月份 得到当月的日子
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        Log.d("DateView", "DateView:First:" + calendar.getFirstDayOfWeek());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Log.d("DateView", "DateView:First:" + calendar.getFirstDayOfWeek());

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "周日";

            case 2:
                return "周一";

            case 3:
                return "周二";

            case 4:
                return "周三";

            case 5:
                return "周四";

            case 6:
                return "周五";

            case 7:
                return "周六";

            default:
                return "";

        }
    }

	static Calendar today = Calendar.getInstance();



	/*获取日期*/
	public static String getDay(String date){
		String h;
		String [] day = date.split("-");
		h = day[2];
		return h;
	}

	/*获取月份*/
	public static String getMonth(String date){
		String m;
		String [] day = date.split("-");
		m = day[1];
		return m;
	}

	/*获取年份*/
	public static String getYear(String date){
		String y;
		String [] day = date.split("-");
		y = day[0];
		return y;
	}

	/*获取当前系统时间*/
	public static String getSysDate(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(date);
	}

	/*格式化日期时间*/
	public static String formatDatetime(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		return sdf.format(date);
	}

	public static String formatDatetime(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Date d = sdf.parse(date);
		return d.toString();
	}

	public static String formatDatetime(String date,int forid){
		if(date == null ||"".equals(date.trim())){
			return "";
		}else{
			String str = "";
			str = date.substring(0,date.indexOf("."));
			String[] array = str.split(" ");
			String[] dates = array[0].split("-");
			switch (forid) {
				case 0:  //yyyy-MM-dd HH:mm:ss
					str = date.substring(0,date.indexOf("."));
					break;
				case 1:  //yyyy-MM-dd
					str = date.substring(0,date.indexOf("."));
					str = str.substring(0,str.indexOf(" "));
					break;
				case 2:  //yyyy年MM月dd日 HH:mm:ss
					str = dates[0]+"年"+dates[1]+"月"+dates[2]+"日 "+array[1];
					break;
				case 3:  //yyyy年MM月dd日 HH:mm
					str = dates[0]+"年"+dates[1]+"月"+dates[2]+"日 "+array[1].substring(0, array[1].lastIndexOf(":"));
					break;
				case 4:  //yyyy年MM月dd日 HH:mm:ss
					str = dates[0]+"年"+dates[1]+"月"+dates[2]+"日 ";
					break;
				default:
					break;
			}
			return str;
		}
	}

	/*获取当前时间的毫秒*/
	public String getSysTimeMillise(){
		long i = System.currentTimeMillis();
		return String.valueOf(i);
	}

	/**
	 * 获取星期几
	 * @param calendar
	 * @return
	 */
	public static String getWeek(Calendar calendar){
//		Calendar cal = Calendar.getInstance();
//		int i = cal.get(Calendar.DAY_OF_WEEK);
		Date date = new Date();
		calendar.setTime(date);
		int i = calendar.get(Calendar.DAY_OF_WEEK);
		switch (i) {
			case 1:
				return "星期日";
			case 2:
				return "星期一";
			case 3:
				return "星期二";
			case 4:
				return "星期三";
			case 5:
				return "星期四";
			case 6:
				return "星期五";
			case 7:
				return "星期六";
			default:
				return "";
		}
	}

	/**
	 * 获取星期几
	 * @return
	 */
	public static String getWeek(){
		Calendar cal = Calendar.getInstance();
		int i = cal.get(Calendar.DAY_OF_WEEK);
		switch (i) {
			case 1:
				return "星期日";
			case 2:
				return "星期一";
			case 3:
				return "星期二";
			case 4:
				return "星期三";
			case 5:
				return "星期四";
			case 6:
				return "星期五";
			case 7:
				return "星期六";
			default:
				return "";
		}
	}

	public static String formatCommentTime(String str){

		Date date = parse(str, "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		String dateStr = sdf.format(date);

		return dateStr;
	}

	public static Date parse(String str, String pattern, Locale locale) {
		if(str == null || pattern == null) {
			return null;
		}
		try {
			return new SimpleDateFormat(pattern, locale).parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}







	//农历月份
	static String[] nlMonth = {"正","二","三","四","五","六","七","八","九","十","十一","十二"};

	//农历日
	static String[] nlday = {"初一","初二","初三","初四","初五","初六","初七","初八","初九","初十",
			"十一","十二","十三","十四","十五","十六","十七","十八","十九","二十",
			"廿一","廿二","廿三","廿四","廿五","廿六","廿七","廿八","廿九","三十"};

	//农历天干
	String[] mten={"null","甲","乙","丙","丁","戊","己","庚","辛","壬","癸"};

	//农历地支
	String[] mtwelve={"null","子（鼠）","丑（牛）","寅（虎）","卯（兔）","辰（龙）",
			"巳（蛇）","午（马）","未（羊）","申（猴）","酉（鸡）","戌（狗）","亥（猪）"};

	public static String convertNlYear(String year){
		String maxYear = "";
		for(int i = 0; i < year.length(); i++){
			maxYear = maxYear + minCaseMax(year.substring(i,i+1));
		}
		return maxYear;
	}

	public static String convertNlMoeth(int month){
		String maxMonth = "";
		maxMonth = nlMonth[month - 1];
		return maxMonth;
	}

	public static String convertNlDay(int day){
		String maxDay = "";
		maxDay = nlday[day - 1];
		return maxDay;
	}

	public static String minCaseMax(String str){
		switch (Integer.parseInt(str)) {
			case 0:
				return "零";
			case 1:
				return "一";
			case 2:
				return "二";
			case 3:
				return "三";
			case 4:
				return "四";
			case 5:
				return "五";
			case 6:
				return "六";
			case 7:
				return "七";
			case 8:
				return "八";
			case 9:
				return "九";

			default:
				return "null";
		}
	}
}
