package rk.device.launcher.api;

import java.util.Map;

import rk.device.launcher.bean.WeatherModel;
import rx.Observable;

/**
 * Created by hanbin on 2017/11/23.
 */

public class ApiService {

    /**
     * 获取天气接口
     *
     * @param params
     * @return
     */
    public static Observable<WeatherModel> weather(Map<String, Object> params) {
        return ApiFactory.weatherFactory()
                .weather(params)
                .compose(RxResultHelper.httpRusult());
    }


    /**
     * 检测App是否更新
     */
    public static Observable<String> updateApp(String verCode, String from) {
        return ApiFactory.weatherFactory().updateApp(verCode, from).compose(RxResultHelper.httpRusult());
    }


}
