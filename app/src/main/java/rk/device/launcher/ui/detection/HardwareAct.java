package rk.device.launcher.ui.detection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.ui.setting.SetNetWorkActivity;
import rk.device.launcher.utils.ResUtil;

/**
 * Created by wuliang on 2018/2/6.
 * <p>
 * 所有需要检测的硬件列表
 */

public class HardwareAct extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.carema)
    LinearLayout carema;
    @Bind(R.id.nfc)
    LinearLayout nfc;
    @Bind(R.id.finder)
    LinearLayout finder;
    @Bind(R.id.led)
    LinearLayout led;
    @Bind(R.id.net_work)
    LinearLayout netWork;

    @Override
    protected int getLayout() {
        return R.layout.act_hardware_list;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle(ResUtil.getString(R.string.hardware_detecation));
        carema.setOnClickListener(this);
        nfc.setOnClickListener(this);
        finder.setOnClickListener(this);
        led.setOnClickListener(this);
        netWork.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.carema:
                gotoActivity(CaremaDetection.class, false);
                break;
            case R.id.led:
                gotoActivity(LedDetection.class, false);
                break;
            case R.id.nfc:
                gotoActivity(NfcDetection.class, false);
                break;
            case R.id.net_work:
                gotoActivity(SetNetWorkActivity.class, false);
                break;
            case R.id.finder:
                gotoActivity(FinderDetection.class, false);
                break;
        }
    }
}
