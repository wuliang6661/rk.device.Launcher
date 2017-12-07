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
import rk.device.launcher.Config;
import rk.device.launcher.R;
import rk.device.launcher.global.LauncherApplication;
import rk.device.launcher.utils.NetWorkUtil;


/**
 * Created by hb on 16/3/7.
 */
public class RkRetrofit {

    private OkHttpClient apiClient = null;

    Interceptor mInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder request = chain.request().newBuilder();
            return chain.proceed(request.build());
        }
    };

    /**
     * Api接口
     *
     * @return
     */
    public Retrofit init_api() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        File cacheFile = new File(LauncherApplication.getContext().getCacheDir(), "cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        apiClient = new OkHttpClient
                .Builder()
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .addInterceptor(mInterceptor)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .build();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Config.APP_WEATHER)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(apiClient).build();
        return retrofit;
    }
    

    class HttpCacheInterceptor implements Interceptor {

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
