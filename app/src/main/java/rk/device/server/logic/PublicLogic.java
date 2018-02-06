package rk.device.server.logic;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.koushikdutta.async.http.Multimap;

import java.util.List;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.global.Constant;
import rk.device.launcher.service.DownLoadIntentService;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;

/**
 * Created by hanbin on 2018/2/5.
 */
public class PublicLogic extends BaseLogic {

    private static final String TAG               = "PublicLogic";
    private static PublicLogic  publicLogic       = null;
    private DeviceUuidFactory   deviceUuidFactory = null;

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
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(300, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(300, "请填写正确的UUID: " + getUUID());
        }
        int code = TypeTranUtils.str2Int(params.getString("code"));
        String file = params.getString("file");
        int currentVersion = PackageUtils.getCurrentVersionCode();
        if (code > currentVersion) {
            DownLoadIntentService.startDownLoad(LauncherApplication.getContext(), file,
                    Constant.KEY_ROM);
        }
        JSONObject result = new JSONObject();
        result.put("code", PackageUtils.getCurrentVersionCode());
        result.put("ver", PackageUtils.getCurrentVersion());
        return onSuccess(result, "请求成功");
    }

    /**
     * 广告
     *
     * @param params
     * @return
     */
    public JSONObject ad(Multimap params) {
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(300, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(300, "请填写正确的UUID: " + getUUID());
        }
        String videoUrl = params.getString("video_url");
        List<String> imageList = params.get("image_list");

        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }
}
