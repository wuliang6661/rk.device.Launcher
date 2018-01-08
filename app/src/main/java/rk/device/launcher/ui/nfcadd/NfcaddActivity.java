package rk.device.launcher.ui.nfcadd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.NFCAddEvent;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.verify.VerifyUtils;
import rx.Subscriber;

/**
 * NFC 卡添加
 * 
 * @doc 1.进入该页面，进入nfc卡录入模式
 * @doc 2.nfc卡号读取到之后判断该卡是否绑定用户
 * @doc 2.1 已绑定提示已绑定
 * @doc 2.2 未绑定则显示卡号
 * @doc 3.点击保存录入卡号
 */
public class NfcaddActivity extends MVPBaseActivity<NfcaddContract.View, NfcaddPresenter>
        implements NfcaddContract.View, View.OnClickListener {

    private static final String EXTRA_UNIQUEID = "uniqueId";
    private String              uniqueId       = null;

    private TextView            cardNumTv;                  //卡号
    @Bind(R.id.ll_card_notice)
    LinearLayout                cardNoticeLL;               //用于展示提示信息的Layout
    @Bind(R.id.stub_layout)
    ViewStub                    cardNumberStub;             //用于展示卡片信息的Layout
    TextView                    noticeTv;                   //用于提示当前卡牌状态
    private View                cardNumberView;
    private Button              saveBtn;

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
        goBack();

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
                        //用户录入，首先需要判断改卡是否已经绑定了用户——用户和卡一一对应
                        User user = VerifyUtils.getInstance().verifyByNfc(nfcAddEvent.NFCCard);
                        if (user == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (cardNoticeLL.getVisibility() == View.VISIBLE) {
                                        cardNoticeLL.setVisibility(View.GONE);
                                        cardNumberView = cardNumberStub.inflate();
                                        cardNumTv = cardNumberView.findViewById(R.id.tv_card_num);
                                        saveBtn = cardNumberView.findViewById(R.id.btn_save);
                                        noticeTv = cardNumberView.findViewById(R.id.tv_notice);
                                        saveBtn.setOnClickListener(NfcaddActivity.this);
                                    }
                                    cardNumTv.setText(nfcAddEvent.NFCCard);
                                    noticeTv.setText(getResources().getString(R.string.notice_card_read_success));
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //判断当前卡已经存在
                                    noticeTv.setText(getResources().getString(R.string.notice_card_is_exist));
                                }
                            });
                            return;
                        }
                        Log.i("VerifyService", "VerifyService NFCAddEvent:" + nfcAddEvent.NFCCard);
                        T.showShort(nfcAddEvent.NFCCard);
                    }
                }));
    }

    private void initData() {
        setTitle(getString(R.string.title_nfc_card_add));
        LauncherApplication.sIsNFCAdd = 1;
        uniqueId = getIntent().getStringExtra(EXTRA_UNIQUEID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LauncherApplication.sIsNFCAdd = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                //添加用户
                if (TextUtils.isEmpty(uniqueId) || uniqueId == null) {
                    T.showShort("该用户不存在");
                    return;
                }
                String cardNumber = cardNumTv.getText().toString().trim();
                User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
                if (eUser != null) {
                    eUser.setCardNo(cardNumber);
                    DbHelper.update(eUser);
                    T.showShort("已成功绑定该卡");
                }
                break;
        }
    }
}
