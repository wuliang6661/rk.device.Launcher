package rk.device.launcher.utils.cache;

import rk.device.launcher.utils.FileUtils;

/**
 * Created by wuliang on 2018/2/1.
 * <p>
 * 建立本地缓存目录结构
 */

public class CacheUtils {


    private static final String BASE = "/data/rk_backup";

    private static final String BASE_CACHE = BASE + "/app_cache";   //存放程序进程的文件根目录

    /**
     * 本地日志保存路径
     */
    public static final String LOG_FILE = BASE_CACHE + "/log";

    /**
     * 人脸信息存放目录
     */
    private static final String FACE_FILE = BASE_CACHE + "/face";

    /**
     * 广告图片存放目录
     */
    private static final String IMG_FILE = BASE_CACHE + "/rk_ad";

    /**
     * 数据库存储路径
     */
    public static final String DB_PATH = BASE_CACHE + "/rk.db";

    public static final String DB_PATH_JOUR = BASE_CACHE + "/rk.db-journal";

    /**
     * 激活文件存储路径
     */
    public static final String KEY_PATH = BASE_CACHE + "/key";

    /**
     * 留言语音文件存储路径
     */
    public static final String RECODE_PATH = BASE_CACHE + "/recode.mp3";


    /**
     * 初始化本地目录结构
     */
    public static void init() {
        boolean isCreate = FileUtils.createOrExistsDir(BASE_CACHE);
        if (isCreate)
            FileUtils.setPermission(BASE_CACHE);
        boolean create = FileUtils.createOrExistsDir(FACE_FILE);
        if (create)
            FileUtils.setPermission(FACE_FILE);
        FileUtils.createOrExistsDir(IMG_FILE);
        FileUtils.setPermission(IMG_FILE);
    }


    /**
     * 删除所有文件
     */
    public static void clearAll() {
        FileUtils.deleteDir(BASE_CACHE);
    }


    /**
     * 获取ROM存储目录
     */
    public static String getBase() {
        return BASE;
    }


    /**
     * 获取本地根目录
     */
    public static String getBaseCache() {
        return BASE_CACHE;
    }

    /**
     * 获取人脸目录
     */
    public static String getFaceFile() {
        return FACE_FILE;
    }

    /**
     * 获取广告存放路径
     */
    public static String getImgFile() {
        return IMG_FILE;
    }


}
