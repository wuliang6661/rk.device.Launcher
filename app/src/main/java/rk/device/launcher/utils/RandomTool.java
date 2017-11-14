package rk.device.launcher.utils;

import java.util.Random;

public class RandomTool {
	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyz";
	public static final String letterChar = "abcdefghijklmnopqrstuvwxyz";
	public static final String numberChar = "0123456789";

	public static String generateString(int length) // 参数为返回随机数的长度
	{
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}
}
