package rk.device.launcher.api;

/**
 * Created by hb on 16/9/12.
 * <p>
 * <p>
 * 获取请求接口的service
 */
public class ApiFactory {
    public static BaseApi weatherApi = null;


    /**
     * 返回请求接口服务
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
