package rk.device.launcher.utils.verify;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.api.HttpResultCode;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.StatusBo;
import rk.device.launcher.bean.TokenBo;
import rk.device.launcher.bean.event.OpenDoorSuccessEvent;
import rk.device.launcher.db.DbRecordHelper;
import rk.device.launcher.db.entity.Record;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.utils.MD5;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.SoundPlayUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.key.KeyUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by hanbin on 2018/1/13.
 */

public class OpenUtils {

    public static final String TAG               = "OpenUtils";

    DeviceUuidFactory          deviceUuidFactory = new DeviceUuidFactory(
            LauncherApplication.getContext());

    private static OpenUtils   openUtils         = null;
    private static SoundPlayUtils soundPlayUtils         = null;

    public static OpenUtils getInstance() {
        if (openUtils == null) {
            synchronized (OpenUtils.class) {
                if (openUtils == null) {
                    openUtils = new OpenUtils();
                }
            }
        }
        return openUtils;
    }

    public OpenUtils(){
        if(soundPlayUtils == null){
            soundPlayUtils = SoundPlayUtils.init(LauncherApplication.getContext());
        }
    }

    /**
     * 开门方式
     *
     * @param type 1 : nfc,2 : 指纹,3 : 人脸,4 : 密码,5 : 二维码,6 : 远程开门
     * @param personId
     * @param personName
     * @step 1 验证通过之后，调取开门接口（接口1）
     * @result 1.1 token过期，需要重新获取token，并重新请求开门（接口2）
     * @result 1.2 验证通过
     * @step 2 数据库插入开门记录
     * @step 3 开门记录同步到服务端(接口3)
     *       <p/>
     */
    public void open(int type, String personId, String personName) {
        String token = SPUtils.getString(Constant.ACCENT_TOKEN);
        if(TextUtils.isEmpty(token)){
            obtainToken(type,personId,personName);
        }else{
            openDoor(token, type, personId, personName);
        }
    }

    /**
     * 设备开门鉴权token请求接口
     *
     * @param type
     * @param personId
     * @param personName
     */
    private void obtainToken(int type, String personId, String personName) {
        BaseApiImpl.postToken(deviceUuidFactory.getUuid().toString(), KeyUtils.getKey())
                .subscribe(new Subscriber<TokenBo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TokenBo tokenBo) {
                        SPUtils.put(Constant.ACCENT_TOKEN,tokenBo.getAccess_token());
                        openDoor(tokenBo.getAccess_token(), type, personId, personName);
                    }
                });
    }

    /**
     * 开门接口
     *
     * @param token
     * @param type
     */
    private void openDoor(String token, int type, String personId, String personName) {
        int time = TimeUtils.getTimeStamp();
        BaseApiImpl.openDoor(token, deviceUuidFactory.getUuid().toString(), type,
                TimeUtils.getTimeStamp()).subscribe(new Subscriber<StatusBo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().equals(HttpResultCode.TOKEN_INVALID)) {
                            obtainToken(type, personId, personName);
                        }
                    }

                    @Override
                    public void onNext(StatusBo statusBo) {
                        String data = openStatus(type);
                        RxBus.getDefault().post(new OpenDoorSuccessEvent(personName, type, 1));
                        syncRecords(token, type, personId, personName, TimeUtils.getTimeStamp(),
                                data);
                        insertToLocalDB(type, personId, personName, time, data);
                        soundPlayUtils.play(3);
                    }
                });
    }

    /**
     * 获取开门方式对应的文案
     *
     * @param type
     * @return
     */
    private String openStatus(int type) {
        String data;
        switch (type) {
            case VerifyTypeConstant.TYPE_CARD:
                data = "开门方式1 卡：录入卡号";
                break;
            case VerifyTypeConstant.TYPE_FINGER:
                data = "开门方式2 指纹：指纹ID";
                break;
            case VerifyTypeConstant.TYPE_FACE:
                data = "开门方式3 人脸：人脸ID";
                break;
            case VerifyTypeConstant.TYPE_PASSWORD:
                data = "开门方式4 密码：开门密码";
                break;
            case VerifyTypeConstant.TYPE_QR_CODE:
                data = "开门方式5 二维码：二维码开门";
                break;
            case VerifyTypeConstant.TYPE_API:
                data = "开门方式6 远程开门：远程开门";
                break;
            default:
                data = "未知开门方式";
                break;
        }
        return data;

    }

    /**
     * 插入本地数据库
     *
     * @param type
     * @param personId
     * @param personName
     * @param time
     */
    private void insertToLocalDB(int type, String personId, String personName, int time,
                                 String data) {
        Record record = new Record();
        record.setUniqueId(MD5.get16Lowercase(UUID.randomUUID().toString()));
        record.setPopeName(personName);
        record.setPeopleId(personId);
        record.setOpenType(type);
        record.setData(data);
        record.setSlide_data(time);
        record.setCdate(TimeUtils.getTimeStamp());
        int recordId = (int) DbRecordHelper.insert(record);
        if (recordId > 0) {
            Log.i(TAG, TAG + " insert record to local db success.");
        } else {
            Log.i(TAG, TAG + " insert record to local db fail.");
        }
    }

    /**
     * 同步开门记录
     *
     * @param token
     * @param type
     * @param personId
     * @param personName
     * @param time
     * @param data
     */
    private void syncRecords(String token, int type, String personId, String personName, int time,
                             String data) {
        JSONObject params = new JSONObject();
        try {
            params.put("access_token", token);
            params.put("uuid", deviceUuidFactory.getUuid());
            params.put("peopleId", personId);
            params.put("peopleName", personName);
            params.put("openType", type);
            params.put("data", data);
            params.put("cdate", time);
        } catch (JSONException e) {
        }
        BaseApiImpl.syncRecords(params).subscribe(new Subscriber<StatusBo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(StatusBo statusBo) {
                if (statusBo.getStatus() == 1) {
                    Log.i(TAG, TAG + " syncRecords success.");
                } else {
                    Log.i(TAG, TAG + " syncRecords fail.");
                }
            }
        });
    }
}
