package rk.device.launcher.ui.person_add;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * <p>
 * 增加用户界面
 */

public class Person_addActivity extends MVPBaseActivity<Person_addContract.View, Person_addPresenter>
        implements Person_addContract.View, View.OnClickListener {

    @Bind(R.id.et_person_name)
    EditText etPersonName;
    @Bind(R.id.tv_time_start)
    TextView tvTimeStart;
    @Bind(R.id.ll_set_time)
    LinearLayout llSetTime;
    @Bind(R.id.tv_time_end)
    TextView tvTimeEnd;
    @Bind(R.id.time_end)
    LinearLayout timeEnd;
    @Bind(R.id.face_text)
    TextView faceText;
    @Bind(R.id.pass_message)
    TextView passMessage;
    @Bind(R.id.pass_text)
    TextView passText;
    @Bind(R.id.card_message)
    TextView cardMessage;
    @Bind(R.id.card_text)
    TextView cardText;
    @Bind(R.id.finger_text01)
    TextView fingerText01;
    @Bind(R.id.finger_text02)
    TextView fingerText02;
    @Bind(R.id.finger_text03)
    TextView fingerText03;

    @Override
    protected int getLayout() {
        return R.layout.act_person_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle("添加用户");
        setOnClick(R.id.face_layout, R.id.pass_layout, R.id.card_layout, R.id.finger_layout01,
                R.id.finger_layout02, R.id.finger_layout03, R.id.btn_finish_setting);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.face_layout:    //录入人脸

                break;
            case R.id.pass_layout:    //录入密码

                break;
            case R.id.card_layout:    //录入卡

                break;
            case R.id.finger_layout01:    //录入指纹
            case R.id.finger_layout02:
            case R.id.finger_layout03:

                break;
            case R.id.btn_finish_setting:     //完成设置

                break;
        }
    }
}
