package rk.device.server.logic;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.utils.BitmapUtil;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.utils.verify.VerifyUtils;
import rk.device.server.api.HttpResponseCode;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 用户逻辑
 */
public class PersonLogic extends BaseLogic {

    private static final String TAG = "PersonLogic";
    private static PersonLogic personLogic = null;

    public PersonLogic() {

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
     *
     * @param params
     * @return
     */
    public synchronized JSONObject addMember(org.json.JSONObject params) throws Exception {
        String accessToken = params.optString("access_token");
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.Error, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.Error, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String popeName = params.optString("peopleName");
        int role = TypeTranUtils.str2Int(params.optString("role"));
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        if (TextUtils.isEmpty(popeName)) {
            return onError(HttpResponseCode.Error, "请填写用户名");
        }
        if (role == 0) {
            return onError(HttpResponseCode.Error, "请选择用户权限类型");
        }
        if (TextUtils.isEmpty(startTime)) {
            return onError(HttpResponseCode.Error, "请填写开始时间");
        }
        if (TextUtils.isEmpty(endTime)) {
            return onError(HttpResponseCode.Error, "请填写结束时间");
        }
        User oldUser = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
        if (oldUser != null) {
            return onError(HttpResponseCode.Error, "该用户已存在");
        }
        User user = new User();
        user.setName(popeName);
        user.setRole(role);
        user.setUniqueId(peopleId);
        user.setStartTime(TypeTranUtils.str2Int(startTime));
        user.setEndTime(TypeTranUtils.str2Int(endTime));
        user.setCreateTime(System.currentTimeMillis());
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
        String accessToken = params.optString("access_token");
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.Error, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.Error, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        String popeName = params.optString("popeName");
        int role = TypeTranUtils.str2Int(params.optString("role"));
        String startTime = params.optString("startTime");
        String endTime = params.optString("endTime");
        //修改用户
        User oldUser = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
        if (oldUser == null) {
            return onError(HttpResponseCode.Error, "该用户不存在");
        }
        User user = new User();
        user.setId(oldUser.getId());
        user.setUniqueId(peopleId);
        user.setName(TextUtils.isEmpty(popeName) ? oldUser.getName() : popeName);
        user.setRole(role == 0 ? oldUser.getRole() : role);
        user.setStartTime(TextUtils.isEmpty(startTime) ? oldUser.getStartTime()
                : TypeTranUtils.str2Int(startTime));
        user.setEndTime(TextUtils.isEmpty(endTime) ? oldUser.getEndTime()
                : TypeTranUtils.str2Int(endTime));
        DbHelper.update(user);
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }


    /**
     * 删除用户
     */
    public synchronized JSONObject deletePerson(org.json.JSONObject params) {
        String accessToken = params.optString("access_token");
        String uuid = params.optString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.Error, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.Error, "请填写正确的UUID: " + getUUID());
        }
        String peopleId = params.optString("peopleId");
        //修改用户
        User oldUser = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
        if (oldUser == null) {
            return onError(HttpResponseCode.Error, "该用户不存在");
        }
        DbHelper.delete(oldUser);
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }


    /**
     * 删除（用户，卡，密码）
     *
     * @param params
     * @return
     */
    public JSONObject delete(org.json.JSONObject params) throws Exception {
        String accessToken = params.getString("access_token");
        String uuid = params.getString("uuid");
        if (TextUtils.isEmpty(uuid)) {
            return onError(HttpResponseCode.Error, "UUID不能为空");
        }
        if (!getUUID().equals(uuid)) {
            LogUtil.i(TAG, getUUID());
            return onError(HttpResponseCode.Error, "请填写正确的UUID: " + getUUID());
        }
        if (TextUtils.isEmpty(params.getString("type"))) {
            return onError(HttpResponseCode.Error, "请指定业务类型");
        }
        String peopleId = params.getString("peopleId");
        if (TextUtils.isEmpty(peopleId)) {
            return onError(HttpResponseCode.Error, "用户ID不能为空");
        }
        int type = TypeTranUtils.str2Int(params.getString("type"));
        User user = VerifyUtils.getInstance().queryUserByUniqueId(peopleId);
        if (user == null) {
            return onError(HttpResponseCode.Error, "该用户不存在");
        }
        switch (type) {
            case 1://用户
                DbHelper.delete(user);
                break;
            case 2://卡

                break;
            case 3://密码
                user.setPassWord(0);
                DbHelper.update(user);
                break;
            case 4://人脸
                if (TextUtils.isEmpty(user.getFaceID())) {
                    return onError(HttpResponseCode.Error, "用户未绑定人脸");
                }
                if (!FaceUtils.getInstance().delete(user.getFaceID())) {
                    return onError(HttpResponseCode.Error, "人脸删除失败");
                }
                user.setFaceID("");
                DbHelper.update(user);
                break;
            default:
                return onError(HttpResponseCode.Error, "错误的业务类型");
        }
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "请求成功");
    }

    /**
     * 文件读写流
     */
    private BufferedOutputStream fileOutPutStream = null;
    private BufferedInputStream fileInputStream = null;
    private File file = null;
    private String filePath = "";

    /**
     * 上传人脸图片
     *
     * @param body
     * @return
     */
    public void upload(MultipartFormDataBody body, AsyncHttpServerResponse response) {
        filePath = "/data/data/rk.device.launcher/files/temp";
        /**
         * 参数接收回调
         */
        body.setMultipartCallback(new MultipartFormDataBody.MultipartCallback() {
            @Override
            public void onPart(Part part) {
                LogUtil.i(TAG, "file type:" + part.getContentType());
                if (part.isFile()) {
                    if (file == null || !file.exists()) {
                        String suffix = "";
                        switch (part.getContentType()) {
                            case "image/jpeg":
                                suffix = ".jpg";
                                break;
                            case "image/png":
                                suffix = ".png";
                                break;
                            default:
                                response.send(
                                        onError(HttpResponseCode.Error, "文件格式有误").toJSONString());
                                break;
                        }
                        filePath = filePath + System.currentTimeMillis() + suffix;
                        file = new File(filePath);
                    }
                    try {
                        fileOutPutStream = new BufferedOutputStream(new FileOutputStream(file));
                    } catch (FileNotFoundException e) {
                    }
                    body.setDataCallback(new DataCallback() {
                        @Override
                        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                            writeData(bb);
                        }
                    });
                } else {
                    response.send(onError(HttpResponseCode.Error, "文件格式有误").toJSONString());
                }
            }
        });
        /**
         * 参数接收结束回调
         */
        body.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                try {
                    fileInputStream.close();
                    fileOutPutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapUtil.decodeBitmapFromFile(filePath, 640, 480);
                AFR_FSDKFace fsdkFace = FaceUtils.getInstance().bitmapToFace(bitmap);
                if (fsdkFace == null) {
                    FileUtils.deleteFile(new File(filePath));
                    response.send(onError(HttpResponseCode.Error, "没有检测到人脸").toJSONString());
                }
                String faceId = FaceUtils.getInstance().saveFace(fsdkFace);
                if (TextUtils.isEmpty(faceId)) {
                    FileUtils.deleteFile(new File(filePath));
                    response.send(onError(HttpResponseCode.Error, "同步人脸失败").toJSONString());
                }
                BitmapUtil.saveBitmap(faceId + ".png", bitmap);
                Log.i(TAG, TAG + "filePath:" + filePath);
                FileUtils.deleteFile(new File(filePath));
                JSONObject result = new JSONObject();
                result.put("faceId", faceId);
                response.send(onSuccess(result, "人脸同步成功").toJSONString());
            }
        });
    }

    /**
     * 写入文件
     *
     * @param bb
     */
    private void writeData(ByteBufferList bb) {
        byte[] buff = new byte[1024];
        try {
            fileInputStream = new BufferedInputStream(
                    new ByteArrayInputStream(bb.getAllByteArray()));

            int bytesRead = 0;
            while (-1 != (bytesRead = fileInputStream.read(buff, 0, buff.length))) {
                fileOutPutStream.write(buff, 0, bytesRead);
            }
            fileOutPutStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 删除人脸
     *
     * @param params
     * @return
     */
    public JSONObject deleteFace(org.json.JSONObject params) throws Exception {
        String faceId = params.getString("faceId");
        if (TextUtils.isEmpty(faceId)) {
            return onError(HttpResponseCode.Error, "faceId不能为空");
        }
        if (!FaceUtils.getInstance().delete(faceId)) {
            return onError(HttpResponseCode.Error, "删除失败");
        }
        JSONObject result = new JSONObject();
        result.put("status", 1);
        return onSuccess(result, "删除成功");
    }
}
