package rk.device.server.logic;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.koushikdutta.async.http.Multimap;

import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.verify.VerifyUtils;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 用户逻辑
 */
public class MemberLogic extends BaseLogic {

    private static final String TAG = "MemberLogic";
    private static MemberLogic memberLogic = null;

    public MemberLogic() {

    }

    public static MemberLogic getInstance() {
        if (memberLogic == null) {
            synchronized (MemberLogic.class) {
                if (memberLogic == null) {
                    memberLogic = new MemberLogic();
                }
            }
        }
        return memberLogic;
    }

    /**
     * 添加用户
     *
     * @param params
     * @return
     */
    public JSONObject addMember(Multimap params) {
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(300, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(300, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.getString("peopleId");
        String popeName = params.getString("popeName");
        String popedomType = params.getString("popedomType");
        String startTime = params.getString("startTime");
        String endTime = params.getString("endTime");
        String cardNo = params.getString("cardNo");
        String faceID = params.getString("faceID");
        String password = params.getString("password");
        if (TextUtils.isEmpty(peopleId)) {//新增用户
            if (TextUtils.isEmpty(popeName)) {
                return onError(300, "请填写用户名");
            }
            if (TextUtils.isEmpty(popedomType)) {
                return onError(300, "请选择用户权限类型");
            }
            if (TextUtils.isEmpty(startTime)) {
                return onError(300, "请填写开始时间");
            }
            if (TextUtils.isEmpty(endTime)) {
                return onError(300, "请填写结束时间");
            }
            User user = new User();
            user.setName(popeName);
            user.setPopedomType(popedomType);
            user.setStartTime(TypeTranUtils.str2Int(startTime));
            user.setEndTime(TypeTranUtils.str2Int(endTime));
            user.setCardNo(cardNo);
            user.setFaceID(faceID);
            user.setPassWord(TypeTranUtils.str2Int(password));
            long userId = DbHelper.insertUser(user);
            String uniqueId = DbHelper.queryUniqueIdByUserId(userId);
            JSONObject result = new JSONObject();
            result.put("status", 1);
            result.put("peopleId", uniqueId);
            return onSuccess(result, "请求成功");
        } else {
            //修改用户
            User oldUser = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
            if (oldUser == null) {
                return onError(300, "该用户不存在");
            }
            User user = new User();
            user.setId(oldUser.getId());
            user.setUniqueId(peopleId);
            user.setName(TextUtils.isEmpty(popeName) ? oldUser.getName() : popeName);
            user.setPopedomType(
                    TextUtils.isEmpty(popedomType) ? oldUser.getPopedomType() : popedomType);
            user.setStartTime(TextUtils.isEmpty(startTime) ? oldUser.getStartTime()
                    : TypeTranUtils.str2Int(startTime));
            user.setEndTime(TextUtils.isEmpty(endTime) ? oldUser.getEndTime()
                    : TypeTranUtils.str2Int(endTime));
            user.setCardNo(TextUtils.isEmpty(cardNo) ? oldUser.getCardNo() : cardNo);
            user.setFaceID(TextUtils.isEmpty(faceID) ? oldUser.getFaceID() : faceID);
            user.setPassWord(TextUtils.isEmpty(password) ? oldUser.getPassWord()
                    : TypeTranUtils.str2Int(password));
            DbHelper.insertUser(user);
            JSONObject result = new JSONObject();
            result.put("status", 1);
            return onSuccess(result, "请求成功");
        }
    }

    /**
     * 删除（用户，卡，密码）
     *
     * @param params
     * @return
     */
    public JSONObject delete(Multimap params) {
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(300, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(300, "请填写正确的UUID: " + getUUID());
        }
        if (TextUtils.isEmpty(params.getString("type"))) {
            return onError(300, "请指定业务类型");
        }
        String peopleId = params.getString("peopleId");
        if (TextUtils.isEmpty(peopleId)) {
            return onError(300, "用户ID不能为空");
        }
        int type = TypeTranUtils.str2Int(params.getString("type"));
        switch (type) {
            case 1://用户
                User user = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
                DbHelper.delete(user);
                break;
            case 2://卡
                user = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
                user.setCardNo("");
                DbHelper.update(user);
                break;
            case 3://密码
                user = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
                user.setPassWord(0);
                DbHelper.update(user);
                break;
            default:
                return onError(300, "错误的业务类型");
        }
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }

    /**
     * 上传人脸图片
     *
     * @param params
     * @return
     */
    public JSONObject upload(Multimap params) {
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }
}
