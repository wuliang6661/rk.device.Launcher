package rk.device.launcher.base.utils;

/**
 * 类型转换帮助类
 * 
 * @author hb
 * 
 */
public class TypeTranUtils {
	public static int str2Int(String number) {
		int num = 0;
		try {
			num = Integer.parseInt(number);
		} catch (Exception e) {
		}
		return num;
	}

	public static float str2float(String number) {
		float num = 0;
		try {
			num = Float.parseFloat(number);
		} catch (Exception e) {
		}
		return num;
	}

	public static double str2double(String number) {
		double num = 0;
		try {
			num = Double.parseDouble(number);
		} catch (Exception e) {
		}
		return num;
	}

	public static long str2long(String number) {
		long num = 0;
		try {
			num = Long.parseLong(number);
		} catch (Exception e) {
		}
		return num;
	}

}
