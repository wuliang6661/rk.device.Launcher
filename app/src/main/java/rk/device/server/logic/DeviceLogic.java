package rk.device.server.logic;

import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.ADupdateEvent;
import rk.device.launcher.bean.event.UpdateConfig;
import rk.device.launcher.db.AdHelper;
import rk.device.launcher.db.entity.AD;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.service.DownLoadIntentService;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.utils.verify.OpenUtils;
import rk.device.server.api.HttpResponseCode;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 设备逻辑
 */
public class DeviceLogic extends BaseLogic {

    private static final String TAG = "PersonLogic";

    private DeviceLogic() {
    }

    public static DeviceLogic getInstance() {
        return new DeviceLogic();
    }

    /**
     * 一键开门
     */
    public synchronized JSONObject open(org.json.JSONObject params) throws Exception {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        OpenUtils.getInstance().openDoorJni(VerifyTypeConstant.TYPE_API, "000000", "远程开门");
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }

    /**
     * 获取设备状态
     */
    public synchronized JSONObject status(org.json.JSONObject params) throws Exception {
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
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
     */
    public synchronized JSONObject update(org.json.JSONObject params) throws Exception {
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String target = params.optString("target");
        String version = params.optString("version");
        int code = TypeTranUtils.str2Int(params.optString("version_code"));
        String file = params.optString("url");
        if (StringUtils.isEmpty(file)) {
            return onError(HttpResponseCode.NO_JSON, "缺少升级地址");
        }
        int currentVersion = PackageUtils.getCurrentVersionCode();
        if (code > currentVersion) {
            DownLoadIntentService.startDownLoad(LauncherApplication.getContext(), file, Constant.KEY_ROM);
        } else {
            return onError(HttpResponseCode.NO_CAOZUO, "当前已是最新版本");
        }
        JSONObject result = new JSONObject();
        result.put("code", PackageUtils.getCurrentVersionCode());
        result.put("ver", PackageUtils.getCurrentVersion());
        return onSuccess(result, "请求成功");
    }

    /**
     * 新增广告
     */
    public synchronized JSONObject add_guangao(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String image = params.optString("image");
        String adID = params.optString("adID");
        if (StringUtils.isEmpty(image)) {
            return onError(HttpResponseCode.NO_JSON, "广告地址为空！");
        }
        if (StringUtils.isEmpty(adID)) {
            return onError(HttpResponseCode.NO_JSON, "广告ID为空！");
        }
        List<AD> ads = AdHelper.queryByAdId(adID);
        if (!ads.isEmpty()) {
            return onError(HttpResponseCode.NO_JSON, "广告已存在！");
        }
        AD ad = new AD();
        ad.setAdID(adID);
        ad.setImage(image);
        ad.setCreateTime(TimeUtils.getTimeStamp());
        AdHelper.insert(ad);
        EventBus.getDefault().post(new ADupdateEvent());
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }

    /**
     * 修改广告
     */
    public synchronized JSONObject update_guangao(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String image = params.optString("image");
        String adID = params.optString("adID");
        if (StringUtils.isEmpty(image)) {
            return onError(HttpResponseCode.NO_JSON, "广告地址为空！");
        }
        if (StringUtils.isEmpty(adID)) {
            return onError(HttpResponseCode.NO_JSON, "广告ID为空！");
        }
        List<AD> ads = AdHelper.queryByAdId(adID);
        if (ads.isEmpty()) {
            return onError(HttpResponseCode.NO_JSON, "广告不存在！");
        }
        AD ad = ads.get(0);
        ad.setAdID(adID);
        ad.setImage(image);
        ad.setCreateTime(TimeUtils.getTimeStamp());
        AdHelper.update(ad);
        EventBus.getDefault().post(new ADupdateEvent());
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }


    /**
     * 删除广告
     */
    public synchronized JSONObject delete_guangao(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String image = params.optString("image");
        String adID = params.optString("adID");
        if (StringUtils.isEmpty(image)) {
            return onError(HttpResponseCode.NO_JSON, "广告地址为空！");
        }
        if (StringUtils.isEmpty(adID)) {
            return onError(HttpResponseCode.NO_JSON, "广告ID为空！");
        }
        List<AD> ads = AdHelper.queryByAdId(adID);
        if (ads.isEmpty()) {
            return onError(HttpResponseCode.NO_JSON, "广告不存在！");
        }
        AD ad = ads.get(0);
        AdHelper.delete(ad);
        EventBus.getDefault().post(new ADupdateEvent());
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }


    /**
     * 配置设备配置
     *
     * @param params
     * @return
     */
    public synchronized JSONObject updateTime(org.json.JSONObject params) throws Exception {
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确UUID: " + getUUID());
        }
        int time = TypeTranUtils.str2Int(params.optString("currentTime"));
        int updateTime = TypeTranUtils.str2Int(params.getString("timeUpdate"));
        int heartbeatInterval = TypeTranUtils.str2Int(params.getString("heartbeatInterval"));   //心跳间隔
        String managerIpAddr = params.optString("managerIpAddr");   //物管IP
        String managerPort = params.optString("managerPort");       //物管端口
        if (heartbeatInterval != 0) {
            SPUtils.putInt(Constant.HEART, heartbeatInterval);
            EventBus.getDefault().post(new UpdateConfig());
        }
        try {// 设置系统时间
            if (updateTime == 0) {
                SystemClock.setCurrentTimeMillis(time * 1000L);
            }
            Settings.Global.putInt(Utils.getContext().getContentResolver(), Settings.Global.AUTO_TIME, updateTime);
            SPUtils.putBoolean(Constant.UPDATE_TIME, updateTime == 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!StringUtils.isEmpty(managerIpAddr)) {
            SPUtils.putString(Constant.MANAGER_IP, managerIpAddr);
        }
        if (!StringUtils.isEmpty(managerPort)) {
            SPUtils.putString(Constant.MANAGER_PORT, managerPort);
        }
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "设置成功");
    }
}
