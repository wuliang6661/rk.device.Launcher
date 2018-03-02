package rk.device.launcher.db;

import android.text.TextUtils;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import rk.device.launcher.db.entity.Finger;
import rk.device.launcher.db.entity.FingerDao;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.TimeUtils;

/**
 * Created by hanbin on 2018/3/1.
 * <p/>
 * 对于指纹的增删改
 */
public class FingerHelper {

    private static FingerDao sFingerDao;

    public static FingerDao getFingerDao() {
        if (sFingerDao == null) {
            sFingerDao = DbManager.getInstance().getFingerDao();
        }
        return sFingerDao;
    }

    /**
     * 此处需要返回insert之后的反馈，不然无法知道是否insert成功
     * 
     * @param personId
     * @param fingerId
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean insert(String personId, String fingerId, int status, int beginTime,
                                 int endTime) {
        Finger card = new Finger();
        card.setPersonId(personId);
        card.setFingerId(fingerId);
        card.setStatus(status);
        card.setBeginTime(beginTime);
        card.setEndTime(endTime);
        card.setUpdateTime(TimeUtils.getTimeStamp());
        try {
            long rowId = getFingerDao().insert(card);
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
    public static int update(int id, String fingerId, int status, int beginTime, int endTime) {
        Query<Finger> query = getFingerDao().queryBuilder()
                .whereOr(FingerDao.Properties.Id.eq(id), FingerDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        List<Finger> fingers = query.list();
        if (fingers.size() == 0) {
            return Constant.NOT_EXIST;
        }
        Finger finger = query.list().get(0);
        if (!TextUtils.isEmpty(fingerId)) {
            finger.setFingerId(fingerId);
        }
        if (status != 0) {
            finger.setStatus(status);
        }
        if (beginTime > 0) {
            finger.setBeginTime(beginTime);
        }
        if (endTime > 0) {
            finger.setEndTime(endTime);
        }
        finger.setUpdateTime(TimeUtils.getTimeStamp());
        getFingerDao().update(finger);
        return Constant.UPDATE_SUCCESS;
    }

    /**
     * 删除
     *
     * @param finger
     */
    public static void delete(Finger finger) {
        getFingerDao().delete(finger);
    }

    /**
     * 指纹列表
     * 
     * @param personId
     * @return
     */
    public static List<Finger> getList(String personId) {
        Query<Finger> query = getFingerDao().queryBuilder()
                .whereOr(FingerDao.Properties.PersonId.eq(personId), FingerDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        return query.list();
    }
}
