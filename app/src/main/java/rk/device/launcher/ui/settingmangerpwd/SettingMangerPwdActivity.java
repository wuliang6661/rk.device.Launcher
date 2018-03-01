package rk.device.launcher.ui.settingmangerpwd;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.setting.SetDoorGuardActivity;
import rk.device.launcher.utils.IMEUtils;
import rk.device.launcher.utils.SPUtils;


/**
 * MVPPlugin
 * <p>
 * 设置管理员密码
 */

public class SettingMangerPwdActivity extends MVPBaseActivity<SettingMangerPwdContract.View, SettingMangerPwdPresenter>
        implements SettingMangerPwdContract.View, View.OnClickListener {

    @Bind(R.id.finnish)
    TextView finnish;
    @Bind(R.id.text1)
    TextView text1;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.text5)
    TextView text5;
    @Bind(R.id.text6)
    TextView text6;
    @Bind(R.id.btn_finish)
    Button btnFinish;
    @Bind(R.id.ed_pass)
    EditText edPass;
    @Bind(R.id.text_layout)
    LinearLayout textLayout;

    String etPass;
    TextView texts[];


    @Override
    protected int getLayout() {
        return R.layout.act_setmanger_pwd;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        edPass.setAlpha(0);
        texts = new TextView[]{text1, text2, text3, text4, text5, text6};
        finnish.setOnClickListener(this);
        btnFinish.setEnabled(false);
        btnFinish.setOnClickListener(this);
        textLayout.setOnClickListener(this);
        setEditListener();
    }


    /**
     * 设置输入框的事件监听
     */
    private void setEditListener() {
        edPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etPass = s.toString();
                for (int i = 0; i < 6; i++) {
                    if (edPass.length() - 1 >= i) {
                        texts[i].setText(etPass.charAt(i) + "");
                    } else {
                        texts[i].setText("");
                    }
                }
                if (etPass.length() == texts.length) {
                    btnFinish.setEnabled(true);
                } else {
                    btnFinish.setEnabled(false);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finnish:
                finish();
                break;
            case R.id.btn_finish:
                if (etPass.length() == 6) {
                    SPUtils.putString(Constant.KEY_PASSWORD, etPass); //缓存密码
                    SPUtils.put(Constant.SETTING_NUM, Constant.SETTING_TYPE4);
                    gotoActivity(SetDoorGuardActivity.class, true);
                }
                break;
            case R.id.text_layout:
                IMEUtils.showIME(this, edPass);
                new Handler().postDelayed(() -> edPass.setSelection(edPass.getText().toString().length()), 300);
                break;
        }
    }


}
