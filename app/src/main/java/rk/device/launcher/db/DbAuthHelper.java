package rk.device.launcher.db;

import java.util.List;

import rk.device.launcher.db.dao.AuthorizationDao;
import rk.device.launcher.db.entity.Authorization;


/**
 * Created by mundane on 2018/1/2 下午3:02
 */

public class DbAuthHelper {
    private static AuthorizationDao sAuthDao;

    public static AuthorizationDao getAuthDao() {
        if (sAuthDao == null) {
            sAuthDao = DbManager.getInstance().getAuthorizationDao();
        }
        return sAuthDao;
    }

    public static boolean insert(Authorization auth) {
        try {
            long rowId = getAuthDao().insert(auth);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void delete(Authorization auth) {
        getAuthDao().delete(auth);
    }

    public static void deleteAll() {
        getAuthDao().deleteAll();
    }

    public static void insertInTx(Authorization... auths) {
        getAuthDao().insertInTx(auths);
    }

    public static void insertInTx(Iterable<Authorization> auths) {
        getAuthDao().insertInTx(auths);
    }

    public static void update(Authorization auth) {
        getAuthDao().update(auth);
    }

    public static void updateInTx(Iterable<Authorization> auths) {
        getAuthDao().updateInTx(auths);
    }

    public static void updateInTx(Authorization... auths) {
        getAuthDao().updateInTx(auths);
    }

    public static List<Authorization> loadAll() {
        return getAuthDao().loadAll();
    }


}
