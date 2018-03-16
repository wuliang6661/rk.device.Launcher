package rk.device.server.logic;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.List;

import peripherals.FingerConstant;
import peripherals.FingerHelper;
import rk.device.launcher.R;
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
import rk.device.launcher.utils.BitmapUtil;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.utils.verify.VerifyUtils;
import rk.device.server.api.HttpResponseCode;

import static com.android.internal.app.IntentForwarderActivity.TAG;
import static rk.device.launcher.utils.ResUtil.getResources;

/**
 * Created by wuliang on 2018/3/15.
 * <p>
 * 各个凭证的增删改查
 */

public class VoucherLogic extends BaseLogic {


    private VoucherLogic() {
    }

    public static VoucherLogic getInstance() {
        return new VoucherLogic();
    }


    /**
     * 新增人脸
     */
    public synchronized JSONObject addPersonFace(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String faceImage = params.optString("faceImage");
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        if (TextUtils.isEmpty(faceImage)) {
            return onError(HttpResponseCode.NO_JSON, "人脸图片呢？没有你瞎请求个毛！");
        }
        if (TextUtils.isEmpty(startTime)) {
            return onError(HttpResponseCode.NO_JSON, "请填写开始时间？没有你瞎请求个毛！");
        }
        if (TextUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.NO_JSON, "请填写结束时间？没有你瞎请求个毛！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<Face> oldFace = FaceHelper.getList(peopleId);
        if (!oldFace.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_EXITE, "该用户已存在人脸");
        }
        Bitmap bitmap = BitmapUtil.stringtoBitmap(faceImage);
        AFR_FSDKFace fsdkFace = FaceUtils.getInstance().bitmapToFace(bitmap);
        if (fsdkFace == null) {
            return onError(HttpResponseCode.NO_JSON, "没有检测到人脸");
        }
        String faceId = FaceUtils.getInstance().saveFace(fsdkFace);
        if (TextUtils.isEmpty(faceId)) {
            return onError(HttpResponseCode.Error, "同步人脸失败");
        }
        BitmapUtil.saveBitmap(faceId + ".png", bitmap);
        FaceHelper.insert(peopleId, faceId, 1, TypeTranUtils.str2Int(startTime), TypeTranUtils.str2Int(endTime));
        JSONObject result = new JSONObject();
        result.put("faceID", faceId);
        return onSuccess(result, "人脸同步成功");
    }


    /**
     * 修改人脸
     */
    public synchronized JSONObject updatePersonFace(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String faceImage = params.optString("faceImage");
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        String faceIDNum = params.optString("faceID");
        if (TextUtils.isEmpty(faceImage)) {
            return onError(HttpResponseCode.NO_JSON, "人脸图片呢？没有你瞎请求个毛！");
        }
        if (TextUtils.isEmpty(startTime)) {
            return onError(HttpResponseCode.NO_JSON, "请填写开始时间？没有你瞎请求个毛！");
        }
        if (TextUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.NO_JSON, "请填写结束时间？没有你瞎请求个毛！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<Face> oldFace = FaceHelper.getList(peopleId);
        if (oldFace.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_EXITE, "该用户的人脸已存在！");
        }
        Bitmap bitmap = BitmapUtil.stringtoBitmap(faceImage);
        AFR_FSDKFace fsdkFace = FaceUtils.getInstance().bitmapToFace(bitmap);
        if (fsdkFace == null) {
            return onError(HttpResponseCode.NO_JSON, "没有检测到人脸");
        }
        String faceId = FaceUtils.getInstance().updateFace(fsdkFace, faceIDNum);
        if (TextUtils.isEmpty(faceId)) {
            return onError(HttpResponseCode.Error, "同步人脸失败");
        }
        BitmapUtil.saveBitmap(faceId + ".png", bitmap);
        FaceHelper.update(oldFace.get(0).getId(), faceId, 1, TypeTranUtils.str2Int(startTime), TypeTranUtils.str2Int(endTime));
        JSONObject result = new JSONObject();
        result.put("faceID", faceId);
        return onSuccess(result, "人脸修改成功");
    }


