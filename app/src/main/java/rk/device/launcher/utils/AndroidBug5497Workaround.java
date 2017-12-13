package rk.device.launcher.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


public class AndroidBug5497Workaround {
	
	// For more information, see https://code.google.com/p/android/issues/detail?id=5497
	// To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
	
	public static void assistActivity (Activity activity) {
		new AndroidBug5497Workaround(activity);
	}
	
	private View mChildOfContent;
	private int usableHeightPrevious;
	private FrameLayout.LayoutParams frameLayoutParams;
	
	public AndroidBug5497Workaround(Activity activity) {
		FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
		mChildOfContent = content.getChildAt(0);
		mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
		frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
	}
	
	public void destroyListener() {
		mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
	}
	
	private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		public void onGlobalLayout() {
			possiblyResizeChildOfContent();
		}
	};
	
	public interface KeyBoardChangeListener{
		void OnKeyBoardPop();
		
		void OnKeyBoardClose();
	}
	private KeyBoardChangeListener mKeyBoardChangeListener;
	
	public void setKeyBoardChangeListener(KeyBoardChangeListener keyBoardChangeListener) {
		mKeyBoardChangeListener = keyBoardChangeListener;
	}
	
	private void possiblyResizeChildOfContent() {
		// 可见窗口的高度
		int usableHeightNow = computeUsableHeight();
		if (usableHeightNow != usableHeightPrevious) {
			int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
			// heightDifference其实就是软键盘的高度
			int heightDifference = usableHeightSansKeyboard - usableHeightNow;
			if (heightDifference > (usableHeightSansKeyboard/4)) {
				// keyboard probably just became visible
				frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
				if (mKeyBoardChangeListener != null) {
					mKeyBoardChangeListener.OnKeyBoardPop();
				}
			} else {
				// keyboard probably just became hidden
				frameLayoutParams.height = usableHeightSansKeyboard;
				if (mKeyBoardChangeListener != null) {
					mKeyBoardChangeListener.OnKeyBoardClose();
				}
			}
			mChildOfContent.requestLayout();
			usableHeightPrevious = usableHeightNow;
		}
	}
	
	private int computeUsableHeight() {
		Rect r = new Rect();
		mChildOfContent.getWindowVisibleDisplayFrame(r);
		return (r.bottom - r.top);
	}
}
