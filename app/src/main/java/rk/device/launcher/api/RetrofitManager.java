package rk.device.launcher.api;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by mundane on 2017/12/4 下午3:38
 */

public class RetrofitManager {

    private final Retrofit mRetrofit;
    private final String BASE_URL = "http://int.dpool.sina.com.cn/";
    private final AddressAPI mAddressAPI;

    private RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .client(genericClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        // 所有的请求方法都放在同一个interface里, 所以这里创建出来的class不会很多, 所以可以用这一个
        mAddressAPI = mRetrofit.create(AddressAPI.class);
    }

    public AddressAPI getAddressAPI() {
        return mAddressAPI;
    }

    private static RetrofitManager INSTANCE;

    public static RetrofitManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final RetrofitManager INSTANCE = new RetrofitManager();
    }


    private OkHttpClient genericClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(INFO, decodeUnicodeToString(message), null);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .addInterceptor(logging)
//		    .addInterceptor(new Interceptor() {
//			    @Override
//			    public Response intercept(Chain chain) throws IOException {
//				    Request request = chain.request().newBuilder()
//				    .addHeader("Content-Type", "text/html")
//				    .addHeader("Accept", "text/html")
//				    .build();
//				    return chain.proceed(request);
//			    }
//		    })
                .build();
        return okHttpClient;
    }

    private String decodeUnicodeToString(String uString) {
        StringBuilder sb = new StringBuilder();
        int i = -1, pos = 0;
        while ((i = uString.indexOf("\\u", pos)) != -1) {
            sb.append(uString.substring(pos, i));
            if (i + 5 < uString.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(uString.substring(i + 2, i + 6), 16));
            }
        }
        sb.append(uString.substring(pos));
        return sb.toString();
    }
}
