package rk.device.launcher.db.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import rk.device.launcher.db.entity.User;
import rk.device.launcher.db.entity.Authorization;
import rk.device.launcher.db.entity.Record;

import rk.device.launcher.db.dao.UserDao;
import rk.device.launcher.db.dao.AuthorizationDao;
import rk.device.launcher.db.dao.RecordDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig authorizationDaoConfig;
    private final DaoConfig recordDaoConfig;

    private final UserDao userDao;
    private final AuthorizationDao authorizationDao;
    private final RecordDao recordDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        authorizationDaoConfig = daoConfigMap.get(AuthorizationDao.class).clone();
        authorizationDaoConfig.initIdentityScope(type);

        recordDaoConfig = daoConfigMap.get(RecordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        authorizationDao = new AuthorizationDao(authorizationDaoConfig, this);
        recordDao = new RecordDao(recordDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(Authorization.class, authorizationDao);
        registerDao(Record.class, recordDao);
    }
    
    public void clear() {
        userDaoConfig.clearIdentityScope();
        authorizationDaoConfig.clearIdentityScope();
        recordDaoConfig.clearIdentityScope();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public AuthorizationDao getAuthorizationDao() {
        return authorizationDao;
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

}
