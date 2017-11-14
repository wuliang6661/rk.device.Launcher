package rk.device.launcher.ui.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.R;

public class RecoveryActivity extends AppCompatActivity {

	@BindView(R.id.iv)
	ImageView mIv;
	@BindView(R.id.tv)
	TextView mTv;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recovery);
		ButterKnife.bind(this);
		((AnimationDrawable) mIv.getBackground()).start();
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mIv.setImageResource(R.drawable.icon_recovery_success);
                mTv.setText("恢复成功, 正在重启");
			}
		}, 3000);

	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
	}
}
