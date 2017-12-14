package rk.device.launcher.ui.activity;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import cvc.EventUtil;
import peripherals.LedHelper;
import rk.device.launcher.R;
import rk.device.launcher.SurfaceHolderCaremaFont;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.fragment.EyesCorrectDialog;
import rk.device.launcher.ui.fragment.EyesCorrectDialogOneFra;
import rk.device.launcher.ui.fragment.WaitDialog;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.widget.BackCameraSurfaceView;

/**
 * Created by wuliang on 2017/12/7.
 * <p>
 * 双目校准界面
 */

public class EyesCorrectActivity extends BaseCompatActivity implements View.OnClickListener {


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

    private static boolean isBack = false;    //页面是否结束
    private static boolean isCallBack = true;   //收集照片是否完成


    @Override
    protected int getLayout() {
        return R.layout.act_eyes_correct;
    }

    @Override
    protected void initView() {
        setTitle("摄像头校准");
        isBack = false;
        openCarmea();
        HandlerThread thread = new HandlerThread("new_thread");
        thread.start();
        Looper looper = thread.getLooper();
        mHandler = new JniHandler(looper);
    }

    @Override
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
                if (isCallBack) {
                    setCorrectWAndH(line, lie);
                } else {
                    T.showShort("上一次收集照片尚未完成，请稍后重新开始...");
                }
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
            mHandler.sendMessageDelayed(msg, 10);
        });
        dialog.show(getSupportFragmentManager(), "");
    }


    /**
     * 初始化摄像头
     */
    private void openCarmea() {
        SurfaceHolder surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(new SurfaceHolderCaremaFont());
    }


    /**
     * 设置棋盘格宽高
     */
    private void setCorrectWAndH(int line, int lie) {
        showWaitProgress("正在初始化，请稍后...");
        Message msg = new Message();
        msg.what = EventUtil.START_CVC;
        msg.arg1 = lie;
        msg.arg2 = line;
        mHandler.sendMessageDelayed(msg, 10);
    }


    /**
     * 初始化校验监听
     */
    private void setCorectListener() {
        mHandler.setEyesCallback(new JniHandler.OnEyesCallBack() {
            @Override
            public void initSuress() {
                hintWaitProgress();
                dialogOneFra.dismiss();
                showDialogTwo();
            }

            @Override
            public void initError() {
                hintWaitProgress();
                T.showShort("棋盘格设置错误！请重新设置！");
            }

            @Override
            public void picluerNextSuress() {
                haveCount++;
                UIhandler.sendEmptyMessage(0x11);
                isCallBack = true;
                if (haveCount >= CountNum) {
                    UIhandler.sendEmptyMessage(0x22);
                    Message msg = new Message();
                    msg.what = EventUtil.START_CALIBRATION;
                    mHandler.sendMessageDelayed(msg, 10);
                } else {
                    if (isBack || dialogOneFra.isVisible()) {
                        return;
                    }
                    isCallBack = false;
                    Message msg = new Message();
                    msg.what = EventUtil.START_CORRECT;
                    mHandler.sendMessageDelayed(msg, 10);
                }
            }

            @Override
            public void picluerNextError() {
                isCallBack = true;
                if (isBack || dialogOneFra.isVisible()) {
                    return;
                }
                isCallBack = false;
                Message msg = new Message();
                msg.what = EventUtil.START_CORRECT;
                mHandler.sendMessageDelayed(msg, 10);
            }

            @Override
            public void pictureFinnish() {
                T.showShort("校准完成！");
                isBack = true;
                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                showMessageDialog("校准照片未满" + CountNum + "张，\n\n确认退出后此次校准失败", "确定", view1 -> {
                    dissmissMessageDialog();
                    isBack = true;
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
                    showWaitProgress("正在校准，需要几分钟左右，请稍后...");
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!SPUtils.getBoolean(Constant.KEY_LIGNT, false)) {
            LedHelper.PER_ledToggle(0);
        }
    }
}
