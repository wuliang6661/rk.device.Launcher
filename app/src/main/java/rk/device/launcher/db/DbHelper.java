package rk.device.launcher.db;

import android.text.TextUtils;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import rk.device.launcher.db.dao.UserDao;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;

/**
 * @author : mundane
 * @time : 2017/4/14 14:06
 * @description :
 * @file : DbHelper.java
 */

public class DbHelper {

    private static UserDao sUserDao;

    public static UserDao getUserDao() {
        if (sUserDao == null) {
            sUserDao = DbManager.getInstance().getUserDao();
        }
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
     * @return
     */
    public static List<User> queryByFinger(int fingerId) {
        Query<User> query = getUserDao().queryBuilder()
                .where(UserDao.Properties.FingerID1.eq(fingerId)).build();
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
        //唯一标识
        if (TextUtils.isEmpty(user.getUniqueId())) {
            return Constant.NULL_UNIQUEID;
        }
        if (user.getId() == null) {
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
}
