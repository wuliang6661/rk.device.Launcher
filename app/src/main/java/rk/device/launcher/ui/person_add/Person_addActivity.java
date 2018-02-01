package rk.device.launcher.ui.person_add;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import peripherals.FingerHelper;
import rk.device.launcher.R;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.fingeradd.FingeraddActivity;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.ui.nfcadd.NfcaddActivity;
import rk.device.launcher.ui.personface.PersonFaceActivity;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.cache.CacheUtils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.utils.verify.SyncPersonUtils;

/**
 * MVPPlugin
 * <p>
 * 增加用户界面
 */

public class Person_addActivity
        extends MVPBaseActivity<Person_addContract.View, Person_addPresenter>
        implements Person_addContract.View, View.OnClickListener {

    private static final String EXTRA_UNIQUEID = "uniqueId";
    private static final String EXTRA_NUMBER = "number";
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
    @Bind(R.id.id_text)
    TextView idText;
    @Bind(R.id.id_layout)
    LinearLayout idLayout;
    @Bind(R.id.id_view)
    View idView;
    @Bind(R.id.face_layout)
    RelativeLayout faceLayout;
    @Bind(R.id.pass_layout)
    RelativeLayout passLayout;
    @Bind(R.id.card_layout)
    RelativeLayout cardLayout;
    @Bind(R.id.finger_layout01)
    RelativeLayout fingerLayout01;
    @Bind(R.id.finger_layout02)
    RelativeLayout fingerLayout02;
    @Bind(R.id.finger_layout03)
    RelativeLayout fingerLayout03;

    User user;
    InputWifiPasswordDialogFragment dialogFragment;


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
        user = (User) getIntent().getSerializableExtra("user");
        initView();
    }


    /**
     * 初始化界面显示
     */
    private void initView() {
        if (user != null) {
            idLayout.setVisibility(View.VISIBLE);
            idView.setVisibility(View.VISIBLE);
            setTitle("修改用户");
            idText.setText(String.valueOf(user.getUniqueId()));
            tvTimeStart.setText(TimeUtils.getFormatTimeFromTimestamp(user.getStartTime(), null));
            tvTimeEnd.setText(TimeUtils.getFormatTimeFromTimestamp(user.getEndTime(), null));
            etPersonName.setText(user.getName());
            setRightButton(R.drawable.delete_person, this);
        } else {
            idLayout.setVisibility(View.GONE);
            idView.setVisibility(View.GONE);
            tvTimeStart.setText(TimeUtils.getTime());
            tvTimeEnd.setText(TimeUtils.getTridTime());
        }
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
                addNumPass();
                break;
            case R.id.card_layout: //录入卡
                if (isHasName()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_UNIQUEID, user.getUniqueId());
                    gotoActivity(NfcaddActivity.class, bundle, false);
                }
                break;
            case R.id.finger_layout01: //录入指纹
                if (isHasName()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_UNIQUEID, user.getUniqueId());
                    bundle.putInt(EXTRA_NUMBER, 1);
                    gotoActivity(FingeraddActivity.class, bundle, false);
                }
                break;
            case R.id.finger_layout02:
                if (isHasName()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_UNIQUEID, user.getUniqueId());
                    bundle.putInt(EXTRA_NUMBER, 2);
                    gotoActivity(FingeraddActivity.class, bundle, false);
                }
                break;
            case R.id.finger_layout03:
                if (isHasName()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_UNIQUEID, user.getUniqueId());
                    bundle.putInt(EXTRA_NUMBER, 3);
                    gotoActivity(FingeraddActivity.class, bundle, false);
                }
                break;
            case R.id.btn_finish_setting:     //完成设置
                if (isHasName()) {
                    user.setName(etPersonName.getText().toString().trim());
                    user.setStartTime(TimeUtils.string2Millis(tvTimeStart.getText().toString().trim()));
                    user.setEndTime(TimeUtils.string2Millis(tvTimeEnd.getText().toString().trim()));
                    DbHelper.insertUser(user);
                    SyncPersonUtils.getInstance().syncPerosn();
                    finish();
                }
                break;
            case R.id.ll_set_time:    //开始时间
                getTimeDialog(tvTimeStart, 0);
                break;
            case R.id.time_end:      //结束时间
                getTimeDialog(tvTimeEnd, 1);
                break;
            case R.id.title_right:     //删除用户
                showMessageDialog("是否确认删除该用户\n\n删除后所有录入信息都被删除", "确定", v -> {
                    if (!StringUtils.isEmpty(user.getFaceID())) {    //删除本地人脸
                        FaceUtils faceUtils = FaceUtils.getInstance();
                        faceUtils.delete(user.getFaceID());
                        FileUtils.deleteFile(CacheUtils.getFaceFile() + "/" + user.getFaceID() + ".png");
                    }
                    //删除指纹
                    if (!TextUtils.isEmpty(user.getFingerID1())) {
                        FingerHelper.JNIFpDelUserByID(LauncherApplication.fingerModuleID, TypeTranUtils.str2Int(user.getFingerID1()));
                    }
                    if (!TextUtils.isEmpty(user.getFingerID2())) {
                        FingerHelper.JNIFpDelUserByID(LauncherApplication.fingerModuleID, TypeTranUtils.str2Int(user.getFingerID2()));
                    }
                    if (!TextUtils.isEmpty(user.getFingerID3())) {
                        FingerHelper.JNIFpDelUserByID(LauncherApplication.fingerModuleID, TypeTranUtils.str2Int(user.getFingerID3()));
                    }
                    DbHelper.delete(user);
                    dissmissMessageDialog();
                    finish();
                });
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


    /**
     * 设置时间选择器
     */
    private void getTimeDialog(TextView timeText, int type) {
        String time = timeText.getText().toString().trim();
        SetFullTimeDialogFragment fragment = SetFullTimeDialogFragment.newInstance();
        fragment.setSelectedTime(TimeUtils.stringToFormat(time, "yyyy"), TimeUtils.stringToFormat(time, "MM"), TimeUtils.stringToFormat(time, "dd"),
                TimeUtils.stringToFormat(time, "HH"), TimeUtils.stringToFormat(time, "mm"));
        fragment.setOnConfirmDialogListener((year, month, day, hour, minute) -> {
            long selTime = TimeUtils.string2Millis(year + "-" + month + "-" + day + " " + hour + ":" + minute);
            long staTime = TimeUtils.string2Millis(tvTimeStart.getText().toString());
            long endTime = TimeUtils.string2Millis(tvTimeEnd.getText().toString());
            if (type == 1) {    //结束时间
                if (selTime <= staTime) {
                    showMessageDialog("结束时间必须大于开始时间");
                    return;
                }
            } else {
                if (selTime >= endTime) {
                    showMessageDialog("开始时间必须小于结束时间");
                    return;
                }
            }
            timeText.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
        });
        fragment.show(getSupportFragmentManager(), "");
    }

    /**
     * 录入数字密码
     */
    private void addNumPass() {
        if (isHasName()) {
            showDialogFragment("开门密码", content -> {
                if (!StringUtils.isEmpty(content)) {
                    if (content.length() != 6) {
                        dialogFragment.showError("请输入6位数密码！");
                        return;
                    }
                }
                if (StringUtils.isEmpty(content)) {
                    content = "0";
                }
                List<User> users = DbHelper.queryByPassword(content);
                if (users.isEmpty() || content.equals("0")) {
                    user.setPassWord(Integer.parseInt(content));
                    user.setUploadStatus(0);
                    DbHelper.insertUser(user);
                    dialogFragment.dismiss();
                    loadUser();
                } else {
                    dialogFragment.showError("密码已存在！");
                }
            }, false);
            dialogFragment.show(getSupportFragmentManager(), "");
        }
    }


    /**
     * 显示设置数字密码弹窗
     */
    private void showDialogFragment(String title, InputWifiPasswordDialogFragment.OnConfirmClickListener listener, boolean isHideInput) {
        dialogFragment = InputWifiPasswordDialogFragment.newInstance();
        dialogFragment.setTitle(title);
        dialogFragment.showHite("请输入6位数密码");
        dialogFragment.setMaxLength(6);
        if (isHideInput) {
            dialogFragment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);   //隐藏密码
        } else {
            dialogFragment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);   //显示密码
        }
        dialogFragment.setOnCancelClickListener(() -> dialogFragment.dismiss()).setOnConfirmClickListener(listener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }


    /**
     * 判断User是否存在，查询值并显示
     */
    private void loadUser() {
        if (user == null) {
            return;
        }
        user = DbHelper.queryUserById(user.getUniqueId()).get(0);
        faceText.setText(getType(user.getFaceID(), faceLayout));
        if (user.getPassWord() != 0) {
            passLayout.setBackgroundColor(Color.parseColor("#302d85"));
            passMessage.setText("密码：******");
            passText.setText("已录入");
        } else {
            passMessage.setText("密码：空");
            passText.setText("未录入");
            passLayout.setBackgroundColor(Color.parseColor("#30374b"));
        }
        if (!StringUtils.isEmpty(user.getCardNo())) {
            cardMessage.setText(String.valueOf("卡号：" + user.getCardNo()));
        } else {
            cardMessage.setText("卡号：空");
        }
        cardText.setText(getType(user.getCardNo(), cardLayout));
        fingerText01.setText(getType(user.getFingerID1(), fingerLayout01));
        fingerText02.setText(getType(user.getFingerID2(), fingerLayout02));
        fingerText03.setText(getType(user.getFingerID3(), fingerLayout03));
    }


    /**
     * 判断数据是否存在，返回显示
     */
    private String getType(String message, View view) {
        if (StringUtils.isEmpty(message)) {
            view.setBackgroundColor(Color.parseColor("#30374b"));
            return "未录入";
        } else {
            view.setBackgroundColor(Color.parseColor("#302d85"));
            return "已录入";
        }
    }
}


