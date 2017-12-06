package rk.device.launcher.bean;

/**
 * Created by mundane on 2017/12/6 下午1:53
 */

public class PushMessageModel {

	public String msgtype;
	public Data data;

	public static class Data {
		public String type; // 类型有两种rom、APK、text,img,video等
		public String ver; // "1.2.8"
		public String file; // "http://www.roombanker.cn/test.zip"
		public String note; // "rom 升级描述"
		public int code; // 11, apk升级才会有
		public String content; // "通知广告内容"
		public String url; // "http://www.baidu.com"
	}
}
