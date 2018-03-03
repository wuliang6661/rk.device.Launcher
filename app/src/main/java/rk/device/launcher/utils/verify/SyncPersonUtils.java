package rk.device.launcher.utils.verify;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.cache.CacheUtils;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> users = DbHelper.queryUserByUpdate();
                if (!users.isEmpty()) {
                    updatePerson(users.get(0), null);
                }
            }
        }).start();
    }

    /**
     * 上传人脸图片
     */
    private void uploadImage(User user) {
        File file = new File(CacheUtils.getFaceFile() + "/", user.getFaceID() + ".png");
        if (!file.exists()) {
            return;
        }
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        BaseApiImpl.updataImage(requestBody).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                updatePerson(user, s);
            }
        });
    }

    /**
     * 上传到服务器
     */
    private void updatePerson(User user, String faceImgUrl) {
        BaseApiImpl.addUser(user, faceImgUrl).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object s) {
                user.setStatus(Constant.NORMAL);
                DbHelper.insertUser(user);
            }
        });
    }
}
