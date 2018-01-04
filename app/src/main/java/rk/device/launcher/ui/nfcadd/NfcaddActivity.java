package rk.device.launcher.ui.nfcadd;

import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;

/**
 * NFC 卡添加
 */
public class NfcaddActivity extends MVPBaseActivity<NfcaddContract.View, NfcaddPresenter>
        implements NfcaddContract.View {

    @Override
    protected int getLayout() {
        return R.layout.activity_nfc_add;
    }
}
