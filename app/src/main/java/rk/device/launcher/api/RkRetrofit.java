package rk.device.launcher.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rk.device.launcher.R;
import rk.device.launcher.global.LauncherApplication;
import rk.device.launcher.utils.NetWorkUtil;


/**
 * Created by hb on 16/3/7.
 * <p>
 * 所有网络请求及过滤器都在此处
 */
public class RkRetrofit {

    private OkHttpClient apiClient = null;

    /**
     * Api接口
     */
    Retrofit init_api(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        apiClient = new OkHttpClient
                .Builder()
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .build();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(apiClient).build();
        return retrofit;
    }


    private class HttpCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtil.isNetConnected(LauncherApplication.getContext())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                T.showShort(LauncherApplication.getContext().getResources().getString(R.string.network_illegal));
            }
            Response originalResponse = chain.proceed(request);
            if (NetWorkUtil.isNetConnected(LauncherApplication.getContext())) {
                //有网的时候
                //                return filter(originalResponse);
            }
            return originalResponse.newBuilder().build();
        }
    }
}
