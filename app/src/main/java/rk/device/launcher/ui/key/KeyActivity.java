package rk.device.launcher.ui.key;


import android.os.Bundle;
import android.support.annotation.Nullable;

import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * <p>
 * 激活activity
 */

public class KeyActivity extends MVPBaseActivity<KeyContract.View, KeyPresenter> implements KeyContract.View {

    @Override
    protected int getLayout() {
        return R.layout.act_key;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
}
