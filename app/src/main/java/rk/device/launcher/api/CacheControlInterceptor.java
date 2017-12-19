package rk.device.launcher.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rk.device.launcher.utils.CommonUtils;
import rk.device.launcher.utils.NetUtils;

/**
 * Created by mundane on 2017/12/19 下午3:34
 */

public class CacheControlInterceptor implements Interceptor {
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		boolean isNetworkConnected = NetUtils.isNetworkConnected(CommonUtils.getContext());
		if (!isNetworkConnected) {
			CacheControl cacheControl = new CacheControl.Builder()
			.maxAge(0, TimeUnit.SECONDS)
			.maxStale(365, TimeUnit.DAYS)
			.build();
			request = request.newBuilder()
			.cacheControl(cacheControl)
			.build();
		}
		Response originalResponse = chain.proceed(request);
		if (isNetworkConnected) {
			int maxAge = 0; // 表示当访问此网页后的max-age秒内再次访问不会去服务器请求
			return originalResponse.newBuilder()
			.removeHeader("Pragma")
			.header("Cache-Control", "public ,max-age=" + maxAge)
			.build();
		} else {
			long maxStale = 60 * 60 * 24 * 30L; // 缓存的过期时间是30天
			return originalResponse.newBuilder()
			.removeHeader("Pragma")
			.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
			.build();
		}
	}
}
