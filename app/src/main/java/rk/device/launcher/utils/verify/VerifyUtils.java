package rk.device.launcher.utils.verify;

import java.util.List;

import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;

/**
 * Created by hanbin on 2017/12/28.
 * <p/>
 * NFC，指纹等校验帮助类
 */
public class VerifyUtils {

    private static VerifyUtils verifyUtils = null;

    public static VerifyUtils getInstance() {
        if (verifyUtils == null) {
            synchronized (VerifyUtils.class) {
                if (verifyUtils == null) {
                    verifyUtils = new VerifyUtils();
                }
            }
        }
        return verifyUtils;
    }

    public VerifyUtils() {

    }

    /**
     * NFC检验
     * 
     * @param nfcCard
     * @return
     */
    public String verifyByNfc(String nfcCard) {
        List<User> userList = DbHelper.queryByNFCCard(nfcCard);
        if (userList.size() > 0) {
            return userList.get(0).getPopedomType();
        }
        return "";
    }

    /**
     * 指纹检验
     * 
     * @param fingerId
     * @return
     */
    public boolean verifyByFinger(int fingerId) {
        List<User> userList = DbHelper.queryByFinger(fingerId);
        if (userList.size() > 0) {
            return true;
        }
        return false;
    }

}
