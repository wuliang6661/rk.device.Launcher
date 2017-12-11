package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.utils.DrawableUtil;
import rk.device.launcher.utils.ScreenUtil;


/**
 * @author : mundane
 * @time : 2017/8/22 19:01
 * @description :
 * @file : ConfirmDialogFragment.java
 */

public class WifiDetailDialogFragment extends DialogFragment {

	@Bind(R.id.btn_left)
	Button mBtnLeft;
	@Bind(R.id.btn_right)
	Button mBtnRight;
	@Bind(R.id.tv_ip)
	TextView mTvIp;
	@Bind(R.id.tv_net_mask)
	TextView mTvNetMask;
	@Bind(R.id.tv_net_gate)
	TextView mTvNetGate;
	@Bind(R.id.tv_dns)
	TextView mTvDns;
	@Bind(R.id.iv_close)
	ImageView mIvClose;
	@Bind(R.id.tv_wifi_name)
	TextView mTvWifiName;
	private OnClickListener mOnClickListener;
	private String mIPAddress;
	private String mNetMask;
	private String mNetGate;
	private String mDns;
	private String mWifiName;


	public static WifiDetailDialogFragment newInstance() {
		WifiDetailDialogFragment dialogFragment = new WifiDetailDialogFragment();
		return dialogFragment;
	}

	public WifiDetailDialogFragment setWifiName(String wifiName) {
		mWifiName = wifiName;
		return this;
	}

	public WifiDetailDialogFragment setIP(String IPAddress) {
		mIPAddress = IPAddress;
		return this;
	}

	public WifiDetailDialogFragment setNetMask(String netMask) {
		mNetMask = netMask;
		return this;
	}

	public WifiDetailDialogFragment setNetGate(String netGate) {
		mNetGate = netGate;
		return this;
	}

	public WifiDetailDialogFragment setDns(String dns) {
		mDns = dns;
		return this;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	public interface OnClickListener {
		void onLeftClicked();

		void onRightClicked();
	}


	public WifiDetailDialogFragment setOnClickListener(OnClickListener onClickListener) {
		mOnClickListener = onClickListener;
		return this;
	}

	@Override
	public void onStart() {
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = ScreenUtil.getScreenWidth(getContext()) - 300;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
//		View decorView = window.getDecorView();
//		decorView.setPadding(100, 0, 100, 0);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View rootView = inflater.inflate(R.layout.dialogfragment_wifi_detail, container, false);
		//设置窗口以对话框样式显示
//		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
		// 设置对话框背景色，否则有虚框
		// 这句代码十分重要, 否则圆角背景的圆角永远就只有那么大
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getDialog().setCanceledOnTouchOutside(true);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mTvIp.setText(String.format("IP地址: %S", mIPAddress));
		mTvNetMask.setText(String.format("子网掩码: %s", mNetMask));
		mTvNetGate.setText(String.format("网关: %s", mNetGate));
		mTvDns.setText(String.format("DNS: %s", mDns));
		mTvWifiName.setText(mWifiName);
		DrawableUtil.addPressedDrawable(getContext(), R.drawable.shape_dialog_btn_cancel, mBtnLeft);
		DrawableUtil.addPressedDrawable(getContext(), R.drawable.shape_dialog_btn_confirm, mBtnRight);
		mIvClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WifiDetailDialogFragment.this.dismiss();
			}
		});
		mBtnLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnClickListener != null) {
					mOnClickListener.onLeftClicked();
				}
			}
		});

		mBtnRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnClickListener != null) {
					mOnClickListener.onRightClicked();
				}
			}
		});
	}
}
