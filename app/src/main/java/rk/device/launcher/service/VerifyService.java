package rk.device.launcher.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import cn.aidl.IMyAidlInterface;
import peripherals.FingerHelper;
import peripherals.NfcHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.DestoryEvent;
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

    private static final int USER_ILLEAGEL = 1001;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case USER_ILLEAGEL:
                    T.showShort(LauncherApplication.getContext().getString(R.string.illeagel_user));
                    break;
            }
            return false;
        }
    });


    NfcThread nfcThread;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        init();
        Intent intent = new Intent(this, KeepAliveService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void init() {
        LogUtil.i(TAG, TAG + " init");
        isOpen = true;
        nfcThread = new NfcThread(this);
        nfcThread.start();
        Thread fingerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOpen) {
                    if (LauncherApplication.fingerModuleID == -1) {
                        LogUtil.i(TAG, "finger init error");
                        return;
                    }
                    fingerService();
                }
            }
        });
        fingerThread.start();
    }


    private static class NfcThread extends Thread {

        WeakReference<VerifyService> weakReference;

        NfcThread(VerifyService service) {
            weakReference = new WeakReference<>(service);
        }


        @Override
        public void run() {
            super.run();
            while (isOpen) {
                if (weakReference.get() != null) {
                    weakReference.get().nfcService();
                    LogUtil.d(TAG, TAG + android.os.Process.myPid() + " Thread: " + android.os.Process.myTid() + " name " + Thread.currentThread().getName());
                    if (weakReference.get().myAidlInterface != null) {
                        try {
                            weakReference.get().myAidlInterface.sendService(0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    return;
                }
            }
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
                    Message msg = new Message();
                    msg.what = USER_ILLEAGEL;
                    handler.sendMessage(msg);
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
                    Message msg = new Message();
                    msg.what = USER_ILLEAGEL;
                    handler.sendMessage(msg);
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DestoryEvent messageEvent) {
        destory();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        LogUtil.e("VerifyService onDestory");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void destory() {
        isOpen = false;
        LogUtil.i(TAG, TAG + ": onDestroy.");
        unbindService(connection);
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


    private IMyAidlInterface myAidlInterface;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
