package rk.device.launcher.ui.nfcadd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.api.T;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.event.NFCAddEvent;
import rk.device.launcher.db.CardHelper;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.Card;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.WindowManagerUtils;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.utils.verify.VerifyUtils;
import rx.Subscriber;

/**
 * NFC 卡添加 and 详情
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

    private TextView            cardNumTv;                                   //卡号
    @Bind(R.id.ll_card_notice)
    LinearLayout                cardNoticeLL;                                //用于展示提示信息的Layout
    @Bind(R.id.stub_layout)
    ViewStub                    cardNumberStub;                              //用于展示卡片信息的Layout
    TextView                    noticeTv;                                    //用于提示当前卡牌状态
    @Bind(R.id.tv_notice)
    TextView                    noticeTv2;
    @Bind(R.id.iv_search)
    ImageView                   deleteImg;                                   //删除按钮
    private View                cardNumberView;
    private Button              saveBtn;
    private boolean             isDetail       = false;
    private boolean             isChange       = false;
    private boolean             isReload       = false;
    private DeviceUuidFactory   factory        = new DeviceUuidFactory(this);

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
        setOnClick(R.id.iv_search);
        registerRxBus();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cardNoticeLL.getLayoutParams();
        int height = WindowManagerUtils.getWindowHeight(this);
        lp.setMargins(0, height / 4, 0, 0);
        cardNoticeLL.setLayoutParams(lp);
        goBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange) {
                    showMessageDialog(getResources().getString(R.string.notice_exist_not_save),
                            getResources().getString(R.string.sure), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                } else {
                    finish();
                }
            }
        });

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
                        isChange = true;
                        //用户录入，首先需要判断改卡是否已经绑定了用户——用户和卡一一对应
                        if (isDetail && !isReload) {
                            return;
                        }
                        User user = VerifyUtils.getInstance().verifyByNfc(nfcAddEvent.NFCCard);
                        if (user == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setCardInfo(nfcAddEvent.NFCCard, true);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //判断当前卡已经存在
                                    if (noticeTv != null) {
                                        noticeTv.setVisibility(View.VISIBLE);
                                        noticeTv.setText(getResources()
                                                .getString(R.string.notice_card_is_exist));
                                    }
                                    if (noticeTv2.getVisibility() == View.VISIBLE) {
                                        noticeTv2.setText(getResources()
                                                .getString(R.string.notice_card_is_exist));
                                    }
                                }
                            });
                            return;
                        }
                        LogUtil.i("VerifyService",
                                "VerifyService NFCAddEvent:" + nfcAddEvent.NFCCard);
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
            if (isDetail) {
                saveBtn.setText(getString(R.string.re_load));
            }
            noticeTv = cardNumberView.findViewById(R.id.tv_notice);
            saveBtn.setOnClickListener(NfcaddActivity.this);
        }
        cardNumTv.setText(nfcCard);
        if (showNotice) {
            noticeTv.setVisibility(View.VISIBLE);
            noticeTv.setText(getResources().getString(R.string.notice_card_read_success));
        }
    }

    private void initData() {
        String title;
        LauncherApplication.sIsNFCAdd = 1;
        uniqueId = getIntent().getStringExtra(EXTRA_UNIQUEID);
        if (TextUtils.isEmpty(uniqueId) || uniqueId == null) {
            T.showShort(getString(R.string.illeagel_user_not_exist));
            return;
        }
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        if (eUser == null) {
            T.showShort(getString(R.string.illeagel_user_not_exist));
            finish();
            return;
        }
        List<Card> cardList = CardHelper.getList(uniqueId);
        if (cardList.size() > 0) {
            isDetail = true;
            setCardInfo(cardList.get(0).getNumber(), false);
            deleteImg.setVisibility(View.VISIBLE);
            deleteImg.setImageDrawable(getResources().getDrawable(R.mipmap.delete));
            title = getString(R.string.title_nfc_card_detail);
        } else {
            isDetail = false;
            deleteImg.setVisibility(View.GONE);
            title = getString(R.string.title_nfc_card_add);
        }
        setTitle(title);
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
                if (isDetail && !isReload) {
                    isReload = true;
                    cardNumTv.setText("");
                    noticeTv.setText(getString(R.string.notice_nfc_add));
                    saveBtn.setText(getResources().getString(R.string.save));
                    return;
                }
                //添加用户
                if (TextUtils.isEmpty(uniqueId) || uniqueId == null) {
                    T.showShort(getString(R.string.illeagel_user_not_exist));
                    return;
                }
                String cardNumber = cardNumTv.getText().toString().trim();
                User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
                if (eUser != null) {
                    //添加card到本地
                    List<Card> cardList = CardHelper.getList(uniqueId);
                    if (cardList.size() > 0) {
                        CardHelper.update(cardList.get(0).getId(), cardNumber,
                                Constant.TO_BE_UPDATE, 0, 0);
                    } else {
                        CardHelper.insert(eUser.getUniqueId(), cardNumber, Constant.TO_BE_ADD, 0,
                                0);
                    }
                    httpUpdateCard(eUser, cardList, cardNumber);
                    T.showShort(getString(R.string.card_add_success));
                    finish();
                } else {
                    T.showShort(getResources().getString(R.string.illeagel_user_not_exist));
                }
                break;
            case R.id.iv_search://删除
                showMessageDialog(getResources().getString(R.string.notice_delete_card),
                        getResources().getString(R.string.sure), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dissmissMessageDialog();
                                deleteCard();
                            }
                        });
                break;
        }
    }

    /**
     * 更新服务端数据
     * 
     * @param eUser
     * @param cardList
     * @param cardNumber
     */
    private void httpUpdateCard(User eUser, List<Card> cardList, String cardNumber) {
        JSONObject params = new JSONObject();
        try {
            params.put("access_token", SPUtils.getString(Constant.ACCENT_TOKEN));
            params.put("uuid", factory.getUuid());
            params.put("peopleId", eUser.getUniqueId());
            params.put("cardNo", cardNumber);
            params.put("startTime", eUser.getStartTime() / 1000);
            params.put("endTime", eUser.getEndTime() / 1000);
        } catch (JSONException e) {
        }
        if (cardList.size() > 0) {
            BaseApiImpl.editCard(params).subscribe(new Subscriber<Object>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Object o) {
                    List<Card> cardList = CardHelper.getList(uniqueId);
                    if (cardList.size() > 0) {
                        Card card = CardHelper.getList(uniqueId).get(0);
                        CardHelper.update(card.getId(), cardNumber, Constant.NORMAL, 0, 0);
                    }
                }
            });
        } else {
            BaseApiImpl.addCard(params).subscribe(new Subscriber<Object>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Object o) {
                    List<Card> cardList = CardHelper.getList(uniqueId);
                    if (cardList.size() > 0) {
                        Card card = CardHelper.getList(uniqueId).get(0);
                        CardHelper.update(card.getId(), cardNumber, Constant.NORMAL, 0, 0);
                    }
                }
            });
        }
    }

    /**
     * 删除卡
     */
    private void deleteCard() {
        if (TextUtils.isEmpty(uniqueId) || uniqueId == null) {
            T.showShort(getString(R.string.illeagel_user_not_exist));
            return;
        }
        User eUser = VerifyUtils.getInstance().queryUserByUniqueId(uniqueId);
        if (eUser != null) {
            eUser.setUploadStatus(0);
            DbHelper.update(eUser);
            T.showShort(getString(R.string.delete_success));
            finish();
        } else {
            T.showShort(getResources().getString(R.string.illeagel_user_not_exist));
        }
    }
}
