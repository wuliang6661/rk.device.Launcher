package rk.device.launcher.ui.detection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.NFCAddEvent;
import rk.device.launcher.utils.ResUtil;
import rk.device.launcher.utils.WindowManagerUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rx.Subscriber;

/**
 * Created by wuliang on 2018/2/5.
 * <p>
 * nfc检测
 */

public class NfcDetection extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.ll_card_notice)
    LinearLayout cardNoticeLL;
    @Bind(R.id.stub_layout)
    ViewStub cardNumberStub;

    private View cardNumberView;
    private TextView cardNumTv;
    private Button saveBtn;
    private TextView noticeTv;                   //用于提示当前卡牌状态

    @Override
    protected int getLayout() {
        return R.layout.activity_nfc_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle(ResUtil.getString(R.string.nfc_detection));

        invition();
    }


    private void invition() {
        LauncherApplication.sIsNFCAdd = 1;
        registerRxBus();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cardNoticeLL.getLayoutParams();
        int height = WindowManagerUtils.getWindowHeight(this);
        lp.setMargins(0, height / 4, 0, 0);
        cardNoticeLL.setLayoutParams(lp);
    }


    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(NFCAddEvent.class)
                .subscribe(new Subscriber<NFCAddEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(NFCAddEvent nfcAddEvent) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setCardInfo(nfcAddEvent.NFCCard, true);
                            }
                        });
                    }
                }));
    }


    /**
     * 设置卡片信息
     *
     * @param nfcCard
     */
    private void setCardInfo(String nfcCard, boolean showNotice) {
        if (cardNoticeLL.getVisibility() == View.VISIBLE) {
            cardNoticeLL.setVisibility(View.GONE);
            cardNumberView = cardNumberStub.inflate();
            cardNumTv = cardNumberView.findViewById(R.id.tv_card_num);
            saveBtn = cardNumberView.findViewById(R.id.btn_save);
            noticeTv = cardNumberView.findViewById(R.id.tv_notice);
        }
        cardNumTv.setText(nfcCard);
        saveBtn.setText("完成");
        saveBtn.setOnClickListener(NfcDetection.this);
        if (showNotice) {
            noticeTv.setVisibility(View.VISIBLE);
            noticeTv.setText(getResources().getString(R.string.notice_card_read_success));
        }
    }


    @Override
    protected void onDestroy() {
        LauncherApplication.sIsNFCAdd = 0;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
