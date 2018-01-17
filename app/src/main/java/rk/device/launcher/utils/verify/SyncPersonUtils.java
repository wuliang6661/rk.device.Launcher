package rk.device.launcher.utils.verify;

import java.util.List;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.bean.TokenBo;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.key.KeyUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by wuliang on 2018/1/16.
 * <p>
 * 同步用户数据到服务器
 */

public class SyncPersonUtils {

    private static SyncPersonUtils syncPersonUtils;

    private DeviceUuidFactory factory = new DeviceUuidFactory(Utils.getContext());

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
        if (StringUtils.isEmpty(SPUtils.getString(Constant.ACCENT_TOKEN))) {
            syncToken();
        } else {
            List<User> users = DbHelper.queryUserByUpdate();
            if (!users.isEmpty()) {
                updatePerson(users.get(0));
            }
        }
    }


    /**
     * 获取token
     */
    public void syncToken() {
        BaseApiImpl.postToken(factory.getUuid().toString(), KeyUtils.getKey())
                .subscribe(new Subscriber<TokenBo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TokenBo tokenBo) {
                        SPUtils.put(Constant.ACCENT_TOKEN, tokenBo.getAccess_token());
                        syncPerosn();
                    }
                });
    }


    /**
     * 上传到服务器
     */
    private void updatePerson(User user) {
        BaseApiImpl.syncPersons(user).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("40004")) {     //token失效
                    syncToken();
                }
            }

            @Override
            public void onNext(Object s) {
                user.setUploadStatus(1);
                DbHelper.insertUser(user);
                syncPerosn();
            }
        });
    }
}
