package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import rk.device.launcher.R;
import rk.device.launcher.base.BaseDialogFragment;


/**
 * @author : mundane
 * @time : 2017/8/22 19:01
 * @description :
 * @file : ConfirmDialogFragment.java
 */

public class SelectItemListDialogFragment extends BaseDialogFragment {


	public static SelectItemListDialogFragment newInstance() {
		SelectItemListDialogFragment dialogFragment = new SelectItemListDialogFragment();
		return dialogFragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
	}



	@Override
	public void onStart() {
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
//		View decorView = window.getDecorView();
//		decorView.setPadding(100, 0, 100, 0);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View rootView = inflater.inflate(R.layout.activity_select_item, container, false);
		//设置窗口以对话框样式显示
//		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
		// 设置对话框背景色，否则有虚框
		// 这句代码十分重要, 否则圆角背景的圆角永远就只有那么大
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getDialog().getWindow().getAttributes().windowAnimations = R.style.selectItemDialogAnimation;

//		getDialog().setCanceledOnTouchOutside(true);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
}
