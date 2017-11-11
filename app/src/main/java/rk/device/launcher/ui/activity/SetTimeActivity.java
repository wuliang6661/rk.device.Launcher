package rk.device.launcher.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.widget.popupwindow.SetDatePopupWindow;

public class SetTimeActivity extends AppCompatActivity {

	@BindView(R.id.iv_back)
	ImageView mIvBack;
	@BindView(R.id.tv_title)
	TextView mTvTitle;
	@BindView(R.id.ll_set_date)
	LinearLayout mLlSetDate;
	@BindView(R.id.btn_finish_setting)
	Button mBtnFinishSetting;
	private SetDatePopupWindow mSetDatePopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time);
		ButterKnife.bind(this);
		mTvTitle.setText("时间设置");
		View view = View.inflate(this, R.layout.layout_picker_date_and_time, null);
		mSetDatePopupWindow = new SetDatePopupWindow(this, view);
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				finish();
			}
		});
		mLlSetDate.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (mSetDatePopupWindow.isShowing()) {
					mSetDatePopupWindow.dismiss();
				} else {
					mSetDatePopupWindow.showAsDropDown(mLlSetDate);
				}
			}
		});
	}
}
