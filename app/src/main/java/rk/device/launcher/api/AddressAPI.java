package rk.device.launcher.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mundane on 2017/12/7 下午1:57
 */

public interface AddressAPI {
	// http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js
	@GET("iplookup/iplookup.php")
	Observable<String> getAddress(@Query("format") String format);
}
