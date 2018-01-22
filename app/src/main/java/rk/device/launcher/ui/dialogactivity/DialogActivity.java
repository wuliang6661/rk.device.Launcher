package rk.device.launcher.ui.dialogactivity;

import android.os.Bundle;

import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;

public class DialogActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.dialogfragment_input_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(true);
    }
}
