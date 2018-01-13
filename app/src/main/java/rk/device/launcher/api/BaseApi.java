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
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.OpenDoorBo;
import rk.device.launcher.bean.SyncOpendoorHistoryBO;
import rk.device.launcher.bean.TokenBO;
import rk.device.launcher.bean.VerifyBO;
import rk.device.launcher.bean.VersionBO;
import rk.device.launcher.bean.WeatherBO;
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
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET(ApiName.WEATHER)
    Observable<BaseResult<List<WeatherBO>>> weather(@QueryMap Map<String, Object> params);

    /**
     * 检查app是否有更新
     */
    @FormUrlEncoded
    @POST(ApiName.UPDATE)
    Observable<BaseResult<VersionBO>> updateApp(@Field("ver") String ver); //版本号

    /**
     * 获取配置接口
     */
    @FormUrlEncoded
    @POST("/public/rest/face/config")
    Observable<BaseResult<DeviceInfoBO>> deviceConfiguration(@Field("ver") String ver, //版本号
                                                             @Field("cid") String cid); //客户号Id

    /**
     * 人脸验证
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(ApiName.VERIFY)
    Observable<BaseResult<VerifyBO>> verify(@FieldMap Map<String, Object> params);


    /**
     * 开门授权同步接口
     * @param access_token
     * @param uuid
     * @param peopleId
     * @param popeName
     * @param popedomType
     * @param type
     * @param data
     * @return
     */
    @FormUrlEncoded
    @POST("/public/rest/face/upAuthlist")
    Observable<BaseResult<SyncOpendoorHistoryBO>> upAuthlist(@Field("access_token") String access_token,
                                                             @Field("uuid") String uuid,
                                                             @Field("peopleId") String peopleId,
                                                             @Field("popeName") String popeName,
                                                             @Field("popedomType") String popedomType,
                                                             @Field("type") String type,
                                                             @Field("data") String data);


    /**
     * 设备开门鉴权token请求接口
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/public/rest/face/token")
    Observable<BaseResult<TokenBO>> obtainToken(@FieldMap Map<String, Object> params);

    /**
     * 开门
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/public/rest/face/token")
    Observable<BaseResult<OpenDoorBo>> openDoor(@FieldMap Map<String, Object> params);

}
