package rk.device.server.logic;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;

/**
 * Created by hanbin on 2018/2/5.
 */

public class BaseLogic {

    private DeviceUuidFactory uuidFactory = new DeviceUuidFactory(LauncherApplication.getContext());

    public JSONObject onSuccess(JSONObject data, String message) {
        return onResult(200, 10, message, data);
    }

    public JSONObject onError(int code, String message) {
        return onResult(code, 10, message, new JSONObject());
    }

    private JSONObject onResult(int result, int code, String message, JSONObject data) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("result", result);
        resultJson.put("code", code);
        resultJson.put("message", TextUtils.isEmpty(message) ? "ok" : message);
        resultJson.put("data", data);
        return resultJson;
    }

    public String getUUID() {
        if (uuidFactory == null) {
            uuidFactory = new DeviceUuidFactory(LauncherApplication.getContext());
        }
        return uuidFactory.getUuid().toString();
    }
}
