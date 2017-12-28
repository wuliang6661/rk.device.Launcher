package rk.device.launcher.ui.finger;


import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FingerActivity extends MVPBaseActivity<FingerContract.View, FingerPresenter> implements FingerContract.View {

    @Override
    protected int getLayout() {
        return R.layout.activity_finger;
    }
}
