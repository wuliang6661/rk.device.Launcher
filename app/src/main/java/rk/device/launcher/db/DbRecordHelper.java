package rk.device.launcher.db;

import java.util.List;

import rk.device.launcher.db.dao.RecordDao;
import rk.device.launcher.db.entity.Record;


/**
 * Created by mundane on 2018/1/2 下午3:02
 */

public class DbRecordHelper {
    private static RecordDao sRecordDao;

    public static RecordDao getRecordDao() {
        if (sRecordDao == null) {
            sRecordDao = DbManager.getInstance().getRecordDao();
        }
        return sRecordDao;
    }

    public static boolean insert(Record record) {
        try {
            long rowId = getRecordDao().insert(record);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void delete(Record record) {
        getRecordDao().delete(record);
    }

    public static void deleteAll() {
        getRecordDao().deleteAll();
    }

    public static void insertInTx(Record... records) {
        getRecordDao().insertInTx(records);
    }

    public static void insertInTx(Iterable<Record> records) {
        getRecordDao().insertInTx(records);
    }

    public static void update(Record record) {
        getRecordDao().update(record);
    }

    public static void updateInTx(Iterable<Record> records) {
        getRecordDao().updateInTx(records);
    }

    public static void updateInTx(Record... records) {
        getRecordDao().updateInTx(records);
    }

    public static List<Record> loadAll() {
        return getRecordDao().loadAll();
    }


}