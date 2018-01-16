package rk.device.launcher.utils.verify;

import java.util.List;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by wuliang on 2018/1/16.
 * <p>
 * 同步用户数据到服务器
 */

public class SyncPersonUtils {

    private static SyncPersonUtils syncPersonUtils;


    public static SyncPersonUtils getInstance() {
        if (syncPersonUtils == null) {
            synchronized (SyncPersonUtils.class) {
                if (syncPersonUtils == null) {
                    syncPersonUtils = new SyncPersonUtils();
                }
            }
        }
        return syncPersonUtils;
    }


    /**
     * 同步人员到服务器
     */
    public void syncPerosn() {
        List<User> users = DbHelper.queryUserByUpdate();
        if (!users.isEmpty()) {
            updatePerson(users.get(0));
        }
    }


    /**
     * 上传到服务器
     */
    private void updatePerson(User user) {
        BaseApiImpl.syncPersons(user).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                user.setUploadStatus(1);
                DbHelper.insertUser(user);
                syncPerosn();
            }
        });
    }
}
