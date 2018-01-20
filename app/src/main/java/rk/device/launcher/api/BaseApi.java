package rk.device.launcher.api;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import rk.device.launcher.bean.BaseResult;
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.StatusBo;
import rk.device.launcher.bean.TokenBo;
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
     * 开门
     *
     * @param requestBody
     * @return
     */
    @POST("/public/rest/face/openDoor")
    Observable<BaseResult<StatusBo>> openDoor(@Body RequestBody requestBody);

    /**
     * 激活设备接口
     */
    @POST("/public/rest/face/activation")
    Observable<BaseResult<Object>> activationDiveces(@Body RequestBody requestBody);

    /**
     * 获取开门token
     */
    @POST("/public/rest/face/token")
    Observable<BaseResult<TokenBo>> getToken(@Body RequestBody requestBody);

    /**
     * 同步开门记录
     *
     * @param requestBody
     * @return
     */
    @POST("/public/rest/face/openHistory")
    Observable<BaseResult<StatusBo>> syncRecords(@Body RequestBody requestBody);


    /**
     * 提交新增用户接口
     */
    @POST("/public/rest/face/upAuthlist")
    Observable<BaseResult<Object>> syncPerson(@Body RequestBody requestBody);

    /**
     * 上传用户图片
     */
    @Multipart
    @POST("/public/rest/face/upload")
    Observable<BaseResult<String>> uploadFile(@Part("headerImage\"; filename=\"avatar.jpg") RequestBody file);

}