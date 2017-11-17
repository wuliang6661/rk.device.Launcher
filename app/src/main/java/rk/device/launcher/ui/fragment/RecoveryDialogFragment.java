package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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

public class RecoveryDialogFragment extends DialogFragment {

	@Bind(R.id.btn_cancel)
	Button mBtnCancel;
	@Bind(R.id.btn_confirm)
	Button mBtnConfirm;
//	Unbinder unbinder;
	private OnConfirmClickListener mOnConfirmClickListener;
	private onCancelClickListener mOnCancelClickListener;

	public static RecoveryDialogFragment newInstance() {
		RecoveryDialogFragment dialogFragment = new RecoveryDialogFragment();
		return dialogFragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		unbinder.unbind();
	}


	public interface OnConfirmClickListener {
		void onConfirmClick();
	}

	public interface onCancelClickListener {
		void onCancelClick();
	}

	public RecoveryDialogFragment setOnCancelClickListener(onCancelClickListener onCancelClickListener) {
		mOnCancelClickListener = onCancelClickListener;
		return this;
	}

	public RecoveryDialogFragment setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
		mOnConfirmClickListener = onConfirmClickListener;
		return this;
	}

	@Override
	public void onStart() {
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = ScreenUtil.getScreenWidth(getContext()) - 300;
		window.setAttributes(params);
//		View decorView = window.getDecorView();
//		decorView.setPadding(100, 0, 100, 0);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View rootView = inflater.inflate(R.layout.dialogfragment_recovery, container, false);
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
		DrawableUtil.addPressedDrawable(getContext(), R.drawable.shape_dialog_btn_cancel, mBtnCancel);
		DrawableUtil.addPressedDrawable(getContext(), R.drawable.shape_dialog_btn_confirm, mBtnConfirm);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnCancelClickListener != null) {
					mOnCancelClickListener.onCancelClick();
				}
			}
		});

		mBtnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnConfirmClickListener != null) {
					mOnConfirmClickListener.onConfirmClick();
				}
			}
		});
	}
}
