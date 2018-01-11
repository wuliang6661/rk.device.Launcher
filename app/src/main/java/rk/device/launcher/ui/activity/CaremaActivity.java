package rk.device.launcher.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;

/**
 * Created by wuliang on 2017/12/21.
 * <p>
 * 测试摄像头页面
 */

public class CaremaActivity extends BaseActivity {

//    @Bind(R.id.image)
//    ImageView image;
//    @Bind(R.id.hint_text)
//    TextView hintText;


    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;

    @Override
    protected int getLayout() {
        return R.layout.act_carema;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        goBack();
        setTitle("摄像头效果预览");

        initSurfaceViewOne();
    }

    protected void initData() {
//        String path = "/data/rk_backup/stereo_calibration.jpg";
//        File file = new File(path);
//        if (file.exists()) {
//            image.setVisibility(View.VISIBLE);
//            hintText.setVisibility(View.GONE);
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            image.setImageBitmap(bitmap);
//        } else {
//            image.setVisibility(View.GONE);
//            hintText.setVisibility(View.VISIBLE);
//        }
    }


    private void initSurfaceViewOne() {
        SurfaceHolder surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(new SurfaceHolderCaremaFont());
    }

}
