package rk.device.launcher.db;

import android.text.TextUtils;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import rk.device.launcher.db.entity.Face;
import rk.device.launcher.db.entity.FaceDao;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.TimeUtils;

/**
 * Created by hanbin on 2018/3/1.
 * <p/>
 * 对于人脸的增删改
 */
public class FaceHelper {

    private static FaceDao sFaceDao;

    public static FaceDao getFaceDao() {
        if (sFaceDao == null) {
            sFaceDao = DbManager.getInstance().getFaceDao();
        }
        return sFaceDao;
    }

    /**
     * 此处需要返回insert之后的反馈，不然无法知道是否insert成功
     * 
     * @param personId
     * @param faceId
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean insert(String personId, String faceId, int status, int beginTime,
                                 int endTime) {
        Face face = new Face();
        face.setPersonId(personId);
        face.setFaceId(faceId);
        face.setStatus(status);
        face.setBeginTime(beginTime);
        face.setEndTime(endTime);
        face.setCreateTime(TimeUtils.getTimeStamp());
        try {
            long rowId = getFaceDao().insert(face);
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
    public static int update(long id, String faceId, int status, int beginTime, int endTime) {
        Query<Face> query = getFaceDao().queryBuilder()
                .whereOr(FaceDao.Properties.Id.eq(id), FaceDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        List<Face> faces = query.list();
        if (faces.size() == 0) {
            return Constant.NOT_EXIST;
        }
        Face face = query.list().get(0);
        if (!TextUtils.isEmpty(faceId)) {
            face.setFaceId(faceId);
        }
        if (status != 0) {
            face.setStatus(status);
        }
        if (beginTime > 0) {
            face.setBeginTime(beginTime);
        }
        if (endTime > 0) {
            face.setEndTime(endTime);
        }
        face.setUpdateTime(TimeUtils.getTimeStamp());
        getFaceDao().update(face);
        return Constant.UPDATE_SUCCESS;
    }

    /**
     * 删除
     *
     * @param face
     */
    public static void delete(Face face) {
        getFaceDao().delete(face);
    }

    /**
     * 人脸列表
     * 
     * @param personId
     * @return
     */
    public static List<Face> getList(String personId) {
        Query<Face> query = getFaceDao().queryBuilder()
                .whereOr(FaceDao.Properties.PersonId.eq(personId), FaceDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        return query.list();
    }


}
