package rk.device.launcher.api;

import java.util.List;
import java.util.Map;

import rk.device.launcher.bean.DeviceInfoBean;
import rk.device.launcher.bean.VerifyBean;
import rk.device.launcher.bean.VersionBean;
import rk.device.launcher.bean.WeatherModel;
import rx.Observable;

/**
 * Created by hanbin on 2017/11/23.
 * <p>
 * 所有网络接口的实现类
 */

public class ApiService {

    /**
     * 获取天气接口
     *
     * @param params
     * @return
     */
    public static Observable<List<WeatherModel>> weather(Map<String, Object> params) {
        return ApiFactory.weatherFactory().weather(params).compose(RxResultHelper.httpRusult());
    }
	
	/**
	 * 访问外网, 根据IP地址获取地址
	 * @return
	 */
	public static Observable<String> address(String format) {
		return ApiFactory.createAddressAPI().getAddress(format);
	}
	
	/**
     * 检测App是否更新
     */
    public static Observable<VersionBean> updateApp(String verCode) {
        return ApiFactory.weatherFactory().updateApp(verCode).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取配置接口
     */
    public static Observable<DeviceInfoBean> deviceConfiguration(String verCode, String cid) {
        return ApiFactory.weatherFactory().deviceConfiguration(verCode, cid)
                .compose(RxResultHelper.httpRusult());
    }

    /**
     * 人脸识别
     * 
     * @param params
     * @return
     */
    public static Observable<VerifyBean> verifyFace(Map<String, Object> params) {
        return ApiFactory.weatherFactory().verify(params).compose(RxResultHelper.httpRusult());
    }

}
