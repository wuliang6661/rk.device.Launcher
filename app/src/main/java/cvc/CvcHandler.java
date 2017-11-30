package cvc;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by hanbin on 2017/11/28.
 */
public class CvcHandler extends Handler {

    private int     ret;
    private int[]   livingCode = new int[3];
    private int     count      = 0;
    private boolean isAdjust   = false;

    public CvcHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case EventUtil.START_CVC:
                if (isAdjust) {
                    Log.i("result code:", String.valueOf(livingCode[0]));
                    return;
                }
                int width = msg.arg1;
                int height = msg.arg2;
                if (width == 0 || height == 0) {
                    Log.i("CVC_determineLivingFace", "请设置宽高");
                    return;
                } else {
                    //如果是第一次，需要设置宽高
                    if (count == 0) {
                        ret = CvcHelper.CVC_calibratorInit(width, height);
                        if (ret != 0) {
                            Log.i("CVC_calibratorInit", "初始化校准失败");
                            return;
                        }
                    }
                    //设置完宽高，采集棋盘格
                    ret = CvcHelper.CVC_calibratorCollectImage();
                    if (ret != 0) {
                        Log.i("CVC_calibratorCollect", "未识别到棋盘格");
                        return;
                    }
                    count++;
                }
                break;
            case EventUtil.START_CALIBRATION:
                ret = CvcHelper.CVC_calibratorStereoCalibrate();
                if (ret != 0) {
                    Log.i("CVC_calibratorStereo", "校准失败");
                    return;
                } else {
                    Log.i("CVC_calibratorStereo", "校准成功");
                    isAdjust = true;
                    count = 0;
                }
                break;
        }
        super.handleMessage(msg);
    }
}