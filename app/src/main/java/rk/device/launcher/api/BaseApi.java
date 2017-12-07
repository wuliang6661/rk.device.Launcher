package rk.device.launcher.api;

import java.util.List;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rk.device.launcher.bean.BaseResult;
import rk.device.launcher.bean.DeviceInfoBean;
import rk.device.launcher.bean.VerifyBean;
import rk.device.launcher.bean.VersionBean;
import rk.device.launcher.bean.WeatherModel;
import rx.Observable;

/**
 * Created by hb on 16/3/8.
 * <p>
 * 此处存放所有后台接口
 */
public interface BaseApi {

    /**
     * 天气接口
     */
    @Headers({ "Content-Type: application/json", "Accept: application/json" })
    @GET(ApiName.WEATHER)
    Observable<BaseResult<List<WeatherModel>>> weather(@QueryMap Map<String, Object> params);

    /**
     * 检查app是否有更新
     */
    @FormUrlEncoded
    @POST(ApiName.UPDATE)
    Observable<BaseResult<VersionBean>> updateApp(@Field("ver") String ver); //版本号

    /**
     * 获取配置接口
     */
    @FormUrlEncoded
    @POST("/public/rest/face/config")
    Observable<BaseResult<DeviceInfoBean>> deviceConfiguration(@Field("ver") String ver, //版本号
                                                               @Field("cid") String cid); //客户号Id

    /**
     * 人脸验证
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(ApiName.VERIFY)
    Observable<BaseResult<VerifyBean>> verify(@FieldMap Map<String, Object> params);

}
