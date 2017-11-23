package rk.device.launcher.api;

import retrofit2.Retrofit;

/**
 * Created by hb on 16/3/8.
 */
public class RkRetrofitFactory {
    private static volatile Retrofit mApiRetrofit;

    /***************************** 天气 ****************************/
    public static BaseApi getApiSingleton() {
        if (mApiRetrofit == null) {
            synchronized (Retrofit.class) {
                if (mApiRetrofit == null) {
                    RkRetrofit rkRetrofit = new RkRetrofit();
                    mApiRetrofit = rkRetrofit.init_api();
                }
            }
        }
        return mApiRetrofit.create(BaseApi.class);
    }

    private static class SingletonApiHolder {
        private static final BaseApi INSTANCE = getApiSingleton();
    }

    /**
     * 天气
     *
     * @return
     */
    public static BaseApi getApiInstance() {
        return SingletonApiHolder.INSTANCE;
    }

}
