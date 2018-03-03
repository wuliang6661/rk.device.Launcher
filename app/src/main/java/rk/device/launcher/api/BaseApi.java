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
    @POST("/api/v1/public/getconfig")
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
    @POST("/api/v1/device/activation")
    Observable<BaseResult<Object>> activationDiveces(@Body RequestBody requestBody);

    /**
     * 获取开门token
     */
    @POST("/api/v1/device/get_token")
    Observable<BaseResult<TokenBo>> getToken(@Body RequestBody requestBody);

    /**
     * 同步开门记录
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/device/upload_access_record")
    Observable<BaseResult<Object>> syncRecords(@Body RequestBody requestBody);

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
    Observable<BaseResult<StatusBo>> uploadDeviceStatus(@Body RequestBody requestBody);

    /**
     * 提交新增用户接口
     */
    @POST("/api/v1/person/add")
    Observable<BaseResult<Object>> addUser(@Body RequestBody requestBody);

    /**
     * 修改用户
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/person/edit")
    Observable<BaseResult<Object>> editUser(@Body RequestBody requestBody);

    /**
     * 删除用户
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/person/delete")
    Observable<BaseResult<Object>> deleteUser(@Body RequestBody requestBody);

    /**
     * 增加凭据 - 卡
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_card/add")
    Observable<BaseResult<Object>> addCard(@Body RequestBody requestBody);

    /**
     * 修改凭据 - 卡
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_card/edit")
    Observable<BaseResult<Object>> editCard(@Body RequestBody requestBody);

    /**
     * 删除凭据 - 卡
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_card/delete")
    Observable<BaseResult<Object>> deleteCard(@Body RequestBody requestBody);

    /**
     * 增加凭据 - 人脸
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_face/add")
    Observable<BaseResult<Object>> addFace(@Body RequestBody requestBody);

    /**
     * 修改凭据 - 人脸
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_face/edit")
    Observable<BaseResult<Object>> editFace(@Body RequestBody requestBody);

    /**
     * 删除凭据 - 人脸
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_face/delete")
    Observable<BaseResult<Object>> deleteFace(@Body RequestBody requestBody);

    /**
     * 增加凭据 - 密码
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_password/add")
    Observable<BaseResult<Object>> addPassword(@Body RequestBody requestBody);

    /**
     * 修改凭据 - 密码
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_password/edit")
    Observable<BaseResult<Object>> editPassword(@Body RequestBody requestBody);

    /**
     * 删除凭据 - 密码
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_password/delete")
    Observable<BaseResult<Object>> deletePassword(@Body RequestBody requestBody);

    /**
     * 增加凭据 - 指纹
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_fingerprint/add")
    Observable<BaseResult<Object>> addFinger(@Body RequestBody requestBody);

    /**
     * 修改凭据 - 指纹
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_fingerprint/edit")
    Observable<BaseResult<Object>> editFinger(@Body RequestBody requestBody);

    /**
     * 删除凭据 - 指纹
     *
     * @param requestBody
     * @return
     */
    @POST("/api/v1/credential_fingerprint/delete")
    Observable<BaseResult<Object>> deleteFinger(@Body RequestBody requestBody);

}
