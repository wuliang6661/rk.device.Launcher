package rk.device.launcher.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.R;
import rk.device.launcher.utils.DrawableUtil;
import rk.device.launcher.utils.QRCodeUtils;

public class SystemInfoActivity extends BaseActivity {

	@BindView(R.id.iv_back)
	ImageView mIvBack;
	@BindView(R.id.tv_title)
	TextView mTvTitle;
	@BindView(R.id.iv_qrcode)
	ImageView mIvQrcode;
	@BindView(R.id.btn_check_update)
	Button mBtnCheckUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_info);
		ButterKnife.bind(this);
		hideNavigationBar();
		mIvQrcode.post(new Runnable() {
			@Override
			public void run() {
				Bitmap qrCodeBitmap = QRCodeUtils.createQRCode("mundane", mIvQrcode.getWidth(), mIvQrcode.getHeight());
				mIvQrcode.setImageBitmap(qrCodeBitmap);
			}
		});
		mTvTitle.setText("系统信息");

		DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_round_corner, mBtnCheckUpdate);
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
