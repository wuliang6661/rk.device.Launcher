package rk.device.server.logic;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.koushikdutta.async.http.Multimap;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.utils.verify.OpenUtils;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 设备逻辑
 */
public class DeviceLogic extends BaseLogic {

    private static final String TAG               = "MemberLogic";
    private static DeviceLogic  deviceLogic       = null;
    private DeviceUuidFactory   deviceUuidFactory = null;

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
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(300, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(300, "请填写正确的UUID: " + getUUID());
        }
        OpenUtils.getInstance().openDoorJni(VerifyTypeConstant.TYPE_API, "000000", "远程开门");
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
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(300, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(300, "请填写正确的UUID: " + getUUID());
        }

        JSONObject result = new JSONObject();
        result.put("uuid", deviceUuidFactory.getUuid());
        result.put("mac", FileUtils.readFile2String("/proc/board_sn", "UTF-8"));
        result.put("hw_ver", Build.HARDWARE);
        result.put("version_code", PackageUtils.getCurrentVersionCode());
        result.put("version_name", PackageUtils.getCurrentVersion());
        result.put("imsi", PackageUtils.getImsi(LauncherApplication.getContext()));
        result.put("msisdn", PackageUtils.getMsisdn(LauncherApplication.getContext()));
        result.put("battery", LauncherApplication.sLevel);
        result.put("temperature", LauncherApplication.sTemperature);
        result.put("signal", WifiHelper.obtainWifiInfo(LauncherApplication.getContext()));
        result.put("card_capacity", 9999);
        result.put("whitelist_count", 9999);
        result.put("finger_capacity", LauncherApplication.totalUserCount);
        result.put("finger_count", LauncherApplication.remainUserCount);
        result.put("opened", 0);
        result.put("work_mode", 9999);
        result.put("power_mode", LauncherApplication.sIsCharge);
        return onSuccess(result, "请求成功");
    }
}
