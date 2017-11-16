package rk.device.launcher.base;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by mundane on 2017/11/16 下午2:40
 */

public class BaseActivity extends AppCompatActivity {
	public void hideNavigationBar() {
		View decorView = getWindow().getDecorView();
		int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(option);
	}
}
