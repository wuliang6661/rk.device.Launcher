package rk.device.launcher.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;

import rk.device.launcher.R;
import rk.device.launcher.service.SleepTaskServer;
import rk.device.launcher.ui.setting.SleepActivity;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.widget.CustomFrameLayout;

/**
 * Created by wuliang on 2017/12/25.
 * <p>
 * 所有Fragment的共有父类
 */

public class BaseDialogFragment extends DialogFragment implements CustomFrameLayout.OnFrameLayoutTouchListener, DialogInterface.OnKeyListener {

    public CustomFrameLayout mCustomFrameLayout;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCustomFrameLayout = view.findViewById(R.id.custom_framelayout);
        if (mCustomFrameLayout != null) {
            mCustomFrameLayout.setOnFrameLayoutTouchListener(this);
        }

        getDialog().setOnKeyListener(this);
//        getView().setOnTouchListener((v, event) -> {
//            LogUtil.d("wuliang", " getView dialogFragment OnTouchListener");
//            if (getActivity() instanceof SleepActivity) {
//                return false;
//            }
//            SleepTaskServer.getSleepHandler(getActivity()).sendEmptyMessage(0x11);
//            return true;
//        });
//        getDialog().getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                LogUtil.d("wuliang", "dialogFragment OnTouchListener");
//                return false;
//            }
//        });
    }

    @Override
    public void onFrameLayoutTouched() {
        LogUtil.d("wuliang", " getView dialogFragment OnTouchListener");
        removeSleep();
    }

    private void removeSleep() {
        if (getActivity() instanceof SleepActivity) {
            return;
        }
        SleepTaskServer.getSleepHandler(getActivity()).sendEmptyMessage(0x11);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        removeSleep();
        return false;
    }
}
