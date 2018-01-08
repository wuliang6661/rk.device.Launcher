package rk.device.launcher.ui.fingeradd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.Bind;
import peripherals.FingerHelper;
import rk.device.launcher.R;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.mvp.MVPBaseActivity;

/**
 * MVPPlugin 邮箱 784787081@qq.com
 */

public class FingeraddActivity extends MVPBaseActivity<FingeraddContract.View, FingeraddPresenter>
        implements FingeraddContract.View, View.OnClickListener {

    @Bind(R.id.ll_button)
    LinearLayout buttonLL;
    @Bind(R.id.btn_add_finger)
    Button       addFingerBtn;

    @Override
    protected int getLayout() {
        return R.layout.activity_finger_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        setTitle("添加指纹");
        LauncherApplication.sIsFingerAdd = 0;
    }

    private void initView() {
        registerRxBus();
        setOnClick(R.id.btn_add_finger);
        goBack();
        addFingerBtn.setVisibility(View.VISIBLE);
    }

    private void registerRxBus() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_finger:
                String resultFinger = FingerHelper.JNIFpGetModuleInfo();
                LauncherApplication.sIsFingerAdd = 1;
                Log.i("FingerAddActivity", "resultFinger:" + resultFinger);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LauncherApplication.sIsFingerAdd = 0;
    }
}
