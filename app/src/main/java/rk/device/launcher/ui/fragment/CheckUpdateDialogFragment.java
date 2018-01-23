package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseDialogFragment;
import rk.device.launcher.utils.ScreenUtil;

/**
 * Created by mundane on 2017/12/21 下午1:43
 */

public class CheckUpdateDialogFragment extends BaseDialogFragment implements View.OnClickListener {
	
	
	@Bind(R.id.tv_title)
	TextView mTvTitle;
	@Bind(R.id.tv_msg)
	TextView mTvMsg;
	@Bind(R.id.btn_confirm)
	Button mBtnConfirm;
	private String mMessage;
	private String mTitle;
	private String mConfirmText;
	
	
	public CheckUpdateDialogFragment setMessage(String message) {
		mMessage = message;
		return this;
	}
	
	public CheckUpdateDialogFragment setTitle(String title) {
		mTitle = title;
		return this;
	}
	
	public CheckUpdateDialogFragment setPositiveButton(String text) {
		mConfirmText = text;
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
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialogfragment_check_update, container, false);
		// 设置对话框背景色，否则有虚框, 这句代码十分重要, 否则圆角背景的圆角永远就只有那么大
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (!TextUtils.isEmpty(mMessage)) {
			mTvMsg.setText(mMessage);
		}
		if (TextUtils.isEmpty(mTitle)) {
			mTvTitle.setText(mTitle);
		}
		mBtnConfirm.setOnClickListener(this);
	}
	
	public boolean isShowing() {
		if (getDialog() != null && getDialog().isShowing()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_confirm:
				dismiss();
				break;
		}
		
	}
}
