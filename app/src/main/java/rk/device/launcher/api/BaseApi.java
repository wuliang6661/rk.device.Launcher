package rk.device.launcher.api;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
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
     * 获取配置接口
     */
    @POST("/api/v1/public/config")
    Observable<BaseResult<DeviceInfoBO>> deviceConfiguration(@Body RequestBody requestBody); //客户号Id

    /**
     * 开门
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/public/opendoor")
    Observable<BaseResult<StatusBo>> openDoor(@Body RequestBody requestBody);

    /**
     * 激活设备接口
     */
    @POST("/api/v1/public/activation")
    Observable<BaseResult<Object>> activationDiveces(@Body RequestBody requestBody);

    /**
     * 获取开门token
     */
    @POST("/api/v1/public/token")
    Observable<BaseResult<TokenBo>> getToken(@Body RequestBody requestBody);

    /**
     * 同步开门记录
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/public/synchistory")
    Observable<BaseResult<StatusBo>> syncRecords(@Body RequestBody requestBody);


    /**
     * 提交新增用户接口
     */
    @POST("/api/v1/public/syncgrant")
    Observable<BaseResult<Object>> syncPerson(@Body RequestBody requestBody);

    /**
     * 上传用户图片
     */
    @Multipart
    @POST("/api/v1/public/upload")
    Observable<BaseResult<String>> uploadFile(@Part("headerImage\"; filename=\"avatar.jpg") RequestBody file);

    /**
     * 上传设备状态
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/public/updateDeviceStatus")
    Observable<BaseResult<StatusBo>> uploadDeviceStatus(@Body RequestBody  requestBody);
}