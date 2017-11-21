package rk.device.launcher.base;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by mundane on 2017/11/16 下午2:40
 */

public class BaseActivity extends AppCompatActivity {
	public void hideNavigationBar() {
		final  View decorView = getWindow().getDecorView();
		final int  uiOption = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				|View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				|View.SYSTEM_UI_FLAG_IMMERSIVE
				|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

		decorView.setSystemUiVisibility(uiOption);

		// This code will always hide the navigation bar
		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					decorView.setSystemUiVisibility(uiOption);
				}
			}
		});
//		hideBottomUIMenu();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	/**
	 * 隐藏虚拟按键，并且全屏
	 */
	protected void hideBottomUIMenu() {
		final int uiOption;
		int curApiVersion = android.os.Build.VERSION.SDK_INT;
		// This work only for android 4.4+
		if (curApiVersion >= Build.VERSION_CODES.KITKAT) {
			// This work only for android 4.4+
			// hide navigation bar permanently in android activity
			// touch the screen, the navigation bar will not show
			uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		} else {
			// touch the screen, the navigation bar will show
			uiOption = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		}

		// must be executed in main thread :)
		final View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(uiOption);
		// This code will always hide the navigation bar
		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					decorView.setSystemUiVisibility(uiOption);
				}
			}
		});
	}

	/**
	 * 如果底部的bar 隐藏就显示
	 */
	protected void showBottomUIMenu() {
		int flags;
		int curApiVersion = android.os.Build.VERSION.SDK_INT;
		// This work only for android 4.4+
		if (curApiVersion >= Build.VERSION_CODES.KITKAT) {
			// This work only for android 4.4+
			// hide navigation bar permanently in android activity
			// touch the screen, the navigation bar will not show
			flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

		} else {
			// touch the screen, the navigation bar will show
			flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		}

		// must be executed in main thread :)
		getWindow().getDecorView().setSystemUiVisibility(flags);
	}
}
