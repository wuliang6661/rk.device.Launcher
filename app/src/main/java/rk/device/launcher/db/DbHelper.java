package rk.device.launcher.db;


import org.greenrobot.greendao.query.Query;

import java.util.List;

import rk.device.launcher.db.entity.User;
import rk.device.launcher.db.entity.UserDao;


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

    public static void insert(User user) {
        getUserDao().insert(user);
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
    public static List<User> query() {

        UserDao userDao = DbHelper.getUserDao();
        // where里面是可变参数
        Query<User> query = userDao.queryBuilder()
                .where(UserDao.Properties.Name.eq("小明"), UserDao.Properties.PopedomType.eq(14))
                .build();
        return query.list();
    }


}
