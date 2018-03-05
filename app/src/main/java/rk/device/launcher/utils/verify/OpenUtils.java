package rk.device.launcher.utils.verify;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import peripherals.RelayHelper;
import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.OpenDoorSuccessEvent;
import rk.device.launcher.db.CardHelper;
import rk.device.launcher.db.CodePasswordHelper;
import rk.device.launcher.db.DbRecordHelper;
import rk.device.launcher.db.FaceHelper;
import rk.device.launcher.db.entity.Card;
import rk.device.launcher.db.entity.CodePassword;
import rk.device.launcher.db.entity.Face;
import rk.device.launcher.db.entity.Record;
import rk.device.launcher.global.Constant;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.utils.MD5;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.SoundPlayUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by hanbin on 2018/1/13.
 */

public class OpenUtils {

    public static final String    TAG               = "OpenUtils";

    DeviceUuidFactory             deviceUuidFactory = new DeviceUuidFactory(
            LauncherApplication.getContext());

    private static OpenUtils      openUtils         = null;
    private static SoundPlayUtils soundPlayUtils    = null;
    private int                   fingerId          = -1;

    private boolean               openDoorSuress    = false;                //默认门没开

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

    public OpenUtils() {
        if (soundPlayUtils == null) {
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
    public synchronized void open(int type, String personId, String personName) {
        if (!isOpen()) {
            return;
        }
        openDoorJni(type, personId, personName);
    }

    public synchronized void open(int type, String personId, String personName, int fingerId) {
        if (!isOpen()) {
            return;
        }
        openDoorJni(type, personId, personName);
        this.fingerId = fingerId;
    }

    private long justTime = 0;

    /**
     * 判断上一次开门时间，2秒内不能多次开门
     */
    private boolean isOpen() {
        if (justTime == 0) {
            return true;
        }
        if (!openDoorSuress) {
            return true;
        }
        long time = System.currentTimeMillis();
        return (time - justTime) > 2000;
    }

    /**
     * 开门接口
     *
     * @param token
     * @param type
     */
    private void openDoor(String token, int type, String personId, String personName) {
        int time = TimeUtils.getTimeStamp();
        justTime = System.currentTimeMillis();
        String data = openStatus(type, personId);
        RxBus.getDefault().post(new OpenDoorSuccessEvent(personName, type, 1));
        syncRecords(token, type, personId, personName, TimeUtils.getTimeStamp(), data);
        insertToLocalDB(type, personId, personName, time, data);
        if (SPUtils.getBoolean(Constant.DEVICE_MP3, true)) {
            soundPlayUtils.play(3);
        }
    }

    /**
     * 获取开门方式对应的文案
     *
     * @param type 类型
     * @param type
     * @return
     */
    private String openStatus(int type, String personId) {
        String data;
        switch (type) {
            case VerifyTypeConstant.TYPE_CARD://卡
                List<Card> cardList = CardHelper.getList(personId);
                data = cardList.size() > 0 ? cardList.get(0).getNumber() : "";
                break;
            case VerifyTypeConstant.TYPE_FINGER://指纹
                data = String.valueOf(fingerId);
                break;
            case VerifyTypeConstant.TYPE_FACE://人脸
                List<Face> faceList = FaceHelper.getList(personId);
                data = faceList.size() > 0 ? faceList.get(0).getFaceId() : "";
                break;
            case VerifyTypeConstant.TYPE_PASSWORD://密码
                List<CodePassword> codePasswordList = CodePasswordHelper.getList(personId);
                data = codePasswordList.size() > 0 ? codePasswordList.get(0).getPassword() : "";
                break;
            case VerifyTypeConstant.TYPE_QR_CODE://二维码
                data = "";
                break;
            case VerifyTypeConstant.TYPE_API://远程开门
                data = "";
                break;
            default://其他
                data = "";
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
        long recordId = DbRecordHelper.insert(record);
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
        BaseApiImpl.syncRecords(params).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object statusBo) {
                Log.i(TAG, TAG + " syncRecords success.");
            }
        });
    }

    /**
     * 使用本地jni方法开门
     */
    public synchronized void openDoorJni(int type, String personId, String personName) {
//        AppManager.getAppManager().goBackMain();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int relayOff;
                if (SPUtils.getBoolean(Constant.DEVICE_OFF, true)) {
                    relayOff = RelayHelper.RelaySetOff();
                } else {
                    relayOff = RelayHelper.RelaySetOn();
                }
                if (relayOff == 0) {
                    try {
                        String time = SPUtils.getString(Constant.DEVICE_TIME);
                        long sleepTime;
                        if (StringUtils.isEmpty(time)) {
                            sleepTime = (long) (0.5 * 1000);
                        } else {
                            sleepTime = (long) (Double.parseDouble(time) * 1000);
                        }
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int relayOn;
                    if (SPUtils.getBoolean(Constant.DEVICE_OFF, true)) {
                        relayOn = RelayHelper.RelaySetOn();
                    } else {
                        relayOn = RelayHelper.RelaySetOff();
                    }
                    if (relayOn == 0) {
                        openDoorSuress = true;
                        openDoor(SPUtils.getString(Constant.ACCENT_TOKEN),type,personId,personName);
                    } else {
                        openDoorSuress = false;
                    }
                }
            }
        }).start();
    }

}
