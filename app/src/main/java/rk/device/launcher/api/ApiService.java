package rk.device.launcher.api;


import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.global.Config;
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.VerifyBO;
import rk.device.launcher.bean.VersionBO;
import rk.device.launcher.bean.WeatherBO;
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
    private static BaseActivity activity;

    /**
     * 动态分配IP地址
     **/
    private static BaseApi weatherFactorys() {
        String IP = SPUtils.getString(Constant.KEY_IP);
        String HOST = SPUtils.getString(Constant.KEY_PORT);
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


    public static void setActivity(BaseActivity activity) {
        ApiService.activity = activity;
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
     */
    public static Observable<List<WeatherBO>> weather(Map<String, Object> params) {
        return weatherFactorys().weather(params).compose(RxResultHelper.httpResult()).compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    /**
     * 访问外网, 根据IP地址获取地址
     */
    public static Observable<String> address(String format) {
        return createAddressAPI().getAddress(format).compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    /**
     * 检测App是否更新
     */
    public static Observable<VersionBO> updateApp(String verCode) {
        return weatherFactorys().updateApp(verCode).compose(RxResultHelper.httpResult()).compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    /**
     * 获取配置接口
     */
    public static Observable<DeviceInfoBO> deviceConfiguration(String verCode, String cid) {
        return weatherFactorys().deviceConfiguration(verCode, cid)
                .compose(RxResultHelper.httpResult()).compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    /**
     * 人脸识别
     */
    public static Observable<VerifyBO> verifyFace(Map<String, Object> params) {
        return weatherFactorys().verify(params).compose(RxResultHelper.httpResult()).compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

}
