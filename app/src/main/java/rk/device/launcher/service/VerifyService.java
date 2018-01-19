package rk.device.launcher.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import peripherals.FingerHelper;
import peripherals.NfcHelper;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.NFCAddEvent;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.verify.OpenUtils;
import rk.device.launcher.utils.verify.VerifyUtils;

/**
 * Created by hanbin on 2018/1/2.
 */

public class VerifyService extends Service {
    private static final int    DELAY           = 500;
    private static final String TAG             = "VerifyService";
    private static final String NFC_ADD_PAGE    = "rk.device.launcher.ui.nfcadd.NfcaddActivity";
    private static final String FINGER_ADD_PAGE = "rk.device.launcher.ui.fingeradd.FingeraddActivity";
    private boolean             isOpen          = true;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        LogUtil.i(TAG, TAG + " init");
        isOpen = true;
        Thread nfcThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOpen) {
                    nfcService();
                    LogUtil.d(TAG,
                            TAG + android.os.Process.myPid() + " Thread: "
                                    + android.os.Process.myTid() + " name "
                                    + Thread.currentThread().getName());

                }
            }
        });
        nfcThread.start();
        Thread fingerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOpen) {
                    fingerService();
                }
            }
        });
        fingerThread.start();
    }

    private String isTopActivity() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    /**
     * finger service
     */
    private void fingerService() {
        if(LauncherApplication.sInitFingerSuccess == -1){
//            LogUtil.i(TAG, TAG + " finger init failed.");
            sleep();
            return;
        }
        if (LauncherApplication.sIsFingerAdd == 1 && isTopActivity().equals(FINGER_ADD_PAGE)) {
            LogUtil.i(TAG, TAG + " model:finger add");
        } else {
            LogUtil.i(TAG, TAG + " model:finger verify");
            int resultCode = FingerHelper.JNIFpFingerMatch();
            LogUtil.i(TAG, TAG + " fingerId:" + resultCode);
            if (resultCode > 0) {
                User user = VerifyUtils.getInstance().verifyByFinger(resultCode);
                if (user == null) {
                    LogUtil.i(TAG, TAG + " user is null");
                    return;
                }
                LogUtil.i(TAG, TAG + " user " + user.getName());
                OpenUtils.getInstance().open(VerifyTypeConstant.TYPE_FINGER, user.getUniqueId(),
                        user.getName());
            }
        }
        sleep();
    }

    /**
     * nfc service
     */
    private void nfcService() {
        int[] cardType = new int[1];
        byte[] cardNumber = new byte[16];
        //read nfc
        int resultCode = NfcHelper.PER_nfcGetCard(cardType, cardNumber);
        LogUtil.i(TAG, TAG + " resultCode:" + resultCode);
        if (resultCode == 0) {
            int type = cardType[0];
            if (type == 0) {
                LogUtil.i(TAG, TAG + ": no card.");
                sleep();
                return;
            }
            String NFCCard = bytesToHexString(cardNumber, cardType[0]);
            if (NFCCard == null) {
                LogUtil.i(TAG, TAG + ": cardNumber is null.");
                return;
            }
            LogUtil.i(TAG, TAG + "NfcCard:" + bytesToHexString(cardNumber, cardType[0]) + "NfcType:"
                    + cardType[0]);
            //nfc add model
            if (LauncherApplication.sIsNFCAdd == 1 && isTopActivity().equals(NFC_ADD_PAGE)) {
                RxBus.getDefault().post(new NFCAddEvent(NFCCard));
            } else {
                //nfc verify model
                User user = VerifyUtils.getInstance().verifyByNfc(NFCCard);
                if (user == null) {
                    return;
                }
                //TextUtils.isEmpty(user.getPopedomType())
                OpenUtils.getInstance().open(VerifyTypeConstant.TYPE_CARD, user.getUniqueId(),user.getName());
//                RxBus.getDefault().post(new OpenDoorSuccessEvent("", VerifyTypeConstant.TYPE_CARD, 1));
            }
        } else {
            LogUtil.i(TAG, TAG + " read nfc failed.");
        }
        sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {

        }
    }

    /*
     * Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @params type 1:A(普通卡，4位) 2:B(身份证，8位)
     * @return hex string
     */
    public static String bytesToHexString(byte[] src, int type) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        int length;
        if (type == 2) {
            length = 8;
        } else {
            length = 4;
        }

        if (type == 2) {
            for (int i = 0; i < length; i++) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
        } else {
            for (int i = length - 1; i >= 0; i--) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, TAG + " onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOpen = false;
        LogUtil.i(TAG, TAG + ": onDestroy.");
        int status = NfcHelper.PER_nfcDeinit();
        if (status == 0) {
            LogUtil.i(TAG, TAG + ": Nfc deinit success.");
        } else {
            LogUtil.i(TAG, TAG + ": Nfc deinit failed.");
        }
        status = FingerHelper.JNIFpDeInit();
        if (status == 0) {
            LogUtil.i(TAG, TAG + ": finger deinit success.");
        } else {
            LogUtil.i(TAG, TAG + ": finger deinit failed.");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
