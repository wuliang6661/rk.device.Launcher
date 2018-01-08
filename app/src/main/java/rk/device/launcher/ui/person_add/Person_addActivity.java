package rk.device.launcher.ui.person_add;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.nfcadd.NfcaddActivity;
import rk.device.launcher.ui.personface.PersonFaceActivity;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;

/**
 * MVPPlugin
 * <p>
 * 增加用户界面
 */

public class Person_addActivity
        extends MVPBaseActivity<Person_addContract.View, Person_addPresenter>
        implements Person_addContract.View, View.OnClickListener {

    @Bind(R.id.et_person_name)
    EditText     etPersonName;
    @Bind(R.id.tv_time_start)
    TextView     tvTimeStart;
    @Bind(R.id.ll_set_time)
    LinearLayout llSetTime;
    @Bind(R.id.tv_time_end)
    TextView     tvTimeEnd;
    @Bind(R.id.time_end)
    LinearLayout timeEnd;
    @Bind(R.id.face_text)
    TextView     faceText;
    @Bind(R.id.pass_message)
    TextView     passMessage;
    @Bind(R.id.pass_text)
    TextView     passText;
    @Bind(R.id.card_message)
    TextView     cardMessage;
    @Bind(R.id.card_text)
    TextView     cardText;
    @Bind(R.id.finger_text01)
    TextView     fingerText01;
    @Bind(R.id.finger_text02)
    TextView     fingerText02;
    @Bind(R.id.finger_text03)
    TextView     fingerText03;

    User user;

    @Override
    protected int getLayout() {
        return R.layout.act_person_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("添加用户");
        setOnClick(R.id.face_layout, R.id.pass_layout, R.id.card_layout, R.id.finger_layout01,
                R.id.finger_layout02, R.id.finger_layout03, R.id.btn_finish_setting, R.id.iv_back);
        llSetTime.setOnClickListener(this);
        timeEnd.setOnClickListener(this);
        initView();
    }


    /**
     * 初始化界面显示
     */
    private void initView() {
        tvTimeStart.setText(TimeUtils.getTime());
        tvTimeEnd.setText(TimeUtils.getTridTime());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:    //返回的话数据不保存
                finish();
                break;
            case R.id.face_layout:    //录入人脸
                if (isHasName()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", user.getUniqueId());
                    gotoActivity(PersonFaceActivity.class, bundle, false);
                }
                break;
            case R.id.pass_layout:    //录入密码
                if (isHasName()) {
                }
                break;
            case R.id.card_layout: //录入卡
                gotoActivity(NfcaddActivity.class, false);
                break;
            case R.id.finger_layout01: //录入指纹
            case R.id.finger_layout02:
            case R.id.finger_layout03:

                break;
            case R.id.btn_finish_setting: //完成设置

                break;
            case R.id.ll_set_time:    //开始时间
                String startTime = tvTimeStart.getText().toString().trim();
                SetFullTimeDialogFragment fragment = SetFullTimeDialogFragment.newInstance();
                fragment.setSelectedTime(TimeUtils.stringToYear(startTime), TimeUtils.stringToMonth(startTime), TimeUtils.stringToDay(startTime),
                        TimeUtils.stringToHour(startTime), TimeUtils.stringToMounth(startTime));
                fragment.setOnConfirmDialogListener((year, month, day, hour, minute) -> {
                    tvTimeStart.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                });
                fragment.show(getSupportFragmentManager(), "");
                break;
            case R.id.time_end:      //结束时间
                String endTime = tvTimeEnd.getText().toString().trim();
                SetFullTimeDialogFragment fragment1 = SetFullTimeDialogFragment.newInstance();
                fragment1.setSelectedTime(TimeUtils.stringToYear(endTime), TimeUtils.stringToMonth(endTime), TimeUtils.stringToDay(endTime),
                        TimeUtils.stringToHour(endTime), TimeUtils.stringToMounth(endTime));
                fragment1.setOnConfirmDialogListener((year, month, day, hour, minute) -> {
                    tvTimeEnd.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                });
                fragment1.show(getSupportFragmentManager(), "");
                break;
        }
    }


    /**
     * 判断人名是否存在
     */
    private boolean isHasName() {
        String name = etPersonName.getText().toString().trim();
        if (StringUtils.isEmpty(name)) {
            showMessageDialog("请输入用户名称！");
            return false;
        }
        if (user != null) {
            return true;
        }
        user = new User();
        user.setName(name);
        user.setStartTime(TimeUtils.string2Millis(tvTimeStart.getText().toString().trim()));
        user.setEndTime(TimeUtils.string2Millis(tvTimeEnd.getText().toString().trim()));
        user.setPopedomType("1");
        DbHelper.insertUser(user);
        return true;
    }
}


