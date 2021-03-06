package rk.device.launcher.db;

import android.text.TextUtils;

import org.greenrobot.greendao.query.Query;

import java.util.List;
import java.util.UUID;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.db.entity.UserDao;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.MD5;
import rk.device.launcher.utils.cache.CacheUtils;

/**
 * @author : mundane
 * @time : 2017/4/14 14:06
 * @description :
 * @file : DbHelper.java
 */

public class DbHelper {
    private static final String DB_NAME = CacheUtils.DB_PATH;
    private static final String DB_PATH_JOUR = CacheUtils.DB_PATH_JOUR;

    private static final String TAG = "DbHelper";

    public static UserDao getUserDao() {
        //if (sUserDao == null) {
        //    sUserDao = DbManager.getInstance().getUserDao();
        //}
        //File dbFile = new File(DB_NAME);
        //LogUtil.d(TAG, "dbFile.exists() = " + dbFile.exists());
        //FileUtils.setPermission(DB_NAME);
        //FileUtils.setPermission(DB_PATH_JOUR);
        return LauncherApplication.getDaoSession().getUserDao();
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
        user.setUpdateTime(System.currentTimeMillis());
        getUserDao().update(user);
    }

    public static void updateInTx(Iterable<User> users) {
        getUserDao().updateInTx(users);
    }

    public static void updateInTx(User... users) {
        getUserDao().updateInTx(users);
    }

    public static List<User> loadAll() {
        return getUserDao().queryBuilder().orderDesc(UserDao.Properties.CreateTime).where(UserDao.Properties.Status
                .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD)).build().list();
    }

    // 根据条件查询, 这里只是举个例子
    private static List<User> query() {

        UserDao userDao = DbHelper.getUserDao();
        // where里面是可变参数
        Query<User> query = userDao.queryBuilder()
                .where(UserDao.Properties.Name.eq("小明"), UserDao.Properties.Role.eq(14))
                .build();
        return query.list();
    }

    /**
     * Get User List By UniqueId
     *
     * @param uniqueId
     * @return
     */
    public static List<User> queryByUniqueId(String uniqueId) {
        Query<User> query = getUserDao().queryBuilder()
                .where(UserDao.Properties.UniqueId.eq(uniqueId),
                        UserDao.Properties.Status.notEq(Constant.TO_BE_DELETE))
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
        if (user.getRole() == 0) {
            return Constant.NULL_POPEDOMTYPE;
        }
        if (user.getId() == null) {
            user.setStatus(Constant.TO_BE_ADD);
            user.setUniqueId(MD5.get16Lowercase(UUID.randomUUID().toString()));
            user.setCreateTime(System.currentTimeMillis());
            getUserDao().insert(user);
            return 0;
        } else {
            if (user.getStatus() == Constant.NORMAL) {
                user.setStatus(Constant.TO_BE_UPDATE);
            }
            user.setUpdateTime(System.currentTimeMillis());
            getUserDao().update(user);
            return Constant.UPDATE_SUCCESS;
        }
    }

    /**
     * 根据唯一标示ID获取User
     */
    public static List<User> queryUserById(String id) {
        UserDao userDao = DbHelper.getUserDao();
        // where里面是可变参数
        Query<User> query = userDao.queryBuilder().where(UserDao.Properties.UniqueId.eq(id),
                UserDao.Properties.Status.notEq(Constant.TO_BE_DELETE))
                .build();
        return query.list();
    }

    /**
     * 查询未同步到服务器的所有user
     */
    public static List<User> queryUserByUpdate() {
        UserDao userDao = DbHelper.getUserDao();
        // where里面是可变参数
        Query<User> query = userDao.queryBuilder().whereOr(
                UserDao.Properties.Status.eq(Constant.TO_BE_ADD),
                UserDao.Properties.Status.eq(Constant.TO_BE_UPDATE),
                UserDao.Properties.Status.eq(Constant.TO_BE_DELETE))
                .build();
        return query.list();
    }
}
