package rk.device.launcher.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rk.device.launcher.bean.ConfigBO;
import rk.device.launcher.bean.StatusBo;
import rk.device.launcher.bean.TokenBo;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Config;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Observable;

/**
 * Created by hanbin on 2017/11/23.
 * <p>
 * 所有网络接口的实现类
 */

public class BaseApiImpl {

    private static volatile Retrofit mApiRetrofit;

    /**
     * 动态分配IP地址
     **/
    private static BaseApi apiFactory() {
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
        return apiFactory().weather(params).compose(RxResultHelper.httpResult());
    }

    /**
     * 访问外网, 根据IP地址获取地址
     */
    public static Observable<String> address(String format) {
        return createAddressAPI().getAddress(format);
    }

    /**
     * 获取配置接口
     */
    public static Observable<ConfigBO> deviceConfiguration() {
        JSONObject params = new JSONObject();
        try {
            params.put("uuid", new DeviceUuidFactory(Utils.getContext()).getUuid().toString());
            params.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
        } catch (JSONException e) {

        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().deviceConfiguration(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 激活设备
     */
    public static Observable<Object> activationDiveces(String uuid, String mac, String license) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", uuid);
            //            object.put("mac", mac);
            object.put("mac", mac);
            object.put("license", license);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                    object.toString());
            return apiFactory().activationDiveces(requestBody).compose(RxResultHelper.httpResult());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取accens_token
     */
    public static Observable<TokenBo> postToken(String uuid, String license) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", uuid);
            object.put("license", license);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                    object.toString());
            return apiFactory().getToken(requestBody).compose(RxResultHelper.httpResult());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步记录
     *
     * @param params
     * @return
     */
    public static Observable<Object> syncRecords(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().syncRecords(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 新增用户接口
     */
    public static Observable<Object> addUser(User user) {
        JSONObject object = new JSONObject();
        try {
            object.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
            object.put("uuid", new DeviceUuidFactory(Utils.getContext()).getUuid() + "");
            object.put("peopleId", user.getUniqueId());
            object.put("peopleName", user.getName());
            object.put("role", user.getRole());
            object.put("startTime", user.getStartTime() / 1000);
            object.put("endTime", user.getEndTime() / 1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                object.toString());
        return apiFactory().addUser(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 修改用户
     */
    public static Observable<Object> updateUser(User user) {
        JSONObject object = new JSONObject();
        try {
            object.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
            object.put("uuid", new DeviceUuidFactory(Utils.getContext()).getUuid() + "");
            object.put("peopleId", user.getUniqueId());
            object.put("peopleName", user.getName());
            object.put("role", user.getRole());
            object.put("startTime", user.getStartTime() / 1000);
            object.put("endTime", user.getEndTime() / 1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                object.toString());
        return apiFactory().editUser(requestBody).compose(RxResultHelper.httpResult());
    }


    /**
     * 上传用户人脸
     */
    public static Observable<String> updataImage(RequestBody requestBody) {
        return apiFactory().uploadFile(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 上传设备状态
     *
     * @param params
     * @return
     */
    public static Observable<StatusBo> uploadDeviceStatus(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().uploadDeviceStatus(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 删除用户
     *
     * @param params
     * @return
     */
    public static Observable<Object> deleteUser(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().deleteUser(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 添加卡
     *
     * @param params
     * @return
     */
    public static Observable<Object> addCard(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().addCard(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 编辑卡
     *
     * @param params
     * @return
     */
    public static Observable<Object> editCard(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().editCard(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 删除卡
     *
     * @param params
     * @return
     */
    public static Observable<Object> deleteCard(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().deleteCard(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 增加人脸
     */
    public static Observable<Object> addFace(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().addFace(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 修改人脸
     */
    public static Observable<Object> updateFace(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().editFace(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 删除人脸
     */
    public static Observable<Object> deleteFace(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().deleteFace(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 增加密码
     */
    public static Observable<Object> addPassWord(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().addPassword(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 修改密码
     */
    public static Observable<Object> updatePassWord(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().editPassword(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 删除密码
     */
    public static Observable<Object> deletePassWord(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().deletePassword(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 添加指纹
     *
     * @param params
     * @return
     */
    public static Observable<Object> addFinger(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().addFinger(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 更新指纹
     *
     * @param params
     * @return
     */
    public static Observable<Object> editFinger(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().editFinger(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 删除指纹
     *
     * @param params
     * @return
     */
    public static Observable<Object> deleteFinger(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                params.toString());
        return apiFactory().deleteFinger(requestBody).compose(RxResultHelper.httpResult());
    }

}
