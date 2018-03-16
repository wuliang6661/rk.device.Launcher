package rk.device.server.logic;

import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.service.DownLoadIntentService;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.utils.verify.OpenUtils;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 设备逻辑
 */
public class DeviceLogic extends BaseLogic {

    private static final String TAG               = "PersonLogic";
    private static DeviceLogic  deviceLogic       = null;

    public DeviceLogic() {
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
    public JSONObject open(org.json.JSONObject params) throws Exception {
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
    public JSONObject status(org.json.JSONObject params) throws Exception {
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
        result.put("uuid", getUUID());
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

    /**
     * 升级接口
     *
     * @param params
     * @return
     */
    public JSONObject update(org.json.JSONObject params) throws Exception {
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
    public JSONObject ad(org.json.JSONObject params) throws Exception {
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
//        List<String> imageList = params.get("image_list");

        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }

    /**
     * 更新一体机时间
     *
     * @param params
     * @return
     */
    public JSONObject updateTime(org.json.JSONObject params) throws Exception {
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(300, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(300, "请填写正确UUID: " + getUUID());
        }
        int time = TypeTranUtils.str2Int(params.getString("update_time"));
        int updateTime = TypeTranUtils.str2Int(params.getString("shouldAutoUpdate"));
        // 设置系统时间
        SystemClock.setCurrentTimeMillis(time);
        Settings.Global.putInt(Utils.getContext().getContentResolver(), Settings.Global.AUTO_TIME, updateTime);
        SPUtils.putBoolean(Constant.UPDATE_TIME, updateTime == 1 ? true : false);
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "设置成功");
    }
}
