package rk.device.launcher.db;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.db.entity.AD;
import rk.device.launcher.db.entity.ADDao;

/**
 * Created by wuliang on 2018/3/17.
 * <p>
 * 操作广告表的工具类
 */

public class AdHelper {

    public static ADDao getADDao() {
        return LauncherApplication.getDaoSession().getADDao();
    }


    /**
     * 插入一张广告
     */
    public static boolean insert(AD ad) {
        try {
            getADDao().insert(ad);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 更新一张广告
     */
    public static boolean update(AD ad) {
        try {
            getADDao().update(ad);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除一张广告
     */
    public static boolean delete(AD ad) {
        try {
            getADDao().delete(ad);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据广告ID查询广告
     */
    public static List<AD> queryByAdId(String adId) {
        Query<AD> query = getADDao().queryBuilder()
                .where(ADDao.Properties.AdID.eq(adId))
                .build();
        return query.list();
    }


    /**
     * 查询所有的广告
     */
    public static List<AD> queryAll() {
        Query<AD> query = getADDao().queryBuilder().build();
        return query.list();
    }

}
