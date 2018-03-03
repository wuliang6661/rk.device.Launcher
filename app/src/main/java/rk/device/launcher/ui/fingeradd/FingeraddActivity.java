package rk.device.launcher.ui.fingeradd;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import peripherals.FingerConstant;
import peripherals.FingerHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.FingerRegisterProgressEvent;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.fragment.InputWifiPasswordDialogFragment;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.TypeTranUtils;
import rk.device.launcher.utils.WindowManagerUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.verify.VerifyUtils;
import rx.Subscriber;

/**
 * MVPPlugin 邮箱 784787081@qq.com
 */

public class FingeraddActivity extends MVPBaseActivity<FingeraddContract.View, FingeraddPresenter>
        implements FingeraddContract.View, View.OnClickListener {
    private static final int    MSG_FINGER_ADD       = 1001;               //添加指纹信息
    private static final int    MSG_READ_FINGER_INFO = 1002;               //读取指纹信息
    private static final int    MSG_DELETE_FINGER    = 1003;               //删除指纹信息
    private static final String EXTRA_UNIQUEID       = "uniqueId";
    private static final String EXTRA_NUMBER         = "number";
    private static final String TAG                  = "FingerAddActivity";
    private static final String FINGER_ERROR         = "fingerError";      //录入失败
    @Bind(R.id.ll_button)
    LinearLayout                buttonLL;
    @Bind(R.id.ll_finger_notice)
    LinearLayout                fingerNoticeLL;
    @Bind(R.id.btn_add_finger)
    Button                      addFingerBtn;
    @Bind(R.id.tv_notice)
    TextView                    noticeTv;
    @Bind(R.id.tv_save)
    TextView                    saveTv;
    @Bind(R.id.iv_search)
    ImageView                   deleteImg;                                 //删除按钮
    @Bind(R.id.img_finger)
    ImageView                   fingerImg;
    private String              uniqueId             = "";
    private int                 number               = 0;
    private FingerAddHandler    fingerAddHandler     = null;
    private String              fingerId             = "";
    private boolean             isDetail             = false;
    private boolean             isChange             = false;
    private boolean             isAdd                = false;
    private String              fingerName           = null;

    @Override
    protected int getLayout() {
        return R.layout.activity_finger_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        LauncherApplication.sIsFingerAdd = 1;
        uniqueId = getIntent().getStringExtra(EXTRA_UNIQUEID);
        number = getIntent().getIntExtra(EXTRA_NUMBER, 0);
        checkIsDetail();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FingerHelper.JNIFpGetRemainSpace(LauncherApplication.fingerModuleID);
            }
        }).start();
    }

    private void initView() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) fingerNoticeLL.getLayoutParams();
        int height = WindowManagerUtils.getWindowHeight(this);
        lp.setMargins(0, height / 4, 0, 0);
        fingerNoticeLL.setLayoutParams(lp);
        registerRxBus();
        setOnClick(R.id.btn_add_finger, R.id.tv_rename, R.id.tv_save, R.id.iv_search);
        goBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange) {
                    showMessageDialog(
                            getResources().getString(R.string.notice_exist_finger_not_save),
                            getResources().getString(R.string.sure), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doDeleteJniFinger(TypeTranUtils.str2Int(fingerId));
                                    finish();
                                }
                            });
                } else {
                    finish();
                }
            }
        });
        initHandler();
    }

    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(FingerRegisterProgressEvent.class)
                .subscribe(new Subscriber<FingerRegisterProgressEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(FingerRegisterProgressEvent fingerRegisterProgressEvent) {
                        int progress = fingerRegisterProgressEvent.progress;
                        LogUtil.i(TAG, TAG + " progress:" + progress);
                        showAnimation(progress);
                    }
                }));
    }

    private AnimationDrawable animationDrawable;

    private void showAnimation(int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (progress) {
                    case 0:
                        fingerImg.clearAnimation();
                        fingerImg.setImageDrawable(
                                getResources().getDrawable(R.drawable.finger_add_01));
                        break;
                    case 1:
                        fingerImg.setImageResource(R.drawable.animation_finger_add_one);
                        animationDrawable = (AnimationDrawable) fingerImg.getDrawable();
                        animationDrawable.start();
                        break;
                    case 2:
                        fingerImg.setImageResource(R.drawable.animation_finger_add_two);
                        animationDrawable = (AnimationDrawable) fingerImg.getDrawable();
                        animationDrawable.start();
                        break;
                    case 3:
                        fingerImg.setImageResource(R.drawable.animation_finger_add_three);
                        animationDrawable = (AnimationDrawable) fingerImg.getDrawable();
                        animationDrawable.start();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_finger:
                LogUtil.i(TAG, TAG + " finger add");
                /**
                 * 新增指纹
                 *
                 * @step 判断指纹头能否录入指纹
                 * @step 录入指纹
                 */
                if (checkFingerModular()) {
                    if (!isDetail) {
                        noticeTv.setBackground(null);
                        noticeTv.setText(getString(R.string.finger_add_notice));
                        showAnimation(0);
                        readFingerInfo();
                    } else {
                        doSaveFinger(true);
                    }
                }
                break;
            case R.id.tv_rename:
                doSaveFinger(true);
                break;
            case R.id.tv_save://保存
                doSaveFinger(false);
                break;
            case R.id.iv_search://删除
                if (checkFingerModular()) {
                    showMessageDialog(getResources().getString(R.string.notice),
                            getResources().getString(R.string.notice_delete_finger),
                            getResources().getString(R.string.sure), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteFinger();
                                }
                            });
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LauncherApplication.sIsFingerAdd = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LauncherApplication.sIsFingerAdd = 1;
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        HandlerThread thread = new HandlerThread("new_thread");
        thread.start();
        Looper looper = thread.getLooper();
        fingerAddHandler = new FingerAddHandler(looper);
    }

    /**
     * 判断当前是添加还是详情
     */
    private void checkIsDetail() {
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        switch (number) {
            case 1:
                fingerId = eUser.getFingerID1();
                fingerName = eUser.getFingerName1();
                break;
            case 2:
                fingerId = eUser.getFingerID2();
                fingerName = eUser.getFingerName2();
                break;
            case 3:
                fingerId = eUser.getFingerID3();
                fingerName = eUser.getFingerName3();
                break;
        }
        String title;
        if (!TextUtils.isEmpty(fingerId)) {
            isDetail = true;
            deleteImg.setVisibility(View.VISIBLE);
            deleteImg.setImageDrawable(getResources().getDrawable(R.mipmap.delete));
            buttonLL.setVisibility(View.VISIBLE);
            saveTv.setVisibility(View.GONE);
            noticeTv.setText(fingerName);
            title = getString(R.string.title_finger_detail);
        } else {
            isDetail = false;
            deleteImg.setVisibility(View.GONE);
            buttonLL.setVisibility(View.GONE);
            title = getString(R.string.title_finger_add);
        }
        setTitle(title);
    }

    /**
     * 发起添加指纹指令
     */
    private void addFinger() {
        LogUtil.i(TAG, TAG + " finger addFinger");
        Message msg = new Message();
        msg.what = MSG_FINGER_ADD;
        fingerAddHandler.sendMessageDelayed(msg, 500);
    }

    /**
     * 发起判断是否能够录入指纹指令
     */
    private void readFingerInfo() {
        LogUtil.i(TAG, TAG + " finger readFingerInfo");
        Message msg = new Message();
        msg.what = MSG_READ_FINGER_INFO;
        fingerAddHandler.sendMessage(msg);
    }

    /**
     * 发起删除指纹指令
     */
    private void deleteFinger() {
        Message msg = new Message();
        msg.what = MSG_DELETE_FINGER;
        fingerAddHandler.sendMessageDelayed(msg, 500);
    }

    class FingerAddHandler extends Handler {
        private FingerAddHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FINGER_ADD://录入指纹
                    doAddFinger();
                    break;
                case MSG_READ_FINGER_INFO://判断是否能够录入指纹
                    LogUtil.i(TAG, TAG + " finger MSG_READ_FINGER_INFO");
                    checkCanAddFinger();
                    break;
                case MSG_DELETE_FINGER://删除指纹信息
                    doDeleteFinger();
                    break;
            }
        }
    }

    /**
     * 删除指纹信息
     *
     * @step 1 先删除指纹头中的指纹信息
     * @step 2 再删除本地数据库中的相关信息
     */
    private void doDeleteFinger() {
        if (TextUtils.isEmpty(uniqueId) || uniqueId == null) {
            showToastMsg(getResources().getString(R.string.illeagel_user_not_exist));
            return;
        }
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        int uId = 0;
        if (eUser != null) {
            switch (number) {
                case 1:
//                    rk.device.launcher.db.FingerHelper
                    uId = TypeTranUtils.str2Int(eUser.getFingerID1());
                    eUser.setFingerID1("");
                    break;
                case 2:
                    uId = TypeTranUtils.str2Int(eUser.getFingerID2());
                    eUser.setFingerID2("");
                    break;
                case 3:
                    uId = TypeTranUtils.str2Int(eUser.getFingerID3());
                    eUser.setFingerID3("");
                    break;
            }
            if (doDeleteJniFinger(uId)) {
                eUser.setUploadStatus(0);
                DbHelper.update(eUser);
                showToastMsg(getResources().getString(R.string.delete_success));
                finish();
            } else {
                showToastMsg(getResources().getString(R.string.delete_fail));
            }
        } else {
            showToastMsg(getResources().getString(R.string.illeagel_user_not_exist));
        }
    }

    private void showToastMsg(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.showShort(msg);
            }
        });
    }

    /**
     * 删除指纹头中的指纹
     * 
     * @param uId
     * @return
     */
    private boolean doDeleteJniFinger(int uId) {
        int resultCode = FingerHelper.JNIFpDelUserByID(LauncherApplication.fingerModuleID, uId);
        if (resultCode == FingerConstant.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断能否录入指纹
     */
    private boolean checkCanAddFinger() {
        int remainSpace = FingerHelper.JNIFpGetRemainSpace(LauncherApplication.fingerModuleID);
        LogUtil.i(TAG, TAG + " JNIFpGetRemainSpace :" + remainSpace);
        if (remainSpace <= 0) {
            showToastMsg(getString(R.string.illeagel_finger_number_overflow));
            return false;
        } else {
            addFinger();
            return true;
        }
    }

    /**
     * 添加指纹（读取指纹头录入指纹信息）
     * 
     * @step 1 录入之前需要判断是否已经存在于该指纹头中
     * @step 1.1 如果已存在，判断该手指是否已经绑定用户
     * @step 1.2 如果没有，删除该指纹，重新录入
     * @step 2 手指还未录入的情况下，才能录入该指纹头
     */
    private void doAddFinger() {
        LogUtil.i(TAG, TAG + " finger doAddFinger");
        if (isAdd) {
            return;
        }
        isAdd = true;
        int oFingerId = FingerHelper.JNIFpFingerMatch(LauncherApplication.fingerModuleID);
        Log.i(TAG, TAG + " JNIFpFingerMatch " + oFingerId);
        //如果指纹已存在
        //step 1 判断该指纹是否已入库 1 提示已存在 0 删除该指纹
        //如果不存在，直接添加
        if (oFingerId > 0) {
            if (VerifyUtils.getInstance().verifyByFinger(oFingerId) == null) {
                if (!doDeleteJniFinger(oFingerId)) {
                    LogUtil.i(TAG, TAG + " delete useless finger error!");
                    isAdd = false;
                    return;
                } else {
                    LogUtil.i(TAG, TAG + " delete useless finger success!");
                    doJniAddFinger();
                }
            } else {
                isAdd = false;
                showNoticeMsg(getString(R.string.notice_finger_existed), false);
                return;
            }
        } else {
            doJniAddFinger();
        }
    }

    /**
     * 注册指纹
     */
    private void doJniAddFinger() {
        int resultCode = FingerHelper.JNIUserRegisterMOFN(LauncherApplication.fingerModuleID);
        LogUtil.i(TAG, TAG + " finger add resultCode:" + resultCode);
        if (resultCode == FingerConstant.TIMEOUT) {
            LogUtil.i(TAG, TAG + " finger add fail:" + resultCode);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isAdd = false;
                    if (addFingerBtn != null) {
                        addFingerBtn.setVisibility(View.VISIBLE);
                        addFingerBtn.setText(getString(R.string.finger_reload));
                        showNoticeMsg(getString(R.string.illeagel_finger_add_timeout), false);
                    }
                }
            });
        } else if (resultCode == FingerConstant.FAIL) {
            LogUtil.i(TAG, TAG + " finger add fail:" + resultCode);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isAdd = false;
                    if (addFingerBtn != null) {
                        addFingerBtn.setVisibility(View.VISIBLE);
                        addFingerBtn.setText(getString(R.string.finger_reload));
                        showNoticeMsg(getString(R.string.illeagel_finger_add_incomplete), false);
                    }
                }
            });
        } else if (resultCode > 0) {
            fingerId = String.valueOf(resultCode);
            showDialogFragment(getString(R.string.finger) + number,
                    new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick(String content) {
                            fingerName = content;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (content.length() == 0 || content.length() > 10) {
                                        T.showShort(getString(R.string.finger_name_rule));
                                        return;
                                    }
                                    addFingerBtn.setVisibility(View.GONE);
                                    buttonLL.setVisibility(View.VISIBLE);
                                    saveTv.setVisibility(View.VISIBLE);
                                    showNoticeMsg(getString(R.string.finger_add_success), true);
                                    dialogFragment.dismiss();
                                }
                            });
                        }
                    });
            dialogFragment.show(getSupportFragmentManager(), "");
            isChange = true;
            isAdd = false;
            LogUtil.i(TAG, TAG + " finger add success:" + resultCode);
        }
    }

    /**
     * 消息
     * 
     * @param msg
     */
    private void showNoticeMsg(String msg, boolean isSuccess) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    noticeTv.setText(msg);
                    noticeTv.setBackground(
                            getResources().getDrawable(R.drawable.shape_finger_add_success));
                } else {
                    noticeTv.setText(msg);
                    noticeTv.setBackground(
                            getResources().getDrawable(R.drawable.shape_finger_add_fail));
                }
            }
        });
    }

    private InputWifiPasswordDialogFragment dialogFragment = null;

    /**
     * 显示输入管理员密码弹窗
     */
    private void showDialogFragment(String content,
                                    InputWifiPasswordDialogFragment.OnConfirmClickListener listener) {
        if (dialogFragment == null) {
            dialogFragment = InputWifiPasswordDialogFragment.newInstance();
        }
        dialogFragment.setTitle(getString(R.string.notice_add_new_finger_name));
        dialogFragment.showHite(getString(R.string.notice_add_finger_name));
        dialogFragment.setContent(content);
        dialogFragment.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogFragment.setOnCancelClickListener(() -> dialogFragment.dismiss())
                .setOnConfirmClickListener(listener);
    }

    /**
     * 保存指纹到本地
     */
    private void doSaveFinger(boolean isRename) {
        LogUtil.i(TAG, TAG + " finger doSaveFinger");
        if (fingerId.equals(FINGER_ERROR)) {
            showToastMsg(getResources().getString(R.string.finger_add_error));
            return;
        }
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        if (eUser != null) {
            //如果是详情，先删除原来的指纹
            if (isDetail && !isRename) {
                deleteFinger();
            }
            if (isRename) {
                showDialogFragment(fingerName,
                        new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(String content) {
                                if (content.length() == 0 || content.length() > 10) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            T.showShort(getString(R.string.finger_name_rule));
                                        }
                                    });
                                    return;
                                }
                                noticeTv.setText(content);
                                fingerName = content;
                                updateUser(eUser);
                                dialogFragment.dismiss();
                            }
                        });
                dialogFragment.show(getSupportFragmentManager(), "");
            } else {
                updateUser(eUser);
            }
        } else {
            showToastMsg(getResources().getString(R.string.illeagel_user_not_exist));
        }
    }

    /**
     * 更新用户数据
     * 
     * @param eUser
     */
    private void updateUser(User eUser) {
        switch (number) {
            case 1:
                eUser.setFingerID1(fingerId);
                eUser.setFingerName1(
                        fingerName == null ? getString(R.string.finger) + "1" : fingerName);
                break;
            case 2:
                eUser.setFingerID2(fingerId);
                eUser.setFingerName2(
                        fingerName == null ? getString(R.string.finger) + "2" : fingerName);
                break;
            case 3:
                eUser.setFingerID3(fingerId);
                eUser.setFingerName3(
                        fingerName == null ? getString(R.string.finger) + "3" : fingerName);
                break;
        }
        eUser.setUploadStatus(0);
        DbHelper.update(eUser);
        showToastMsg(getString(R.string.finger_add_success));
        finish();
    }

    private boolean checkFingerModular() {
        if (LauncherApplication.sInitFingerSuccess == -1) {
            showMessageDialog(getString(R.string.notice_finger_add));
            LogUtil.i(TAG, TAG + " finger init failed.");
            return false;
        }
        return true;
    }
}
