package rk.device.launcher.utils.verify;

import java.util.List;

import rk.device.launcher.db.CardHelper;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.FingerPrintHelper;
import rk.device.launcher.db.entity.Card;
import rk.device.launcher.db.entity.Finger;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.utils.LogUtil;

import static rk.device.launcher.db.DbHelper.queryByUniqueId;

/**
 * Created by hanbin on 2017/12/28.
 * <p/>
 * NFC，指纹等校验帮助类
 */
public class VerifyUtils {


    public static VerifyUtils getInstance() {
        return new VerifyUtils();
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
        List<Card> cardList = CardHelper.queryByCardNumber(nfcCard);
        if (cardList.size() > 0) {
            List<User> userList = queryByUniqueId(cardList.get(0).getPersonId());
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
        List<Finger> fingerList = FingerPrintHelper.getListByFingerId(fingerId);
        if (fingerList.size() > 0) {
            List<User> userList = DbHelper.queryByUniqueId(fingerList.get(0).getPersonId());
            if (userList.size() > 0) {
                return userList.get(0);
            }
        }
        return null;
    }

    /**
     * Get User by uniqueId
     *
     * @param uniqueId
     * @return
     */
    public User queryUserByUniqueId(String uniqueId) {
        List<User> userList = DbHelper.queryByUniqueId(uniqueId);
        if (!userList.isEmpty()) {
            LogUtil.i("VerifyUtils", "uniqueId:" + userList.get(0).getUniqueId());
            return userList.get(0);
        }
        return null;
    }

}
