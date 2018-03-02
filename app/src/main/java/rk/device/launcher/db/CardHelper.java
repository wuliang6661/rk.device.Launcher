package rk.device.launcher.db;

import android.text.TextUtils;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import rk.device.launcher.db.entity.Card;
import rk.device.launcher.db.entity.CardDao;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.TimeUtils;

/**
 * Created by hanbin on 2018/3/1.
 * <p/>
 * 对于卡的增删改
 */
public class CardHelper {

    private static CardDao      sCardDao;

    public static CardDao getCardDao() {
        if (sCardDao == null) {
            sCardDao = DbManager.getInstance().getCardDao();
        }
        return sCardDao;
    }

    /**
     * 此处需要返回insert之后的反馈，不然无法知道是否insert成功
     * 
     * @param personId
     * @param number
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean insert(String personId, String number, int status, int beginTime,
                                 int endTime) {
        Card card = new Card();
        card.setPersonId(personId);
        card.setNumber(number);
        card.setStatus(status);
        card.setBeginTime(beginTime);
        card.setEndTime(endTime);
        card.setUpdateTime(TimeUtils.getTimeStamp());
        try {
            long rowId = getCardDao().insert(card);
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
    public static int update(int id, String number, int status, int beginTime, int endTime) {
        Query<Card> query = getCardDao().queryBuilder()
                .whereOr(CardDao.Properties.Id.eq(id), CardDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        List<Card> cards = query.list();
        if (cards.size() == 0) {
            return Constant.NOT_EXIST;
        }
        Card card = query.list().get(0);
        if (!TextUtils.isEmpty(number)) {
            card.setNumber(number);
        }
        if (status != 0) {
            card.setStatus(status);
        }
        if (beginTime > 0) {
            card.setBeginTime(beginTime);
        }
        if (endTime > 0) {
            card.setEndTime(endTime);
        }
        card.setUpdateTime(TimeUtils.getTimeStamp());
        getCardDao().update(card);
        return Constant.UPDATE_SUCCESS;
    }

    /**
     * 删除
     *
     * @param card
     */
    public static void delete(Card card) {
        getCardDao().delete(card);
    }

    /**
     * 卡列表
     * 
     * @param personId
     * @return
     */
    public static List<Card> getList(String personId) {
        Query<Card> query = getCardDao().queryBuilder()
                .whereOr(CardDao.Properties.PersonId.eq(personId), CardDao.Properties.Status
                        .in(Constant.TO_BE_UPDATE, Constant.NORMAL, Constant.TO_BE_ADD))
                .build();
        return query.list();
    }
}
