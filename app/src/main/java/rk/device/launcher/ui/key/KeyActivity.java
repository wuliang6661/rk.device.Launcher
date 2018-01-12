package rk.device.launcher.ui.key;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * <p>
 * 激活activity
 */

public class KeyActivity extends MVPBaseActivity<KeyContract.View, KeyPresenter> implements KeyContract.View {

    @Bind(R.id.key_edit)
    EditText keyEdit;
    @Bind(R.id.suress_button)
    Button suressButton;
    @Bind(R.id.go_net)
    TextView goNet;
    @Bind(R.id.error_layout)
    RelativeLayout errorLayout;

    @Override
    protected int getLayout() {
        return R.layout.act_key;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
