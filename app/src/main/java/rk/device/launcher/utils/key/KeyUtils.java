package rk.device.launcher.utils.key;

import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.encrypt.RSAUtils;

/**
 * Created by wuliang on 2018/1/12.
 * <p>
 * 本地激活码的存取工具类
 */

public class KeyUtils {

    private static final String keyPath = "/data/rk_backup/key";


    /**
     * 存储激活码
     */
    public static boolean saveKey(String key) {
        String encodeKey;
        try {
            encodeKey = RSAUtils.encrypt(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        boolean isWrite = FileUtils.writeFileFromString(keyPath, encodeKey, false);
        FileUtils.setPermission(keyPath);
        return isWrite;
    }


    /**
     * 获取激活码
     */
    public static String getKey() {
        if (isHaveKey()) {
            String encodeKey = FileUtils.readFile2String(keyPath, "UTF-8");
            try {
                return RSAUtils.decrypt(encodeKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }


    /**
     * 机器是否已激活
     */
    public static boolean isHaveKey() {
        return FileUtils.getFileByPath(keyPath).exists();
    }
}
