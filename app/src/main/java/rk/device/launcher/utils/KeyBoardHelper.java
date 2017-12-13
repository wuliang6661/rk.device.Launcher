package rk.device.launcher.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardHelper {

	private Activity activity;
	private OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener;
	private int screenHeight;
	private int blankHeight = 0;

	public KeyBoardHelper(Activity activity) {
		this.activity = activity;
		screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		if (activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public void onCreate() {
//		View content = activity.findViewById(android.R.id.content);
//		// content.addOnLayoutChangeListener(listener); 这个方法有时会出现一些问题
//		content.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
		activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
	}

	public void onDestory() {
//		View content = activity.findViewById(android.R.id.content);
//		content.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
		activity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
	}
	
	private boolean isFirst = true;

	private OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			// 判断窗口可见区域大小
			Rect rect = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
			// fixme 如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3, 则表示软键盘弹出, 否则为软键盘收起
			int heightDifference = screenHeight - rect.bottom;
			if (isFirst) {
				blankHeight = heightDifference;
				isFirst = false;
			}
			if (heightDifference != blankHeight) {
				if (heightDifference > blankHeight) {
					// keyboard pop
					if (onKeyBoardStatusChangeListener != null) {
						onKeyBoardStatusChangeListener.OnKeyBoardPop(heightDifference);
					}
				} else { // heightDifference < blankHeight
					// keyboard close
					if (onKeyBoardStatusChangeListener != null) {
						onKeyBoardStatusChangeListener.OnKeyBoardClose(blankHeight);
					}
				}
				blankHeight = heightDifference;
			}
		}
	};

	public void setOnKeyBoardStatusChangeListener(OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener) {
		this.onKeyBoardStatusChangeListener = onKeyBoardStatusChangeListener;
	}

	public void showKeyBoard(final View editText) {
		editText.requestFocus();
		InputMethodManager manager = (InputMethodManager) activity.getSystemService("input_method");
		manager.showSoftInput(editText, 0);
	}

	public void hideKeyBoard(View editText) {
		InputMethodManager manager = (InputMethodManager) activity.getSystemService("input_method");
		manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public interface OnKeyBoardStatusChangeListener {

		void OnKeyBoardPop(int keyBoardheight);

		void OnKeyBoardClose(int oldKeyBoardheight);
	}

}
