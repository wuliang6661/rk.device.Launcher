package rk.device.launcher.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.guo.android_extend.java.AbsLoop;

import java.lang.ref.WeakReference;

import peripherals.FingerHelper;
import peripherals.NfcHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.NFCAddEvent;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.verify.OpenUtils;
import rk.device.launcher.utils.verify.VerifyUtils;

/**
 * Created by hanbin on 2018/1/2.
 * <p>
 * 指纹于刷卡的服务
 */

public class VerifyService extends Service {


    private static final int DELAY = 500;
    private static final String TAG = "VerifyService";
    private static final String NFC_ADD_PAGE = "rk.device.launcher.ui.nfcadd.NfcaddActivity";
    private static final String NFC_DETECTION = "rk.device.launcher.ui.detection.NfcDetection";
    private static final String FINGER_ADD_PAGE = "rk.device.launcher.ui.fingeradd.FingeraddActivity";
    private static final String FINGER_DETECTION = "rk.device.launcher.ui.detection.FinderDetection";
    private static boolean isOpen = true;


    NfcThread nfcThread;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        LogUtil.i(TAG, TAG + " init");
        isOpen = true;
        nfcThread = new NfcThread(this);
        nfcThread.start();
//        Thread fingerThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isOpen) {
//                    if (LauncherApplication.fingerModuleID == -1) {
//                        LogUtil.i(TAG, "finger init error");
//                        return;
//                    }
//                    fingerService();
//                }
//            }
//        });
//        fingerThread.start();
    }


    private static class NfcThread extends AbsLoop {

        WeakReference<VerifyService> weakReference;

        NfcThread(VerifyService service) {
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void setup() {

        }

        @Override
        public void loop() {
            while (isOpen) {
                if (weakReference.get() != null) {
                    weakReference.get().nfcService();
                    LogUtil.d(TAG,
                            TAG + android.os.Process.myPid() + " Thread: "
                                    + android.os.Process.myTid() + " name "
                                    + Thread.currentThread().getName());
                    ActivityManager activityManager = (ActivityManager) weakReference.get().getSystemService(ACTIVITY_SERVICE);
                    //最大分配内存
                    int memory = activityManager.getMemoryClass();
                    System.out.println("memory: " + memory);
                    //最大分配内存获取方法2
                    float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
                    //当前分配的总内存
                    float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
                    //剩余内存
                    float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
                    System.out.println("maxMemory: " + maxMemory);
                    System.out.println("totalMemory: " + totalMemory);
                    System.out.println("freeMemory: " + freeMemory);
                } else {
                    return;
                }
            }
        }

        @Override
        public void over() {

        }
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
        if (LauncherApplication.sInitFingerSuccess == -1) {
            LogUtil.i(TAG, TAG + " finger init failed.");
            sleep();
            return;
        }
        if (LauncherApplication.sIsFingerAdd == 1 && isTopActivity().equals(FINGER_ADD_PAGE)) {
            LogUtil.i(TAG, TAG + " model:finger add");
        } else if (LauncherApplication.sIsFingerAdd == 2
                && isTopActivity().equals(FINGER_DETECTION)) {
            LogUtil.i(TAG, TAG + " model:finger detectiton");
        } else {
            LogUtil.i(TAG, TAG + " model:finger verify");
            int resultCode = FingerHelper.JNIFpFingerMatch(LauncherApplication.fingerModuleID);
            LogUtil.i(TAG, TAG + " fingerId:" + resultCode);
            if (resultCode > 0) {
                User user = VerifyUtils.getInstance().verifyByFinger(resultCode);
                if (user == null) {
                    return;
                }
                if (TimeUtils.getTimeStamp() < user.getStartTime() / 1000l
                        || TimeUtils.getTimeStamp() > user.getEndTime() / 1000l) {
                    T.showShort(LauncherApplication.getContext().getString(R.string.illeagel_user));
                    return;
                }
                OpenUtils.getInstance().open(VerifyTypeConstant.TYPE_FINGER, user.getUniqueId(),
                        user.getName(), resultCode);
            } else {
                if (resultCode == -3) {//未初始化
                    LogUtil.i("finger", "finger init");
                    FingerHelper.JNIFpInit();
                } else if (resultCode == -2) {//匹配失败

                }
            }
        }
        sleep();
    }


    int[] cardType;
    byte[] cardNumber;


    /**
     * nfc service
     */
    private void nfcService() {
        cardType = new int[1];
        cardNumber = new byte[16];
        //read nfc
        int resultCode = NfcHelper.PER_nfcGetCard(cardType, cardNumber);
        LogUtil.i(TAG, TAG + " resultCode:" + resultCode);
        if (resultCode == 0) {
            int type = cardType[0];
            if (type == 0) {
//                LogUtil.i(TAG, TAG + ": no card.");
                sleep();
                return;
            }
            String NFCCard = bytesToHexString(cardNumber, cardType[0]);
            if (NFCCard == null) {
                LogUtil.i(TAG, TAG + ": cardNumber is null.");
                return;
            }
            LogUtil.i(TAG, TAG + "NfcCard:" + NFCCard + "NfcType:" + cardType[0]);
            //nfc add model
            if (LauncherApplication.sIsNFCAdd == 1 && (isTopActivity().equals(NFC_ADD_PAGE)
                    || isTopActivity().equals(NFC_DETECTION))) {
                RxBus.getDefault().post(new NFCAddEvent(NFCCard));
            } else {
                //nfc verify model
                User user = VerifyUtils.getInstance().verifyByNfc(NFCCard);
                if (user == null) {
                    return;
                }
                if (TimeUtils.getTimeStamp() < user.getStartTime() / 1000l
                        || TimeUtils.getTimeStamp() > user.getEndTime() / 1000l) {
                    T.showShort(LauncherApplication.getContext().getString(R.string.illeagel_user));
                    return;
                }
                OpenUtils.getInstance().open(VerifyTypeConstant.TYPE_CARD, user.getUniqueId(),
                        user.getName());
            }
        } else {
            if (resultCode == 1) {//未初始化
                NfcHelper.PER_nfcInit();
            }
            LogUtil.i(TAG, TAG + " read nfc failed.");
        }
        sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        destory();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder result = null;
        if (null == result) result = new MyBinder();
        return result;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.e("VerifyService onUnbind");
        destory();
        return super.onUnbind(intent);
    }


    @Override
    public void unbindService(ServiceConnection conn) {
        LogUtil.e("VerifyService unbindService");
        destory();
        super.unbindService(conn);
    }

    private void destory() {
        isOpen = false;
        nfcThread.shutdown();
        LogUtil.i(TAG, TAG + ": onDestroy.");
        int status = NfcHelper.PER_nfcDeinit();
        if (status == 0) {
            LogUtil.i(TAG, TAG + ": Nfc deinit success.");
        } else {
            NfcHelper.PER_nfcDeinit();
            LogUtil.i(TAG, TAG + ": Nfc deinit failed.");
        }
        if (LauncherApplication.fingerModuleID == -1) {
            return;
        }
        status = FingerHelper.JNIFpDeInit(LauncherApplication.fingerModuleID);
        if (status == 0) {
            LogUtil.i(TAG, TAG + ": finger deinit success.");
        } else {
            FingerHelper.JNIFpDeInit(LauncherApplication.fingerModuleID);
            LogUtil.i(TAG, TAG + ": finger deinit failed.");
        }
    }


    private static class MyBinder extends Binder {
    }
}
