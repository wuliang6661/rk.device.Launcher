package rk.device.launcher.ui.setting;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;

public class RecoveryActivity extends BaseActivity {

    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv)
    TextView mTv;
    private Handler mHandler;

    @Override
    protected int getLayout() {
        return R.layout.activity_recovery;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        ((AnimationDrawable) mIv.getBackground()).start();
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((AnimationDrawable) mIv.getBackground()).stop();
                mIv.setBackgroundResource(R.drawable.icon_recovery_success);
                mTv.setText("恢复成功, 正在重启");
            }
        }, 3000);
    }

    protected void initData() {

    }

    @Override
    public void onBackPressed() {
//		super.onBackPressed();
    }
}
