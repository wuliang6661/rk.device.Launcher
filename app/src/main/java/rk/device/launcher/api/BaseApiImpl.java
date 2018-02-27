package rk.device.launcher.api;

import com.trello.rxlifecycle.ActivityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.DeviceInfoBO;
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
    private static BaseActivity activity;

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


    public static void setActivity(BaseActivity activity) {
        BaseApiImpl.activity = activity;
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
        return createAddressAPI().getAddress(format).compose(activity.bindUntilEvent(ActivityEvent.PAUSE));
    }

    /**
     * 获取配置接口
     */
    public static Observable<DeviceInfoBO> deviceConfiguration(String verCode, String cid) {
        JSONObject params = new JSONObject();
        try {
            params.put("ver", verCode);
            params.put("cid", cid);
        } catch (JSONException e) {

        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(params).toString());
        return apiFactory().deviceConfiguration(requestBody)
                .compose(RxResultHelper.httpResult());
    }

    /**
     * 开门
     *
     * @param token
     * @param uuid
     * @param type
     * @param time
     * @return
     */
    public static Observable<StatusBo> openDoor(String token, String uuid, int type, int time) {
        JSONObject params = new JSONObject();
        try {
            params.put("access_token", token);
            params.put("uuid", uuid);
            params.put("openType", type);
            params.put("time", time);
        } catch (JSONException e) {

        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(params).toString());
        return apiFactory().openDoor(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 激活设备
     */
    public static Observable<Object> activationDiveces(String uuid, String mac, String license) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", uuid);
            object.put("mac", mac);
            object.put("license", license);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(object).toString());
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
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(object).toString());
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
    public static Observable<StatusBo> syncRecords(JSONObject params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(params).toString());
        return apiFactory().syncRecords(requestBody).compose(RxResultHelper.httpResult());
    }


    /**
     * 新增用户接口
     */
    public static Observable<Object> syncPersons(User user, String url) {
        JSONObject object = new JSONObject();
        try {
            object.put("peopleId", user.getUniqueId());
            object.put("peopleName", user.getName());
            object.put("popedomType", user.getPopedomType());
            object.put("startTime", user.getStartTime() / 1000);
            object.put("endTime", user.getEndTime() / 1000);
            object.put("createTime", user.getCreateTime() / 1000);
            object.put("cardNo", user.getCardNo());
            object.put("fingerID1", user.getFingerID1());
            object.put("fingerID2", user.getFingerID2());
            object.put("fingerID3", user.getFingerID3());
            object.put("password", user.getPassWord());
            object.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
            object.put("uuid", new DeviceUuidFactory(Utils.getContext()).getUuid() + "");
            if (!StringUtils.isEmpty(url)) {
                object.put("faceID", url);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(object).toString());
        return apiFactory().syncPerson(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 上传用户人脸
     */
    public static Observable<String> updataImage(RequestBody requestBody) {
        return apiFactory().uploadFile(requestBody).compose(RxResultHelper.httpResult());
    }

    /**
     * 上传设备状态
     * @param params
     * @return
     */
    public static Observable<StatusBo> uploadDeviceStatus(JSONObject params){
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(params).toString());
        return apiFactory().uploadDeviceStatus(requestBody).compose(RxResultHelper.httpResult());
    }

     /**
      * 删除用户
      *
      * @param params
      * @return
      */
    public static Observable<StatusBo> deleteUser(JSONObject params){
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), setBaseMap(params).toString());
        return apiFactory().deleteUser(requestBody).compose(RxResultHelper.httpResult());
    }



    /**
     * 封装初始化数据
     *
     * @param jsonObject
     * @return
     */
    private static JSONObject setBaseMap(JSONObject jsonObject){
        JSONObject postData = new JSONObject();
        try {
            postData.put("result",200);
            postData.put("code",11);
            postData.put("message","post");
            postData.put("data",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

}