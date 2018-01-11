package rk.device.launcher.ui.fingeradd;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.Bind;
import peripherals.FingerConstant;
import peripherals.FingerHelper;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.FingerInfoModel;
import rk.device.launcher.bean.event.FingerRegisterProgressEvent;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.SPUtils;
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
    private static final int    READ_NEED_ADD        = 1;                  //删除指纹信息
    private static final int    READ_NOT_NEED_ADD    = 0;                  //删除指纹信息
    private static final String EXTRA_UNIQUEID       = "uniqueId";
    private static final String EXTRA_NUMBER         = "number";
    private static final String TAG                  = "FingerAddActivity";
    private static final String FINGER_ERROR         = "fingerError";      //录入失败
    private static final String EXTRA_MAXUSER        = "maxUser";          //最大用户数
    @Bind(R.id.ll_button)
    LinearLayout                buttonLL;
    @Bind(R.id.ll_finger_notice)
    LinearLayout                fingerNoticeLL;
    @Bind(R.id.btn_add_finger)
    Button                      addFingerBtn;
    @Bind(R.id.tv_notice)
    TextView                    noticeTv;
    @Bind(R.id.iv_search)
    ImageView                   deleteImg;                                 //删除按钮
    private String              uniqueId             = "";
    private int                 number               = 0;
    private FingerAddHandler    fingerAddHandler     = null;
    private String              fingerId             = "";
    private boolean             isDetail             = false;
    private boolean             isChange             = false;
    private int                 maxUser              = 0;
    private int                 currUser             = 0;
    private boolean             isAdd                = false;

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
        readFingerInfo(READ_NOT_NEED_ADD);
        checkIsDetail();
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
                                    if (doDeleteJniFinger(TypeTranUtils.str2Int(fingerId))) {
                                        finish();
                                    } else {
                                        T.showShort("delete jni finger error");
                                    }
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
                        Log.i(TAG, TAG + " fingerRegister number:"
                                + fingerRegisterProgressEvent.progress);
                    }
                }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_finger://新增 or 修改
                readFingerInfo(READ_NEED_ADD);
                break;
            case R.id.tv_rename:

                break;
            case R.id.tv_save://保存
                doSaveFinger();
                break;
            case R.id.iv_search://删除
                showMessageDialog(getResources().getString(R.string.notice_delete_finger),
                        getResources().getString(R.string.sure), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteFinger();
                            }
                        });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LauncherApplication.sIsFingerAdd = 0;
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
                break;
            case 2:
                fingerId = eUser.getFingerID2();
                break;
            case 3:
                fingerId = eUser.getFingerID3();
                break;
        }
        String title;
        if (!TextUtils.isEmpty(fingerId)) {
            isDetail = true;
            deleteImg.setVisibility(View.VISIBLE);
            deleteImg.setImageDrawable(getResources().getDrawable(R.mipmap.delete));
            title = getString(R.string.title_finger_detail);
        } else {
            isDetail = false;
            deleteImg.setVisibility(View.GONE);
            title = getString(R.string.title_finger_add);
        }
        setTitle(title);
    }

    /**
     * 发起添加指纹指令
     */
    private void addFinger() {
        if (isAdd) {
            return;
        }
        isAdd = true;
        Message msg = new Message();
        msg.what = MSG_FINGER_ADD;
        fingerAddHandler.sendMessage(msg);
    }

    /**
     * 发起读取指纹头信息指令
     */
    private void readFingerInfo(int needAdd) {
        Message msg = new Message();
        msg.what = MSG_READ_FINGER_INFO;
        msg.arg1 = needAdd;
        fingerAddHandler.sendMessage(msg);
    }

    /**
     * 发起删除指纹指令
     */
    private void deleteFinger() {
        Message msg = new Message();
        msg.what = MSG_DELETE_FINGER;
        fingerAddHandler.sendMessage(msg);
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
                case MSG_READ_FINGER_INFO://获取指纹头信息
                    doReadFingerInfoOrCheckCanAdd(msg.arg1);
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
            T.showShort(getString(R.string.illeagel_user_not_exist));
            return;
        }
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        int uId = 0;
        if (eUser != null) {
            switch (number) {
                case 1:
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
                DbHelper.update(eUser);
                T.showShort(getResources().getString(R.string.delete_success));
                finish();
            } else {
                T.showShort(getResources().getString(R.string.delete_fail));
            }
        } else {
            T.showShort(getResources().getString(R.string.illeagel_user_not_exist));
        }
    }

    /**
     * 删除指纹头中的指纹
     * 
     * @param uId
     * @return
     */
    private boolean doDeleteJniFinger(int uId) {
        String resultCode = FingerHelper.JNIFpDelUserByID(uId);
        if (TypeTranUtils.str2Int(resultCode) == FingerConstant.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 读取指纹头信息 or 判断能否录入
     */
    private boolean doReadFingerInfoOrCheckCanAdd(int needAdd) {
        String moduleInfo = FingerHelper.JNIFpGetModuleInfo();
        Log.i(TAG, TAG + " read moduleInfo:" + moduleInfo);
        FingerInfoModel fingerInfoModel = new Gson().fromJson(moduleInfo, FingerInfoModel.class);
        if (fingerInfoModel.getMaxUser() == 0) {
            maxUser = SPUtils.getInt(EXTRA_MAXUSER);
        } else {
            SPUtils.put(EXTRA_MAXUSER, fingerInfoModel.getMaxUser());
        }
        maxUser = fingerInfoModel.getMaxUser();
        currUser = TypeTranUtils.str2Int(FingerHelper.JNIFpGetTotalUser());
        Log.i(TAG, TAG + " read JNIFpGetTotalUser currUserNumber:" + currUser);
        if (currUser == -1) {
            Log.i(TAG, TAG + " read JNIFpGetTotalUser error!");
            return false;
        }
        if (currUser >= maxUser) {
            T.showShort("指纹头指纹信息已经录满，请清理指纹头中指纹信息");
            return false;
        }
        if (needAdd == READ_NEED_ADD) {
            addFinger();
        }
        return true;
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
        String resultCode = FingerHelper.JNIUserRegisterMOFN();
        Log.i(TAG, TAG + " finger add resultCode:" + resultCode);
        if (resultCode.equals("-2") || resultCode.equals("-1")) {
            Log.i(TAG, TAG + " finger add fail:" + resultCode);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addFingerBtn.setText("重新录入");
                }
            });
        } else {
            String[] fingerArr = resultCode.split("#");
            fingerId = fingerArr.length == 2 ? fingerArr[1] : FINGER_ERROR;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addFingerBtn.setVisibility(View.GONE);
                    buttonLL.setVisibility(View.VISIBLE);
                }
            });
            isChange = true;
            Log.i(TAG, TAG + " finger add success:" + resultCode);
        }
        isAdd = false;
    }

    /**
     * 保存指纹到本地
     */
    private void doSaveFinger() {
        if (fingerId.equals(FINGER_ERROR)) {
            T.showShort(getResources().getString(R.string.finger_add_error));
            return;
        }
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        if (eUser != null) {
            //如果是详情，先删除原来的指纹
            if (isDetail) {
                deleteFinger();
            }
            switch (number) {
                case 1:
                    eUser.setFingerID1(fingerId);
                    break;
                case 2:
                    eUser.setFingerID2(fingerId);
                    break;
                case 3:
                    eUser.setFingerID3(fingerId);
                    break;
            }

            DbHelper.update(eUser);
            T.showShort("已成功录入指纹");
            finish();
        } else {
            T.showShort(getResources().getString(R.string.illeagel_user_not_exist));
        }
    }

}
