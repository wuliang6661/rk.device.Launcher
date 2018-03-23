package rk.device.launcher.ui.detection;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.utils.ResUtil;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaBack;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;

/**
 * Created by wuliang on 2017/12/21.
 * <p>
 * 测试摄像头页面
 */

public class CaremaDetection extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.btn_finish_setting)
    Button btnFinishSetting;

    SurfaceHolder surfaceholder;

    @Override
    protected int getLayout() {
        return R.layout.act_carema;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    protected void initView() {
        goBack();
        setTitle(ResUtil.getString(R.string.carema_detecation));

        JniHandler handler = JniHandler.getInstance();
        Message message = Message.obtain();
        message.what = EventUtil.MEDIA_OPEN;
        handler.sendMessage(message);
        initSurfaceViewOne();
        btnFinishSetting.setOnClickListener(this);
    }


    private void initSurfaceViewOne() {
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(new SurfaceHolderCaremaFont());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish_setting:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        surfaceholder.addCallback(null);
        SurfaceHolderCaremaBack.stopCarema();
        super.onDestroy();
    }
}
