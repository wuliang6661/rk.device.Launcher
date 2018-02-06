package rk.device.server.logic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.koushikdutta.async.http.Multimap;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;

/**
 * Created by hanbin on 2018/2/5.
 */
public class PublicLogic extends BaseLogic {
    private static PublicLogic publicLogic       = null;
    private DeviceUuidFactory  deviceUuidFactory = null;

    public PublicLogic() {
        if (deviceUuidFactory == null) {
            deviceUuidFactory = new DeviceUuidFactory(LauncherApplication.getContext());
        }
    }

    public static PublicLogic getInstance() {
        if (publicLogic == null) {
            synchronized (PublicLogic.class) {
                if (publicLogic == null) {
                    publicLogic = new PublicLogic();
                }
            }
        }
        return publicLogic;
    }

    /**
     * 升级接口
     *
     * @param params
     * @return
     */
    public JSONObject update(Multimap params) {
        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("ver", "1.0.0");
        return onSuccess(result, "请求成功");
    }

    /**
     * 广告
     *
     * @param params
     * @return
     */
    public JSONObject ad(Multimap params) {
        JSONObject result = new JSONObject();
        result.put("video_url", "http://www.roombanker.cn");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("http://www.roombanker.cn/images/image1.ipg");
        jsonArray.add("http://www.roombanker.cn/images/image1.ipg");
        jsonArray.add("http://www.roombanker.cn/images/image1.ipg");
        result.put("imageList", jsonArray);
        return onSuccess(result, "请求成功");
    }
}
