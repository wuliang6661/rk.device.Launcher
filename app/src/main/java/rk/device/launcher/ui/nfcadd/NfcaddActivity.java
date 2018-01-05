package rk.device.launcher.ui.nfcadd;

import android.os.Bundle;
import android.support.annotation.Nullable;

import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.NFCAddEvent;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.rxjava.RxBus;
import rx.Subscriber;

/**
 * NFC 卡添加
 */
public class NfcaddActivity extends MVPBaseActivity<NfcaddContract.View, NfcaddPresenter>
        implements NfcaddContract.View {

    @Override
    protected int getLayout() {
        return R.layout.activity_nfc_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        registerRxBus();
    }

    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(NFCAddEvent.class).subscribe(new Subscriber<NFCAddEvent>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(NFCAddEvent nfcAddEvent) {
                T.showShort(nfcAddEvent.NFCCard);
            }
        }));
    }

    private void initData() {
        LauncherApplication.sIsNFCAdd = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LauncherApplication.sIsNFCAdd = 0;
    }
}
