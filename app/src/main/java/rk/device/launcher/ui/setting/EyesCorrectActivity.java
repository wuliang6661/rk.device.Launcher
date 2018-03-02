package rk.device.launcher.ui.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import cvc.EventUtil;
import peripherals.LedHelper;
import rk.device.launcher.R;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.fragment.EyesCorrectDialog;
import rk.device.launcher.ui.fragment.EyesCorrectDialogOneFra;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.widget.carema.BackCameraSurfaceView;

/**
 * Created by wuliang on 2017/12/7.
 * <p>
 * 双目校准界面
 */

public class EyesCorrectActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.surfaceview01)
    BackCameraSurfaceView surfaceview01;
    @Bind(R.id.picule_num)
    TextView piculeNum;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.all_num)
    TextView allNum;

    int CountNum = 20;    //校准照片数量默认20张
    int haveCount = 0;    //已成功收集的照片，默认从0开始


    private JniHandler mHandler = null;
    EyesCorrectDialogOneFra dialogOneFra;


    @Override
    protected int getLayout() {
        return R.layout.act_eyes_correct;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        setTitle(getString(R.string.carema_correct));
        openCarmea();

        mHandler = JniHandler.getInstance();
    }

    protected void initData() {
        ivBack.setOnClickListener(this);
        setCorectListener();
        LedHelper.PER_ledToggle(1);
        showDialogOne();
    }


    /**
     * 显示第一个提示弹窗(输入行列)
     */
    private void showDialogOne() {
        dialogOneFra = EyesCorrectDialogOneFra.newInstance();
        dialogOneFra.setCallBack(new EyesCorrectDialogOneFra.CallBack() {
            @Override
            public void callMessage(int line, int lie, int countNum) {
                EyesCorrectActivity.this.CountNum = countNum;
                allNum.setText(String.valueOf("/" + countNum));
                setCorrectWAndH(line, lie);
            }

            @Override
            public void cancle() {
                dialogOneFra.dismiss();
                finish();
            }
        });
        dialogOneFra.show(getSupportFragmentManager(), "");
    }


    /**
     * 显示第二个动画提示
     */
    private void showDialogTwo() {
        EyesCorrectDialog dialog = EyesCorrectDialog.newInstance();
        dialog.setonCallBack(() -> {
            Message msg = new Message();
            msg.what = EventUtil.START_CORRECT;
            mHandler.sendMessage(msg);
        });
        dialog.show(getSupportFragmentManager(), "");
    }


    /**
     * 初始化摄像头
     */
    private void openCarmea() {
        SurfaceHolder surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(new SurfaceHolderCaremaFont(this));
    }


    /**
     * 设置棋盘格宽高
     */
    private void setCorrectWAndH(int line, int lie) {
        showWaitProgress(getString(R.string.init_loading));
        Message msg = new Message();
        msg.what = EventUtil.START_CVC;
        msg.arg1 = lie;
        msg.arg2 = line;
        mHandler.sendMessage(msg);
    }


    /**
     * 初始化校验监听
     */
    private void setCorectListener() {
        mHandler.setEyesCallback(new JniHandler.OnEyesCallBack() {
            @Override
            public void initSuress() {
                runOnUiThread(() -> {
                    hintWaitProgress();
                    dialogOneFra.dismiss();
                    showDialogTwo();
                });
            }

            @Override
            public void initError(String message) {
                runOnUiThread(() -> {
                    hintWaitProgress();
                    T.showShort(message);
                });
            }

            @Override
            public void picluerNextSuress() {
                Log.i("wuliang", "next!");
                haveCount++;
                UIhandler.sendEmptyMessage(0x11);
                if (haveCount >= CountNum) {
                    UIhandler.sendEmptyMessage(0x22);
                    Message msg = new Message();
                    msg.what = EventUtil.START_CALIBRATION;
                    mHandler.sendMessage(msg);
                } else {
                    if (dialogOneFra.isVisible()) {
                        return;
                    }
                    Message msg = new Message();
                    msg.what = EventUtil.START_CORRECT;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void picluerNextError() {
                Log.i("wuliang", "error!");
                Message msg = new Message();
                msg.what = EventUtil.START_CORRECT;
                mHandler.sendMessage(msg);
            }

            @Override
            public void pictureFinnish(boolean isSuress) {
                if (isSuress) {
                    T.showShort("校准完成！");
                    finish();
                } else {
                    runOnUiThread(() -> showMessageDialog(getString(R.string.correct_error)));
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                showMessageDialog(String.format(getString(R.string.eyes_hint), CountNum + ""), getString(R.string.sure), view1 -> {
                    dissmissMessageDialog();
                    Message msg = new Message();
                    msg.what = EventUtil.STOP_CORRECT;
                    mHandler.sendMessage(msg);
                    finish();
                });
                break;
        }
    }


    Handler UIhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    if (piculeNum != null) {
                        piculeNum.setText(String.valueOf(haveCount));
                    }
                    break;
                case 0x22:
                    showWaitProgress(getString(R.string.correct_loading));
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        if (!SPUtils.getBoolean(Constant.KEY_LIGNT, false)) {
            LedHelper.PER_ledToggle(0);
        }
        SurfaceHolderCaremaFont.stopCarema();
        super.onDestroy();
    }
}
