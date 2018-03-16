package rk.device.server.logic;


import com.alibaba.fastjson.JSONObject;

import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.MD5;
import rk.device.launcher.utils.SPUtils;
import rk.device.server.api.HttpResponseCode;

/**
 * Created by hanbin on 2018/2/5.
 */
public class PublicLogic extends BaseLogic {

    private static PublicLogic publicLogic = null;

    public PublicLogic() {
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
     * 获取token
     *
     * @return
     */
    public JSONObject getToken() {
        long grantTime = System.currentTimeMillis();
        String token = MD5.strToMd5Low32(String.valueOf(grantTime) + (int) ((Math.random() * 9 + 1) * 100000));
        SPUtils.putString(Constant.GRANT_TOKEN, token);
        SPUtils.putLong(Constant.GRANT_TIME, grantTime);
        JSONObject result = new JSONObject();
        result.put("access_token", token);
        result.put("expires_in", 86400);
        return onSuccess(result, "请求成功");
    }


    public JSONObject returnError(String msg) {
        return onError(HttpResponseCode.WUXIAO_LINGPAI, msg);
    }

}
