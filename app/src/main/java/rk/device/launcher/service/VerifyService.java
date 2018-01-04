package rk.device.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import peripherals.NfcHelper;

/**
 * Created by hanbin on 2018/1/2.
 */

public class VerifyService extends Service {
    private static final String TAG    = "VerifyService";
    private boolean             isOpen = true;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Log.i(TAG, TAG + " init");
        isOpen = true;
        //初始化线程池
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOpen) {
                    int[] cardType = new int[1];
                    byte[] cardNumber = new byte[16];
                    int resultCode = NfcHelper.PER_nfcGetCard(cardType, cardNumber);
                    Log.i(TAG, TAG + "resultCode:" + resultCode);
                    Log.i(TAG, TAG + "NfcCard:" + bytesToHexString(cardNumber, cardType[0])
                            + "NfcType:" + cardType[0]);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                    Log.d(TAG,
                            TAG + android.os.Process.myPid() + " Thread: "
                                    + android.os.Process.myTid() + " name "
                                    + Thread.currentThread().getName());
                    //                    Log.d(TAG, TAG + " code:" + thread.getId());

                }
            }
        });
        thread.start();
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
        int length = 0;
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
        Log.i(TAG, TAG + " onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOpen = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
