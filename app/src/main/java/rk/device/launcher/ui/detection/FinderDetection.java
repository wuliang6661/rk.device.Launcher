package rk.device.launcher.ui.detection;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import peripherals.FingerConstant;
import peripherals.FingerHelper;
import retrofit2.http.HEAD;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.FingerRegisterProgressEvent;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.ResUtil;
import rk.device.launcher.utils.rxjava.RxBus;
import rx.Subscriber;

/**
 * Created by wuliang on 2018/2/6.
 * <p>
 * 指纹模块检测
 */

public class FinderDetection extends BaseActivity implements View.OnClickListener {

    private static final int    MSG_FINGER_ADD       = 1001;             //添加指纹信息
    private static final int    MSG_READ_FINGER_INFO = 1002;             //读取指纹信息
    private static final int    MSG_DELETE_FINGER    = 1003;             //删除指纹信息
    private static final String TAG                  = "FinderDetection";
    @Bind(R.id.btn_add_finger)
    Button                      addFingerBtn;
    @Bind(R.id.tv_notice)
    TextView                    noticeTv;
    @Bind(R.id.img_finger)
    ImageView                   fingerImg;
    private AnimationDrawable   animationDrawable;                       //指纹录入动画
    private FingerAddHandler    fingerAddHandler     = null;
    private int                 fingerId             = 0;
    private boolean             isAdded              = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_finger_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goBack();
        setTitle(getString(R.string.title_finger_check));

        initView();
    }

    private void initView() {
        setOnClick(R.id.btn_add_finger);
        LauncherApplication.sIsFingerAdd = 2;
        registerRxBus();
        initHandler();
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
            case R.id.btn_add_finger://新增指纹
                LogUtil.i(TAG, TAG + " finger add");
                if (!checkFingerModular()) {
                    return;
                }
                if (isAdded) {
                    deleteFinger();
                } else {
                    readFingerInfo();
                }
                break;
        }
    }

    /**
     * 判断是否有指纹模块
     * 
     * @return
     */
    private boolean checkFingerModular() {
        if (LauncherApplication.sInitFingerSuccess == -1) {
            showMessageDialog(getString(R.string.notice_finger_add));
            LogUtil.i(TAG, TAG + " finger init failed.");
            return false;
        }
        return true;
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
                    doDeleteJniFinger(fingerId);
                    break;
            }
        }
    }

    /**
     * 发起判断是否能够录入指纹指令，如果可以就直接录入
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
     * 发起添加指纹指令
     */
    private void addFinger() {
        LogUtil.i(TAG, TAG + " finger addFinger");
        Message msg = new Message();
        msg.what = MSG_FINGER_ADD;
        fingerAddHandler.sendMessageDelayed(msg, 500);
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
        int oFingerId = FingerHelper.JNIFpFingerMatch(LauncherApplication.fingerModuleID);
        LogUtil.i(TAG, TAG + " JNIFpFingerMatch " + oFingerId);
        //如果指纹已存在
        //step 1 判断该指纹是否已入库 1 提示已存在 0 删除该指纹
        //如果不存在，直接添加
        if (oFingerId > 0) {
            if (doDeleteJniFinger(oFingerId)) {
                doJniAddFinger();
            }
        } else {
            doJniAddFinger();
        }
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
            LogUtil.i(TAG, TAG + "doDeleteJniFinger success");
            if (addFingerBtn.getText().toString().equals(getString(R.string.delete_finger))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addFingerBtn.setText(getString(R.string.finger_reload));
                        noticeTv.setText(getString(R.string.finger_add_notice));
                        noticeTv.setBackground(null);
                        showAnimation(0);
                    }
                });
            }
            isAdded = false;
            return true;
        } else {
            LogUtil.i(TAG, TAG + "doDeleteJniFinger failed");
            return false;
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
                    if (addFingerBtn != null) {
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
                    if (addFingerBtn != null) {
                        addFingerBtn.setText(getString(R.string.finger_reload));
                        showNoticeMsg(getString(R.string.illeagel_finger_add_incomplete), false);
                    }
                }
            });
        } else if (resultCode > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fingerId = resultCode;
                    if (addFingerBtn != null) {
                        addFingerBtn.setText(getString(R.string.delete_finger));
                    }
                    showNoticeMsg(getString(R.string.finger_add_success), true);
                    isAdded = true;
                    LogUtil.i(TAG, TAG + " finger add success:" + resultCode);
                }
            });

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

    private void showToastMsg(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.showShort(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LauncherApplication.sIsFingerAdd = 0;
        deleteFinger();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LauncherApplication.sIsFingerAdd = 2;
    }
}
