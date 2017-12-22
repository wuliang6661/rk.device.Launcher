package rk.device.launcher.ui.activity;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.SurfaceHolderCaremaFont;
import rk.device.launcher.base.BaseCompatActivity;

/**
 * Created by wuliang on 2017/12/21.
 * <p>
 * 测试摄像头页面
 */

public class CaremaActivity extends BaseCompatActivity {


    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;

    @Override
    protected int getLayout() {
        return R.layout.act_carema;
    }

    @Override
    protected void initView() {
        goBack();
        setTitle("摄像头效果预览");

    }

    @Override
    protected void initData() {
        initSurfaceViewOne();
    }


    private void initSurfaceViewOne() {
        SurfaceHolder surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(new SurfaceHolderCaremaFont());
    }

}