package rk.device.launcher.utils.verify;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.api.T;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by wuliang on 2018/1/16.
 * <p>
 * 同步用户数据到服务器
 */

public class SyncPersonUtils {

    private DeviceUuidFactory factory;


    public static SyncPersonUtils getInstance() {
        return new SyncPersonUtils();
    }

    private SyncPersonUtils() {
        factory = new DeviceUuidFactory(Utils.getContext());
    }


    /**
     * 同步人员到服务器
     */
    public void syncPerosn(User user) {
        List<User> updateUser = DbHelper.queryUserByUpdate();
        if (!updateUser.isEmpty()) {
            for (User mUser : updateUser) {
                if (user.getStatus() == Constant.TO_BE_DELETE) {
                    doDeleteUser(mUser);
                } else if (user.getStatus() == Constant.TO_BE_ADD) {
                    addPerson(mUser);
                } else if (user.getStatus() == Constant.TO_BE_UPDATE) {
                    updatePerson(mUser);
                }
            }
        }
    }

    /**
     * 添加用户
     */
    private synchronized void addPerson(User user) {
        BaseApiImpl.addUser(user).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if ("7".equals(e.getMessage())) {    //用户已存在，下次更新
                    user.setStatus(Constant.TO_BE_UPDATE);
                    DbHelper.insertUser(user);
                }
            }

            @Override
            public void onNext(Object s) {
                user.setStatus(Constant.NORMAL);
                DbHelper.insertUser(user);
            }
        });
    }

    /**
     * 修改用户
     */
    private synchronized void updatePerson(User user) {
        BaseApiImpl.updateUser(user).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if ("6".equals(e.getMessage())) {             //用户不存在，下次添加
                    user.setStatus(Constant.TO_BE_ADD);
                    DbHelper.insertUser(user);
                }
            }

            @Override
            public void onNext(Object s) {
                user.setStatus(Constant.NORMAL);
                DbHelper.insertUser(user);
            }
        });
    }


    /**
     * 删除用户
     */
    private synchronized void doDeleteUser(User user) {
        JSONObject params = new JSONObject();
        try {
            params.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
            params.put("uuid", factory.getUuid());
            params.put("peopleId", user.getUniqueId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BaseApiImpl.deleteUser(params).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if ("6".equals(e.getMessage())) {             //用户不存在，直接删除
                    DbHelper.delete(user);
                    T.showShort("删除成功");
                }
            }

            @Override
            public void onNext(Object Object) {
                DbHelper.delete(user);
                T.showShort("删除成功");
            }
        });
    }
}
