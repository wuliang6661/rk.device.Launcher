package rk.device.launcher.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.donkingliang.banner.CustomBanner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.event.SleepImageEvent;
import rk.device.launcher.service.SleepTaskServer;

/**
 * Created by wuliang on 2017/12/19.
 * <p>
 * 休眠时显示的activity
 */

public class SleepActivity extends BaseCompatActivity {


    @Bind(R.id.advertising_img)
    RelativeLayout advertisingImg;
    @Bind(R.id.banner)
    CustomBanner mBanner;

    @Override
    protected int getLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 注意顺序
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.act_sleep;
    }

    @Override
    protected void initView() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//需要添加的语句
        SleepTaskServer.getSleepHandler(SleepActivity.this).sendEmptyMessage(0x22);
        isHaveLunBo();
        registerBus();
    }

    @Override
    protected void initData() {
        advertisingImg.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * 判断是否已接收到图片，显示最新的图片
     */
    private void isHaveLunBo() {
        String destDirPath = "/data/rk_backup/rk_ad";
        File destDir = new File(destDirPath);
        if (destDir.exists()) {
            File fa[] = destDir.listFiles();
            setLunBoSleep(Arrays.asList(fa));
        }
    }

    /**
     * 接收到更换休眠图片通知
     */
    private void registerBus() {
        addSubscription(RxBus.getDefault().toObserverable(SleepImageEvent.class).subscribe(sleepImageEvent -> {
            setLunBoSleep(sleepImageEvent.getFileList());
        }, throwable -> throwable.printStackTrace()));
    }


    /**
     * 设置图片轮播
     */
    private void setLunBoSleep(List<File> imageFils) {
        if (imageFils.size() == 1) {
            Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(imageFils.get(0).getAbsolutePath()));
            advertisingImg.setBackground(drawable);
            return;
        }
        mBanner.setPages(new CustomBanner.ViewCreator<File>() {
            @Override
            public View createView(Context context, int position) {
                //这里返回的是轮播图的项的布局 支持任何的布局
                //position 轮播图的第几个项
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            }

            @Override
            public void updateUI(Context context, View view, int position, File data) {
                Bitmap bitmap = BitmapFactory.decodeFile(data.getAbsolutePath());
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(bitmap);
            }
        }, imageFils);
        mBanner.startTurning(5000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SleepTaskServer.getSleepHandler(SleepActivity.this).sendEmptyMessage(0x33);
    }

}
