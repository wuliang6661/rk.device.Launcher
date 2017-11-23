package rk.device.launcher.api;


import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rk.device.launcher.bean.BaseResult;
import rk.device.launcher.bean.WeatherModel;
import rx.Observable;

/**
 * Created by hb on 16/3/8.
 */
public interface BaseApi {


    /**
     * 天气接口
     *
     * @param params
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("public/rest/weather")
    Observable<BaseResult<WeatherModel>> weather(@QueryMap Map<String, Object> params);


    /**
     * 检查app是否有更新
     */
    @FormUrlEncoded
    @POST("/rest/update")
    Observable<BaseResult<String>> updateApp(@Field("ver") String ver,    //版本号
                                             @Field("from") String from);    //访问来源


}
