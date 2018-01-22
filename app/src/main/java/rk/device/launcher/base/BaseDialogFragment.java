package rk.device.launcher.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import rk.device.launcher.service.SleepTaskServer;
import rk.device.launcher.ui.setting.SleepActivity;

/**
 * Created by wuliang on 2017/12/25.
 * <p>
 * 所有Fragment的共有父类
 */

public class BaseDialogFragment extends DialogFragment {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setOnTouchListener((v, event) -> {
            if (getActivity() instanceof SleepActivity) {
                return false;
            }
            SleepTaskServer.getSleepHandler(getActivity()).sendEmptyMessage(0x11);
            return true;
        });
    }

}
