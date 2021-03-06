package rk.device.launcher.ui.setting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
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
import cvc.EventUtil;
import peripherals.LedHelper;
import peripherals.MdHelper;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.event.SleepImageEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.service.SleepTaskServer;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.cache.CacheUtils;
import rk.device.launcher.utils.rxjava.RxBus;

/**
 * Created by wuliang on 2017/12/19.
 * <p>
 * 休眠时显示的activity
 */

public class SleepActivity extends BaseActivity {


    @Bind(R.id.advertising_img)
    RelativeLayout advertisingImg;
    @Bind(R.id.banner)
    CustomBanner mBanner;

    private boolean isStartMd = true;

    @Override
    protected int getLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 注意顺序
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.act_sleep;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    protected void initView() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//需要添加的语句
        SleepTaskServer.getSleepHandler().sendEmptyMessage(0x22);
        isHaveLunBo();
        registerBus();
        if (SPUtils.getBoolean(Constant.KEY_LIGNT, false)) {    //如果灯是开的，休眠时关灯
            LedHelper.PER_ledToggle(0);
        }
    }

    protected void initData() {
        advertisingImg.setOnClickListener(view -> {
            setCloseAct();
        });
    }

    /**
     * 页面结束操作
     */
    private void setCloseAct() {
        isStartMd = false;
        setCrema(EventUtil.MEDIA_OPEN);
        if (SPUtils.getBoolean(Constant.KEY_LIGNT, false)) {    //如果灯是开的，结束休眠时开灯
            LedHelper.PER_ledToggle(1);
        }
        finish();
    }


    /**
     * 开始人体红外检测线程
     */
    private void startMdRunable() {
        int[] mdStaus = new int[1];
        new Thread(() -> {
            while (isStartMd) {
                int mdStatus = MdHelper.PER_mdGet(1, mdStaus);
                if (mdStatus == 0 && mdStaus[0] == 1) {
                    setCloseAct();
                }
            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setCrema(EventUtil.MEDIA_CLOSE);
        //五秒后发送
        handler.sendEmptyMessageDelayed(1, 1000);
    }


    Handler handler = new Handler(msg -> {
        startMdRunable();
        return true;
    });


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(1);
    }

    /**
     * 判断是否已接收到图片，显示最新的图片
     */
    private void isHaveLunBo() {
        String destDirPath = CacheUtils.getImgFile();
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
        SleepTaskServer.getSleepHandler().sendEmptyMessage(0x33);
    }


    private void setCrema(int status) {
        JniHandler mHandler = JniHandler.getInstance();
        Message msg = new Message();
        msg.what = status;
        mHandler.sendMessage(msg);
    }

}
