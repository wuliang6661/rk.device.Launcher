package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cvc.EventUtil;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseDialogFragment;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.utils.ScreenUtil;

/**
 * Created by wuliang on 2017/12/9.
 * <p>
 * 初始化外设失败的弹窗
 */

public class InitErrorDialogFragment extends BaseDialogFragment implements View.OnClickListener {


    @Bind(R.id.led_error)
    ImageView ledError;
    @Bind(R.id.cvc_error)
    ImageView cvcError;
    @Bind(R.id.nfc_error)
    ImageView nfcError;
    @Bind(R.id.md_error)
    ImageView finderError;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.led_progress)
    ProgressBar ledProgress;
    @Bind(R.id.cvc_progress)
    ProgressBar cvcProgress;
    @Bind(R.id.nfc_progress)
    ProgressBar nfcProgress;
    @Bind(R.id.md_progress)
    ProgressBar mdProgress;

    private int ledStatus = 0;
    private int cvcStatus = 0;
    private int finderStarus = 0;
    private int nfcStatus = 0;

    private JniHandler jniHandler;

    /**
     * 获取dialog对象
     */
    public static InitErrorDialogFragment newInstance() {
        return new InitErrorDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_init_error, container, false);
        //设置窗口以对话框样式显示
//		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
        // 设置对话框背景色，否则有虚框
        // 这句代码十分重要, 否则圆角背景的圆角永远就只有那么大
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jniHandler = JniHandler.getInstance();
        setViewVisable(cvcError, cvcStatus);
        setViewVisable(ledError, ledStatus);
        setViewVisable(nfcError, nfcStatus);
        if (finderStarus > 0) {
            finderError.setVisibility(View.GONE);
        } else {
            finderError.setVisibility(View.VISIBLE);
        }
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ScreenUtil.getScreenWidth(getContext()) - 300;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    /**
     * 判断控件是否显示
     */
    private void setViewVisable(View view, int status) {
        if (status == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }


    public void setStatus(int cvcStatus, int LedStatus, int finderStarus, int NfcStatus) {
        this.cvcStatus = cvcStatus;
        this.ledStatus = LedStatus;
        this.finderStarus = finderStarus;
        this.nfcStatus = NfcStatus;
    }


    public void setInitFinish() {
        ledProgress.setVisibility(View.GONE);
        cvcProgress.setVisibility(View.GONE);
        mdProgress.setVisibility(View.GONE);
        nfcProgress.setVisibility(View.GONE);

        setViewVisable(cvcError, cvcStatus);
        setViewVisable(ledError, ledStatus);
        setViewVisable(nfcError, nfcStatus);

        if (finderStarus > 0) {
            finderError.setVisibility(View.GONE);
        } else {
            finderError.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        Message msg = new Message();
        msg.what = EventUtil.INIT_JNI;
        jniHandler.sendMessageDelayed(msg, 10);

        setProgress(cvcProgress, cvcError, cvcStatus);
        setProgress(nfcProgress, nfcError, nfcStatus);
        setProgress(mdProgress, finderError, finderStarus);
        setProgress(ledProgress, ledError, ledStatus);
    }

    private void setProgress(View progress, View error, int status) {
        if (status != 0) {
            error.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
        }
    }


}
