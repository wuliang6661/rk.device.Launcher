package rk.device.launcher.utils.verify;

import java.util.List;

import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.utils.LogUtil;

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
    public User verifyByNfc(String nfcCard) {
        List<User> userList = DbHelper.queryByNFCCard(nfcCard);
        if (userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

    /**
     * 指纹检验
     * 
     * @param fingerId
     * @return
     */
    public User verifyByFinger(int fingerId) {
        if (fingerId == 0) {
            return null;
        }
        return DbHelper.queryByFinger(fingerId);
    }

    /**
     * Get User by uniqueId
     * 
     * @param uniqueId
     * @return
     */
    public User queryUserByUniqueId(String uniqueId) {
        List<User> userList = DbHelper.queryByUniqueId(uniqueId);
        if (userList.size() > 0) {
            LogUtil.i("VerifyUtils", "uniqueId:" + userList.get(0).getUniqueId());
            return userList.get(0);
        }
        return null;
    }

}
