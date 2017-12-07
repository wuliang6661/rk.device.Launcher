package rk.device.launcher.ui.activity;

import android.nfc.cardemulation.HostNfcFService;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import cvc.CvcHandler;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.SurfaceHolderCaremaFont;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.ui.fragment.EyesCorrectDialog;
import rk.device.launcher.ui.fragment.EyesCorrectDialogOneFra;
import rk.device.launcher.ui.fragment.WaitDialog;
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
    @Bind(R.id.deng_off)
    ImageView dengOff;
    @Bind(R.id.deng_text)
    TextView dengText;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.all_num)
    TextView allNum;

    int CountNum = 20;    //校准照片数量默认20张
    int haveCount = 0;    //已成功收集的照片，默认从0开始


    private CvcHandler mHandler = null;
    EyesCorrectDialogOneFra dialogOneFra;
    Timer timer;

    @Override
    protected int getLayout() {
        return R.layout.act_eyes_correct;
    }

    @Override
    protected void initView() {
        setTitle("摄像头校准");
        openCarmea();
        HandlerThread thread = new HandlerThread("new_thread");
        thread.start();
        Looper looper = thread.getLooper();
        mHandler = new CvcHandler(looper);
    }

    @Override
    protected void initData() {
        ivBack.setOnClickListener(this);
        setCorectListener();
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
            timer = new Timer();
            timer.schedule(task, 0, 3000);
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
        mHandler.setEyesCallback(new CvcHandler.OnEyesCallBack() {
            @Override
            public void initSuress() {
                dialogOneFra.dismiss();
                showDialogTwo();
            }

            @Override
            public void initError() {
                T.showShort("棋盘格设置错误！请重新设置！");
            }

            @Override
            public void picluerNext() {
                haveCount++;
                UIhandler.sendEmptyMessage(0x11);
                if (haveCount == CountNum) {
                    timer.cancel();
                    UIhandler.sendEmptyMessage(0x22);
                    Message msg = new Message();
                    msg.what = EventUtil.START_CALIBRATION;
                    mHandler.sendMessageDelayed(msg, 10);
                }
            }

            @Override
            public void pictureFinnish() {
                T.showShort("校准完成！");
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
                    finish();
                });
                break;
        }
    }


    /**
     * 执行收集照片
     */
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = EventUtil.START_CORRECT;
            mHandler.sendMessageDelayed(msg, 10);
        }
    };

    Handler UIhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    piculeNum.setText(String.valueOf(haveCount));
                    break;
                case 0x22:
                    WaitDialog dialog = new WaitDialog();
                    dialog.show(getSupportFragmentManager(), "");
                    break;
            }
        }
    };


}
