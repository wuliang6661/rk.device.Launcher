package rk.device.launcher.api;


import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rk.device.launcher.Config;
import rk.device.launcher.bean.DeviceInfoBean;
import rk.device.launcher.bean.VerifyBean;
import rk.device.launcher.bean.VersionBean;
import rk.device.launcher.bean.WeatherModel;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rx.Observable;

/**
 * Created by hanbin on 2017/11/23.
 * <p>
 * 所有网络接口的实现类
 */

public class ApiService {


    private static volatile Retrofit mApiRetrofit;

    private static String IP;
    private static String HOST;

    /**
     * 动态分配IP地址
     **/
    private static BaseApi weatherFactorys() {
        IP = SPUtils.getString(Constant.KEY_IP);
        HOST = SPUtils.getString(Constant.KEY_PORT);
        if (StringUtils.isEmpty(IP) || StringUtils.isEmpty(HOST)) {
            if (mApiRetrofit == null) {
                synchronized (Retrofit.class) {
                    if (mApiRetrofit == null) {
                        mApiRetrofit = new RkRetrofit().init_api(Config.APP_WEATHER);
                    }
                }
            }
            return mApiRetrofit.create(BaseApi.class);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("http://");
            builder.append(IP);
            builder.append(":");
            builder.append(HOST);
            if (mApiRetrofit == null) {
                synchronized (Retrofit.class) {
                    if (mApiRetrofit == null) {
                        mApiRetrofit = new RkRetrofit().init_api(builder.toString());
                    }
                }
            }
            return mApiRetrofit.create(BaseApi.class);
        }
    }


    /**
     * 切換IP和端口之后需清空请求对象
     */
    public static void clearIP() {
        mApiRetrofit = null;
    }


    private static AddressAPI createAddressAPI() {
        return RetrofitManager.getInstance().getAddressAPI();
    }


    /**
     * 获取天气接口
     *
     * @param params
     * @return
     */
    public static Observable<List<WeatherModel>> weather(Map<String, Object> params) {
        return weatherFactorys().weather(params).compose(RxResultHelper.httpRusult());
    }

    /**
     * 访问外网, 根据IP地址获取地址
     *
     * @return
     */
    public static Observable<String> address(String format) {
        return createAddressAPI().getAddress(format);
    }

    /**
     * 检测App是否更新
     */
    public static Observable<VersionBean> updateApp(String verCode) {
        return weatherFactorys().updateApp(verCode).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取配置接口
     */
    public static Observable<DeviceInfoBean> deviceConfiguration(String verCode, String cid) {
        return weatherFactorys().deviceConfiguration(verCode, cid)
                .compose(RxResultHelper.httpRusult());
    }

    /**
     * 人脸识别
     *
     * @param params
     * @return
     */
    public static Observable<VerifyBean> verifyFace(Map<String, Object> params) {
        return weatherFactorys().verify(params).compose(RxResultHelper.httpRusult());
    }

}
