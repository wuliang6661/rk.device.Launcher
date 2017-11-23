package rk.device.launcher.api;

/**
 * Created by hb on 16/9/12.
 */
public class ApiFactory {
    public static BaseApi weatherApi = null;


    /**
     * 天气
     **/
    public static BaseApi weatherFactory() {
        if (weatherApi == null) {
            synchronized (ApiFactory.class) {
                if (weatherApi == null) {
                    weatherApi = RkRetrofitFactory.getApiInstance();
                }
            }
        }
        return weatherApi;
    }

}
