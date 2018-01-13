package rk.device.launcher.utils.verify;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.OpenDoorBo;
import rk.device.launcher.bean.TokenBO;
import rk.device.launcher.db.DbRecordHelper;
import rk.device.launcher.db.entity.Record;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.MD5;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by hanbin on 2018/1/13.
 */

public class OpenUtils {

    DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(LauncherApplication.getContext());

    /**
     * 开门方式
     *
     * @param type       1 : nfc,2 : 指纹,3 : 人脸,4 : 密码,5 : 二维码,6 : 远程开门
     * @param personId
     * @param personName
     * @param time       验资时间，比如刷卡，按指纹时间
     * @step 1 验证通过之后，调取开门接口（接口1）
     * @result 1.1 token过期，需要重新获取token，并重新请求开门（接口2）
     * @result 1.2 验证通过
     * @step 2 数据库插入开门记录
     * @step 3 开门记录同步到服务端(接口3)
     * <p/>
     */
    public void open(int type, int personId, String personName, int time) {
        String token = SPUtils.getString(Constant.ACCENT_TOKEN);
        openDoor(token, type, personId, personName, time);
    }

    /**
     * 设备开门鉴权token请求接口
     *
     * @param type
     * @param personId
     * @param personName
     * @param time
     */
    public void obtainToken(int type, int personId, String personName, int time) {
        BaseApiImpl.postToken(deviceUuidFactory.getUuid().toString(), "").subscribe(new Subscriber<TokenBO>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TokenBO tokenBo) {
                openDoor(tokenBo.getAccess_token(), type, personId, personName, time);
            }
        });
    }

    /**
     * 开门接口
     *
     * @param token
     * @param type
     */
    private void openDoor(String token, int type, int personId, String personName, int time) {
        JSONObject params = new JSONObject();
        try {
            params.put("access_token", token);
            params.put("uuid", deviceUuidFactory.getUuid());
            params.put("openType", type);
            params.put("time", TimeUtils.getTimeStamp());
        } catch (JSONException e) {

        }
        BaseApiImpl.openDoor(params).subscribe(new Subscriber<OpenDoorBo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(OpenDoorBo openDoorBo) {
                if (openDoorBo.getStatus() == 1) {
                    //开门成功
                    //Record( int slide_data,int cdate)
                    String data;
                    switch (type) {
                        case 1:
                            data = "开门方式1 卡：录入卡号";
                            break;
                        case 2:
                            data = "开门方式2 指纹：指纹ID";
                            break;
                        case 3:
                            data = "开门方式3 人脸：人脸ID";
                            break;
                        case 4:
                            data = "开门方式4 密码：开门密码";
                            break;
                        case 5:
                            data = "开门方式5 二维码：二维码开门";
                            break;
                        case 6:
                            data = "开门方式6 远程开门：远程开门";
                            break;
                        default:
                            data = "未知开门方式";
                            break;
                    }
                    Record record = new Record(null, MD5.get16Lowercase(UUID.randomUUID().toString()), personName, String.valueOf(personId), type, data, time, TimeUtils.getTimeStamp());
                    int recordId = (int) DbRecordHelper.insert(record);
                    if (recordId > 0) {

                    }
                } else {
                    //开门失败，此处是token失效还是因为什么，接口文档看不出来？

                }
            }
        });
    }
}
