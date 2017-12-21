package rk.device.launcher.ui.activity;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.service.SleepTaskServer;

/**
 * Created by wuliang on 2017/12/19.
 * <p>
 * 休眠时显示的activity
 */

public class SleepActivity extends BaseCompatActivity {


    @Bind(R.id.advertising_img)
    RelativeLayout advertisingImg;

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
    }

    @Override
    protected void initData() {
        advertisingImg.setOnClickListener(view -> {
            finish();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SleepTaskServer.getSleepHandler(SleepActivity.this).sendEmptyMessage(0x33);
    }
}
