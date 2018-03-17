package rk.device.server.logic;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

import peripherals.FingerHelper;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.db.CardHelper;
import rk.device.launcher.db.CodePasswordHelper;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.FaceHelper;
import rk.device.launcher.db.FingerPrintHelper;
import rk.device.launcher.db.entity.Card;
import rk.device.launcher.db.entity.CodePassword;
import rk.device.launcher.db.entity.Face;
import rk.device.launcher.db.entity.Finger;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.cache.CacheUtils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.server.api.HttpResponseCode;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 用户逻辑
 */
public class PersonLogic extends BaseLogic {

    private static final String TAG = "PersonLogic";
    private static PersonLogic personLogic = null;

    private PersonLogic() {
    }

    public static PersonLogic getInstance() {
        if (personLogic == null) {
            synchronized (PersonLogic.class) {
                if (personLogic == null) {
                    personLogic = new PersonLogic();
                }
            }
        }
        return personLogic;
    }

    /**
     * 添加用户
     */
    public synchronized JSONObject addMember(org.json.JSONObject params) throws Exception {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String popeName = params.optString("peopleName");
        int role = TypeTranUtils.str2Int(params.optString("role"));
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        if (TextUtils.isEmpty(popeName)) {
            return onError(HttpResponseCode.NO_JSON, "请填写用户名");
        }
        if (role == 0) {
            return onError(HttpResponseCode.NO_JSON, "请选择用户权限类型");
        }
        if (TextUtils.isEmpty(startTime)) {
            return onError(HttpResponseCode.NO_JSON, "请填写开始时间");
        }
        if (TextUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.NO_JSON, "请填写结束时间");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (!users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_EXITE, "该用户已存在");
        }
        User user = new User();
        user.setName(popeName);
        user.setRole(role);
        user.setUniqueId(peopleId);
        user.setStartTime(TypeTranUtils.str2Int(startTime));
        user.setEndTime(TypeTranUtils.str2Int(endTime));
        user.setCreateTime(System.currentTimeMillis());
        user.setStatus(1);
        if (DbHelper.insert(user)) {
            JSONObject result = new JSONObject();
            result.put("status", 1);
            result.put("peopleId", user.getUniqueId());
            return onSuccess(result, "请求成功");
        }
        return onError(HttpResponseCode.Error, "数据库操作失败");
    }


    /**
     * 修改用户
     */
    public synchronized JSONObject updatePerson(org.json.JSONObject params) throws Exception {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String popeName = params.optString("peopleName");
        int role = TypeTranUtils.str2Int(params.optString("role"));
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        //修改用户
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_EXITE, "该用户不存在");
        }
        User oldUser = users.get(0);
        User user = new User();
        user.setId(oldUser.getId());
        user.setUniqueId(peopleId);
        user.setName(TextUtils.isEmpty(popeName) ? oldUser.getName() : popeName);
        user.setRole(role == 0 ? oldUser.getRole() : role);
        user.setStartTime(TextUtils.isEmpty(startTime) ? oldUser.getStartTime()
                : TypeTranUtils.str2Int(startTime));
        user.setEndTime(TextUtils.isEmpty(endTime) ? oldUser.getEndTime()
                : TypeTranUtils.str2Int(endTime));
        user.setStatus(1);
        DbHelper.update(user);
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }


    /**
     * 删除用户
     */
    public synchronized JSONObject deletePerson(org.json.JSONObject params) throws Exception {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        //修改用户
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        User oldUser = users.get(0);
        //删除本地人脸
        List<Face> faces = FaceHelper.getList(peopleId);
        if (!faces.isEmpty()) {
            FaceUtils faceUtils = FaceUtils.getInstance();
            for (Face face : faces) {
                faceUtils.delete(face.getFaceId());
                FileUtils.deleteFile(CacheUtils.getFaceFile() + "/" + face.getFaceId() + ".png");
                FaceHelper.delete(face);
            }
        }
        //删除指纹
        List<Finger> fingerList = FingerPrintHelper.getList(peopleId);
        if (!fingerList.isEmpty()) {
            for (int i = 0; i < fingerList.size(); i++) {
                Finger finger = fingerList.get(i);
                FingerPrintHelper.delete(finger);
                FingerHelper.JNIFpDelUserByID(LauncherApplication.fingerModuleID, finger.getNumber());
            }
        }
        //删除密码
        List<CodePassword> codePasswords = CodePasswordHelper.getList(peopleId);
        if (!codePasswords.isEmpty()) {
            for (CodePassword codePassword : codePasswords) {
                CodePasswordHelper.delete(codePassword);
            }
        }
        //删除卡
        List<Card> cards = CardHelper.getList(peopleId);
        if (!cards.isEmpty()) {
            for (Card codePassword : cards) {
                CardHelper.delete(codePassword);
            }
        }
        DbHelper.delete(oldUser);
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }
}
