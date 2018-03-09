package rk.device.launcher.utils.verify;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/3/5.
 */

public class OpenTwoUtils {


    public static void httpOpen(){
        try {
            String baseUrl = "http://121.41.123.16:8085/DeviceCenter/server/temp";
            // 新建一个URL对象
            URL url = new URL(baseUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
            urlConn.setRequestProperty("Content-Type", "application/json");
            //设置客户端与服务连接类型
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                Log.e("OpenTwoUtils", "Post方式请求成功");
            } else {
                Log.e("OpenTwoUtils", "Post方式请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
        }
    }
}
