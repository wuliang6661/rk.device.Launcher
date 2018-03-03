package rk.device.launcher.ui.person_add;

import org.json.JSONException;
import org.json.JSONObject;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.db.CodePasswordHelper;
import rk.device.launcher.db.entity.CodePassword;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class Person_addPresenter extends BasePresenterImpl<Person_addContract.View> implements Person_addContract.Presenter {

    @Override
    public void addPassWord(CodePassword codePassword) {
        BaseApiImpl.addPassWord(passwordToJson(codePassword)).subscribe(new Subscriber<Object>() {
            @Override
            public void onNext(Object o) {
                CodePasswordHelper.update(codePassword.getId(), codePassword.getPassword(), 1, codePassword.getBeginTime(), codePassword.getEndTime());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void editPassWord(CodePassword codePassword) {
        BaseApiImpl.updatePassWord(passwordToJson(codePassword)).subscribe(new Subscriber<Object>() {
            @Override
            public void onNext(Object o) {
                CodePasswordHelper.update(codePassword.getId(), codePassword.getPassword(), 1, codePassword.getBeginTime(), codePassword.getEndTime());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void deletePassWord(CodePassword codePassword) {
        BaseApiImpl.deletePassWord(passwordToJson(codePassword)).subscribe(new Subscriber<Object>() {
            @Override
            public void onNext(Object o) {
                CodePasswordHelper.delete(codePassword);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }


    /**
     * 将密码数据转为json
     */
    private JSONObject passwordToJson(CodePassword password) {
        JSONObject object = new JSONObject();
        try {
            object.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
            object.put("uuid", new DeviceUuidFactory(mView.getContext()).getUuid().toString());
            object.put("peopleId", password.getPersonId());
            object.put("password", password.getPassword());
            object.put("startTime", password.getBeginTime());
            object.put("endTime", password.getEndTime());
            object.put("createTime", password.getCreateTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

}
