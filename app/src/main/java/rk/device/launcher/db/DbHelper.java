package rk.device.launcher.db;

import android.text.TextUtils;

import org.greenrobot.greendao.query.Query;

import java.io.File;
import java.util.List;
import java.util.UUID;

import rk.device.launcher.db.entity.User;
import rk.device.launcher.db.entity.UserDao;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.MD5;

/**
 * @author : mundane
 * @time : 2017/4/14 14:06
 * @description :
 * @file : DbHelper.java
 */

public class DbHelper {
    private static final String DB_NAME = "/data/rk_backup/rk.db";
    private static final String DB_PATH_JOUR = "/data/rk_backup/rk.db-journal";

    private static UserDao sUserDao;
    private static final String TAG = "DbHelper";

    public static UserDao getUserDao() {
        if (sUserDao == null) {
            sUserDao = DbManager.getInstance().getUserDao();
        }
        File dbFile = new File(DB_NAME);
        LogUtil.d(TAG, "dbFile.exists() = " + dbFile.exists());
        FileUtils.setPermission(DB_NAME);
        FileUtils.setPermission(DB_PATH_JOUR);
        return sUserDao;
    }

    public static void delete(User user) {
        getUserDao().delete(user);
    }

    public static void deleteAll() {
        getUserDao().deleteAll();
    }

    /**
     * 此处需要返回insert之后的反馈，不然无法知道是否insert成功
     *
     * @param user
     * @return
     */
    public static boolean insert(User user) {
        try {
            long rowId = getUserDao().insert(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void insertInTx(User... users) {
        getUserDao().insertInTx(users);
    }

    public static void insertInTx(Iterable<User> users) {
        getUserDao().insertInTx(users);
    }

    public static void update(User user) {
        getUserDao().update(user);
    }

    public static void updateInTx(Iterable<User> users) {
        getUserDao().updateInTx(users);
    }

    public static void updateInTx(User... users) {
        getUserDao().updateInTx(users);
    }

    public static List<User> loadAll() {
        return getUserDao().loadAll();
    }

    // 根据条件查询, 这里只是举个例子
    private static List<User> query() {

        UserDao userDao = DbHelper.getUserDao();
        // where里面是可变参数
        Query<User> query = userDao.queryBuilder()
                .where(UserDao.Properties.Name.eq("小明"), UserDao.Properties.PopedomType.eq(14))
                .build();
        return query.list();
    }

    /**
     * 通过NFC CardNum 获取当前记录
     *
     * @param cardNum
     * @return
     */
    public static List<User> queryByNFCCard(String cardNum) {
        Query<User> query = getUserDao().queryBuilder().where(UserDao.Properties.CardNo.eq(cardNum))
                .build();
        return query.list();
    }

    /**
     * 通过指纹ID 获取当前记录
     *
     * @param fingerId
     * @value 1 指纹1
     * @value 2 指纹2
     * @value 3 指纹3
     * @return
     */
    public static User queryByFinger(int fingerId) {
        Query<User> query = getUserDao().queryBuilder()
                .where(UserDao.Properties.FingerID1.eq(fingerId)).build();
        if (query.list().size() == 0) {
            query = getUserDao().queryBuilder().where(UserDao.Properties.FingerID2.eq(fingerId))
                    .build();
        } else {
            return query.list().get(0);
        }
        if (query.list().size() == 0) {
            query = getUserDao().queryBuilder().where(UserDao.Properties.FingerID3.eq(fingerId))
                    .build();
        } else {
            return query.list().get(0);
        }
        if (query.list().size() == 0) {
            return null;
        } else {
            return query.list().get(0);
        }
    }

    /**
     * Get User List By UniqueId
     *
     * @param uniqueId
     * @return
     */
    public static List<User> queryByUniqueId(String uniqueId) {
        Query<User> query = getUserDao().queryBuilder()
                .where(UserDao.Properties.UniqueId.eq(uniqueId)).build();
        return query.list();
    }

    /**
     * 通过faceId获取是否可开门
     */
    public static List<User> queryByFaceId(String faceId) {
        Query<User> query = getUserDao().queryBuilder()
                .where(UserDao.Properties.FaceID.eq(faceId), UserDao.Properties.PopedomType.eq(1))
                .build();
        return query.list();
    }

    /**
     * 插入或更新一条数据
     *
     * @param user
     * @return
     */
    public static long insertUser(User user) {
        //名称为空
        if (TextUtils.isEmpty(user.getName())) {
            return Constant.NULL_NAME;
        }
        //权限类型为空
        if (TextUtils.isEmpty(user.getPopedomType())) {
            return Constant.NULL_POPEDOMTYPE;
        }
        //        //唯一标识
        //        if (TextUtils.isEmpty(user.getUniqueId())) {
        //            return Constant.NULL_UNIQUEID;
        //        }
        if (user.getId() == null) {
            user.setUniqueId(MD5.get16Lowercase(UUID.randomUUID().toString()));
            return getUserDao().insert(user);
        } else {
            getUserDao().update(user);
            return Constant.UPDATE_SUCCESS;
        }
    }

    /**
     * 查询数字密码是否可开门
     */
    public static List<User> queryByPassword(String password) {
        UserDao userDao = DbHelper.getUserDao();
        // where里面是可变参数
        Query<User> query = userDao.queryBuilder().where(UserDao.Properties.PassWord.eq(password),
                UserDao.Properties.PopedomType.eq(1)).build();
        return query.list();
    }

    /**
     * 根据唯一标示ID获取User
     */
    public static List<User> queryUserById(String id) {
        UserDao userDao = DbHelper.getUserDao();
        // where里面是可变参数
        Query<User> query = userDao.queryBuilder().where(UserDao.Properties.UniqueId.eq(id))
                .build();
        return query.list();
    }

}
