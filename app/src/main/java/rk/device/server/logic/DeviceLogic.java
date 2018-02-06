package rk.device.server.logic;

import com.alibaba.fastjson.JSONObject;
import com.koushikdutta.async.http.Multimap;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 设备逻辑
 */
public class DeviceLogic extends BaseLogic{

    private static DeviceLogic deviceLogic       = null;
    private DeviceUuidFactory  deviceUuidFactory = null;

    public DeviceLogic() {
        if (deviceUuidFactory == null) {
            deviceUuidFactory = new DeviceUuidFactory(LauncherApplication.getContext());
        }
    }

    public static DeviceLogic getInstance() {
        if (deviceLogic == null) {
            synchronized (DeviceLogic.class) {
                if (deviceLogic == null) {
                    deviceLogic = new DeviceLogic();
                }
            }
        }
        return deviceLogic;
    }

    /**
     * 一键开门
     *
     * @param params
     * @return
     */
    public JSONObject open(Multimap params) {
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }

    /**
     * 获取设备状态
     *
     * @param params
     * @return
     */
    public JSONObject status(Multimap params) {
        JSONObject result = new JSONObject();
        result.put("uuid", deviceUuidFactory.getUuid());
        result.put("mac", FileUtils.readFile2String("/proc/board_sn", "UTF-8"));
        return onSuccess(result, "请求成功");
    }
}
