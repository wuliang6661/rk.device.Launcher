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
import rk.device.launcher.utils.TypeTranUtils;
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
    @Bind(R.id.ll_button)
    LinearLayout                buttonLL;
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
        readFingerInfo();
        checkIsDetail();
    }

    private void initView() {
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
                                    finish();
                                }
                            });
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
                addFinger();
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
        String title;
        if (!TextUtils.isEmpty(eUser.getCardNo())) {
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
        Message msg = new Message();
        msg.what = MSG_FINGER_ADD;
        fingerAddHandler.sendMessage(msg);
    }

    /**
     * 发起读取指纹头信息指令
     */
    private void readFingerInfo() {
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
                    doReadFingerInfo();
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
            String resultCode = FingerHelper.JNIFpDelUserByID(uId);
            if (TypeTranUtils.str2Int(resultCode) == FingerConstant.SUCCESS) {
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
     * 读取指纹头信息
     */
    private void doReadFingerInfo() {
        String moduleInfo = FingerHelper.JNIFpGetModuleInfo();
        Log.i(TAG, TAG + " finger moduleInfo:" + moduleInfo);
    }

    /**
     * 添加指纹（读取指纹头录入指纹信息）
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
            fingerId = resultCode;
            isChange = true;
            Log.i(TAG, TAG + " finger add success:" + resultCode);
        }
    }

    /**
     * 保存指纹到本地
     */
    private void doSaveFinger() {
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        if (eUser != null) {
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
