package rk.device.launcher.ui.key;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.setting.SetBasicInfoActivity;
import rk.device.launcher.ui.setting.SetNetWorkActivity;
import rk.device.launcher.ui.settingmangerpwd.SettingMangerPwdActivity;
import rk.device.launcher.utils.DeviceUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.key.KeyUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;


/**
 * MVPPlugin
 * <p>
 * 激活activity
 */

public class KeyActivity extends MVPBaseActivity<KeyContract.View, KeyPresenter> implements KeyContract.View, View.OnClickListener {

    @Bind(R.id.key_edit)
    EditText keyEdit;
    @Bind(R.id.suress_button)
    Button suressButton;
    @Bind(R.id.go_net)
    TextView goNet;
    @Bind(R.id.error_layout)
    RelativeLayout errorLayout;

    String key;

    @Override
    protected int getLayout() {
        return R.layout.act_key;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        suressButton.setOnClickListener(this);
        goNet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suress_button:
                key = keyEdit.getText().toString().trim();
                if (StringUtils.isEmpty(key) || key.length() < 14) {
                    onRequestError("请输入正确激活码");
                } else {
                    mPresenter.activationDiveces(new DeviceUuidFactory(this).getUuid() + "", DeviceUtils.getMacAddress(), key);
                    showWaitProgress("正在激活...");
                }
                break;
            case R.id.go_net:
                Intent intent = new Intent(this, SetNetWorkActivity.class);
                intent.putExtra("isFinish", true);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRequestError(String msg) {
        hintWaitProgress();
        errorLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> errorLayout.setVisibility(View.GONE), 1500);
    }

    @Override
    public void onRequestEnd() {
        hintWaitProgress();
    }

    @Override
    public void onSuress() {
        hintWaitProgress();
        if (KeyUtils.saveKey(key)) {
            SPUtils.put(Constant.SETTING_NUM, Constant.SETTING_TYPE3);
            gotoActivity(SettingMangerPwdActivity.class, true);
        } else {
            onRequestError("激活流程出现错误，请重新激活");
        }
    }

}
