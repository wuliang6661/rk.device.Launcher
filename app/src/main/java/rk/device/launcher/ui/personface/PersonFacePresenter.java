package rk.device.launcher.ui.personface;

import org.json.JSONException;
import org.json.JSONObject;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.db.FaceHelper;
import rk.device.launcher.db.entity.Face;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.utils.BitmapUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonFacePresenter extends BasePresenterImpl<PersonFaceContract.View> implements PersonFaceContract.Presenter {

    @Override
    public void addFace(Face face) {
        BaseApiImpl.addFace(faceToJson(face)).subscribe(new Subscriber<Object>() {

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object o) {
                FaceHelper.update(face.getId(), face.getFaceId(), 1, face.getBeginTime(), face.getEndTime());
            }

            @Override
            public void onCompleted() {

            }
        });
    }


    /**
     * 将人脸数据转为json
     */
    private JSONObject faceToJson(Face face) {
        JSONObject object = new JSONObject();
        try {
            object.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
            object.put("uuid", new DeviceUuidFactory(mView.getContext()).getUuid().toString());
            object.put("peopleId", face.getPersonId());
            object.put("faceID", face.getFaceId());
            if (!StringUtils.isEmpty(face.getFaceId())) {
                object.put("faceImage", BitmapUtil.bitmapToString(face.getFaceId()));
                object.put("faceImageEncoding", "1");
                object.put("faceImageFormat", "2");
            }
            object.put("startTime", face.getBeginTime());
            object.put("endTime", face.getEndTime());
            object.put("createTime", face.getCreateTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    @Override
    public void updateFace(Face face) {
        BaseApiImpl.updateFace(faceToJson(face)).subscribe(new Subscriber<Object>() {

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object o) {
                FaceHelper.update(face.getId(), face.getFaceId(), 1, face.getBeginTime(), face.getEndTime());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void deleteFace(Face face) {
        BaseApiImpl.deleteFace(faceToJson(face)).subscribe(new Subscriber<Object>() {

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object o) {
                FaceHelper.delete(face);
            }

            @Override
            public void onCompleted() {

            }
        });
    }
}