    /**
     * 删除人脸
     */
    public synchronized JSONObject deleteFaceImg(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String faceID = params.optString("faceID");
        if (StringUtils.isEmpty(peopleId) || StringUtils.isEmpty(faceID)) {
            return onError(HttpResponseCode.NO_JSON, "信息不完整！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<Face> oldFace = FaceHelper.getList(peopleId);
        if (oldFace.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户没有该人脸");
        }
        Face face = oldFace.get(0);
        if (!face.getFaceId().equals(faceID)) {
            return onError(HttpResponseCode.Error, "faceId与本地不一致！");
        }
        FaceHelper.delete(face);
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }


    /**
     * 新增密码
     */
    public synchronized JSONObject addPassWord(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String password = params.optString("password");
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        if (StringUtils.isEmpty(peopleId) || StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.NO_JSON, "信息不完整！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<CodePassword> codePasswords = CodePasswordHelper.getList(peopleId);
        if (!codePasswords.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_EXITE, "该用户已存在密码");
        }
        CodePasswordHelper.insert(peopleId, password, 1, TypeTranUtils.str2Int(startTime), TypeTranUtils.str2Int(endTime));
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "密码增加成功");
    }

    /**
     * 修改密码
     */
    public synchronized JSONObject updatePassWord(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String password = params.optString("password");
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        if (StringUtils.isEmpty(peopleId) || StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.NO_JSON, "信息不完整！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<CodePassword> codePasswords = CodePasswordHelper.getList(peopleId);
        if (codePasswords.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户没有设置过密码！");
        }
        CodePasswordHelper.update(codePasswords.get(0).getId(), password, 1, TypeTranUtils.str2Int(startTime), TypeTranUtils.str2Int(endTime));
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "密码修改成功");
    }


    /**
     * 删除密码
     */
    public synchronized JSONObject deletePassWord(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String password = params.optString("password");
        if (StringUtils.isEmpty(peopleId) || StringUtils.isEmpty(password)) {
            return onError(HttpResponseCode.NO_JSON, "信息不完整！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<CodePassword> codePasswords = CodePasswordHelper.getList(peopleId);
        if (codePasswords.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户没有设置过密码！");
        }
        if (!codePasswords.get(0).getPassword().equals(password)) {
            return onError(HttpResponseCode.NO_JSON, "password与本地不一致！");
        }
        CodePasswordHelper.delete(codePasswords.get(0));
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "密码删除成功");
    }


    /**
     * 新增卡
     */
    public synchronized JSONObject addCard(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String cardNo = params.optString("cardNo");
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        if (StringUtils.isEmpty(peopleId) || StringUtils.isEmpty(cardNo) ||
                StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.NO_JSON, "信息不完整！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<Card> cards = CardHelper.getList(peopleId);
        if (!cards.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_EXITE, "该用户已录入卡");
        }
        CardHelper.insert(peopleId, cardNo, 1, TypeTranUtils.str2Int(startTime), TypeTranUtils.str2Int(endTime));
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "卡增加成功");
    }


    /**
     * 修改卡
     */
    public synchronized JSONObject updateCards(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String cardNo = params.optString("cardNo");
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        if (StringUtils.isEmpty(peopleId) || StringUtils.isEmpty(cardNo) ||
                StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.NO_JSON, "信息不完整！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<Card> cards = CardHelper.getList(peopleId);
        if (cards.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户没有录入过卡！");
        }
        CardHelper.update(cards.get(0).getId(), cardNo, 1, TypeTranUtils.str2Int(startTime), TypeTranUtils.str2Int(endTime));
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "卡修改成功");
    }


    /**
     * 删除卡
     */
    public synchronized JSONObject deleteCards(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String cardNo = params.optString("cardNo");
        if (StringUtils.isEmpty(peopleId) || StringUtils.isEmpty(cardNo)) {
            return onError(HttpResponseCode.NO_JSON, "信息不完整！");
        }
        List<User> users = DbHelper.queryByUniqueId(peopleId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        List<Card> cards = CardHelper.getList(peopleId);
        if (cards.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户没有设置过密码！");
        }
        if (!cards.get(0).getNumber().equals(cardNo)) {
            return onError(HttpResponseCode.NO_JSON, "password与本地不一致！");
        }
        CardHelper.delete(cards.get(0));
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "卡删除成功");
    }

    /**
     * 删除指纹
     */
    public JSONObject deleteFinger(org.json.JSONObject params) {
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            return onError(HttpResponseCode.NO_JSON, "请填写正确的UUID: " + getUUID());
        }
        String uniqueId = params.optString("peopleId");
        if (TextUtils.isEmpty(uniqueId)) {
            return onError(HttpResponseCode.NO_JSON, "用户唯一标志不能为空");
        }
        List<User> users = DbHelper.queryByUniqueId(uniqueId);
        if (users.isEmpty()) {
            return onError(HttpResponseCode.OBJECT_NO_FOUND, "该用户不存在");
        }
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        int uId = 0;
        int number = params.optInt("number");
        if (number <= 0 || number > 3) {
            return onError(HttpResponseCode.NO_JSON, "请指定要删除的指纹");
        }
        if (eUser != null) {
            if (doDeleteJniFinger(uId)) {
                Finger finger = FingerPrintHelper.queryOne(eUser.getUniqueId(), number);
                FingerPrintHelper.delete(finger);
                JSONObject result = new JSONObject();
                result.put("status", 1);
                return onSuccess(result, getResources().getString(R.string.delete_success));
            } else {
                return onError(HttpResponseCode.Error, getResources().getString(R.string.delete_fail));
            }
        } else {
            return onError(HttpResponseCode.Error, getResources().getString(R.string.illeagel_user_not_exist));
        }
    }

    /**
     * 删除指纹头中的指纹
     */
    private boolean doDeleteJniFinger(int uId) {
        int resultCode = FingerHelper.JNIFpDelUserByID(LauncherApplication.fingerModuleID, uId);
        if (resultCode == FingerConstant.SUCCESS) {
            LogUtil.i(TAG, TAG + "doDeleteJniFinger success");
            return true;
        } else {
            LogUtil.i(TAG, TAG + "doDeleteJniFinger failed");
            return false;
        }
    }
}