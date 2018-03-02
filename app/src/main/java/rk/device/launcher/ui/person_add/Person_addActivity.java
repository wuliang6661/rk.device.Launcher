package rk.device.launcher.ui.person_add;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import peripherals.FingerHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.TokenBo;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.fingeradd.FingeraddActivity;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.ui.nfcadd.NfcaddActivity;
import rk.device.launcher.ui.personface.PersonFaceActivity;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.cache.CacheUtils;
import rk.device.launcher.utils.key.KeyUtils;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.utils.verify.SyncPersonUtils;
import rx.Subscriber;

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
    DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(this);


    @Override
    protected int getLayout() {
        return R.layout.act_person_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.add_person));
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
            setTitle(getString(R.string.update_person));
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
                    Log.i("SyncPersonUtils","SyncPersonUtils btn_finish_setting");
                    SyncPersonUtils.getInstance().syncPerosn();
                }
                finish();
                break;
            case R.id.ll_set_time:    //开始时间
                getTimeDialog(tvTimeStart, 0);
                break;
            case R.id.time_end:      //结束时间
                getTimeDialog(tvTimeEnd, 1);
                break;
            case R.id.title_right:     //删除用户
                showMessageDialog(getString(R.string.delete_person_message), getString(R.string.confirm), v -> {
                    doDeleteLocalUser();
                    obtainToken();
                });
                break;
        }
    }

    /**
     * 删除本地用户
     */
    private void doDeleteLocalUser() {
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
        user.setStatus(Constant.TO_BE_DELETE);
        DbHelper.update(user);
        dissmissMessageDialog();
        finish();
    }

    /**
     * 删除用户
     */
    private void doDeleteUser() {
        JSONObject params = new JSONObject();
        try {
            params.put("access_token",SPUtils.getString(Constant.ACCENT_TOKEN));
            params.put("uuid",deviceUuidFactory.getUuid());
            params.put("peopleId",user.getUniqueId());
        } catch (JSONException e) {
        }
        BaseApiImpl.deleteUser(params).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object Object) {
                DbHelper.delete(user);
                T.showShort("删除成功");
            }
        });
    }

    /**
     * token请求接口
     */
    private void obtainToken() {
        BaseApiImpl.postToken(deviceUuidFactory.getUuid().toString(), KeyUtils.getKey())
                .subscribe(new Subscriber<TokenBo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TokenBo tokenBo) {
                        SPUtils.put(Constant.ACCENT_TOKEN, tokenBo.getAccess_token());
                        doDeleteUser();
                    }
                });
    }

    /**
     * 判断人名是否存在
     */
    private boolean isHasName() {
        String name = etPersonName.getText().toString().trim();
        if (StringUtils.isEmpty(name)) {
            showMessageDialog(getString(R.string.please_edit_personname));
            return false;
        }
        if (user != null) {
            return true;
        }
        user = new User();
        user.setName(name);
        user.setStartTime(TimeUtils.string2Millis(tvTimeStart.getText().toString().trim()));
        user.setEndTime(TimeUtils.string2Millis(tvTimeEnd.getText().toString().trim()));
        user.setRole(Constant.USER_TYPE_OPEN_ONLY);
        user.setStatus(Constant.TO_BE_ADD);
        DbHelper.insertUser(user);
        //新增
        SyncPersonUtils.getInstance().syncPerosn();
        return false;
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
                    showMessageDialog(getString(R.string.endtime_starttime));
                    return;
                }
            } else {
                if (selTime >= endTime) {
                    showMessageDialog(getString(R.string.starttime_endtime));
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
            showDialogFragment(getString(R.string.kaimen_pwd), content -> {
                if (!StringUtils.isEmpty(content)) {
                    if (content.length() != 6) {
                        dialogFragment.showError(getString(R.string.edit_pwd_hint));
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
                    dialogFragment.showError(getString(R.string.pwd_ishave));
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
        dialogFragment.showHite(getString(R.string.edit_pwd_hint));
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
            passMessage.setText(R.string.pwd_all);
            passText.setText(R.string.add_suress);
        } else {
            passMessage.setText(R.string.pwd_null);
            passText.setText(R.string.no_add);
            passLayout.setBackgroundColor(Color.parseColor("#30374b"));
        }
        if (!StringUtils.isEmpty(user.getCardNo())) {
            cardMessage.setText(String.valueOf(getString(R.string.card_num) + user.getCardNo()));
        } else {
            cardMessage.setText(R.string.card_null);
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
            return getString(R.string.no_add);
        } else {
            view.setBackgroundColor(Color.parseColor("#302d85"));
            return getString(R.string.add_suress);
        }
    }
}


