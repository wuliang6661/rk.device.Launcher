package rk.device.launcher.db;

import android.text.TextUtils;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import rk.device.launcher.db.entity.CodePassword;
import rk.device.launcher.db.entity.CodePasswordDao;
import rk.device.launcher.db.entity.Face;
import rk.device.launcher.db.entity.FaceDao;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.TimeUtils;

/**
 * Created by hanbin on 2018/3/1.
 * <p/>
 * 对于数字密码的增删改
 */
public class CodePasswordHelper {

    private static CodePasswordDao sCodePasswordDao;

    public static CodePasswordDao getCodePasswordDao() {
        if (sCodePasswordDao == null) {
            sCodePasswordDao = DbManager.getInstance().getCodePasswordDao();
        }
        return sCodePasswordDao;
    }

    /**
     * 此处需要返回insert之后的反馈，不然无法知道是否insert成功
     * 
     * @param personId
     * @param password
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean insert(String personId, String password, int status, int beginTime,
                                 int endTime) {
        CodePassword codePassword = new CodePassword();
        codePassword.setPersonId(personId);
        codePassword.setPassword(password);
        codePassword.setStatus(status);
        codePassword.setBeginTime(beginTime);
        codePassword.setEndTime(endTime);
        codePassword.setCreateTime(TimeUtils.getTimeStamp());
        try {
            long rowId = getCodePasswordDao().insert(codePassword);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新
     * 
     * @param
     */
    public static int update(long id, String password, int status, int beginTime, int endTime) {
        Query<CodePassword> query = getCodePasswordDao().queryBuilder()
                .where(CodePasswordDao.Properties.Id.eq(id), CodePasswordDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        List<CodePassword> codePasswords = query.list();
        if (codePasswords.size() == 0) {
            return Constant.NOT_EXIST;
        }
        CodePassword codePassword = query.list().get(0);
        if (!TextUtils.isEmpty(password)) {
            codePassword.setPassword(password);
        }
        if (status != 0) {
            codePassword.setStatus(status);
        }
        if (beginTime > 0) {
            codePassword.setBeginTime(beginTime);
        }
        if (endTime > 0) {
            codePassword.setEndTime(endTime);
        }
        codePassword.setUpdateTime(TimeUtils.getTimeStamp());
        getCodePasswordDao().update(codePassword);
        return Constant.UPDATE_SUCCESS;
    }

    /**
     * 删除
     *
     * @param codePassword
     */
    public static void delete(CodePassword codePassword) {
        getCodePasswordDao().delete(codePassword);
    }

    /**
     * 数字密码列表
     * 
     * @param personId
     * @return
     */
    public static List<CodePassword> getList(String personId) {
        Query<CodePassword> query = getCodePasswordDao().queryBuilder()
                .where(CodePasswordDao.Properties.PersonId.eq(personId), CodePasswordDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        return query.list();
    }


    /**
     * 根据密码查询列表
     */
    public static List<CodePassword> getPassword(String password){
        Query<CodePassword> query = getCodePasswordDao().queryBuilder()
                .whereOr(CodePasswordDao.Properties.Password.eq(password), CodePasswordDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        return query.list();
    }



}
