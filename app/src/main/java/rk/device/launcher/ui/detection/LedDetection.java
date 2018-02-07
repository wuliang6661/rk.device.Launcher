package rk.device.launcher.ui.detection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import butterknife.Bind;
import peripherals.LedHelper;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.SPUtils;

/**
 * Created by wuliang on 2018/2/5.
 * <p>
 * LED灯检测
 */

public class LedDetection extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.cb_light)
    CheckBox cbLight;
    @Bind(R.id.btn_finish_setting)
    Button btnFinishSetting;

    @Override
    protected int getLayout() {
        return R.layout.act_led_detection;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle("补光灯检测");
        btnFinishSetting.setOnClickListener(this);

        cbLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                LedHelper.PER_ledToggle(1);
            } else {
                LedHelper.PER_ledToggle(0);
            }
        });
    }

    /**
     * 下一步,状态还原,不保存测试状态
     */
    @Override
    public void onClick(View v) {
        finish();
    }


    @Override
    protected void onDestroy() {
        // 读取保存的补光灯的开关状态
        boolean isLightTurnOn = SPUtils.getBoolean(Constant.KEY_LIGNT, false);
        if (isLightTurnOn) {
            LedHelper.PER_ledToggle(1);
        } else {
            LedHelper.PER_ledToggle(0);
        }
        super.onDestroy();
    }
}
