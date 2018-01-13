package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.utils.DrawableUtil;
import rk.device.launcher.utils.ScreenUtil;


/**
 * @author : mundane
 *         管理员密码输入弹窗
 */

public class InputWifiPasswordDialogFragment extends DialogFragment {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.btn_cancel)
    Button mBtnCancel;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    //	Unbinder unbinder;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.error_message)
    TextView errorMessage;


    private OnConfirmClickListener mOnConfirmClickListener;
    private onCancelClickListener mOnCancelClickListener;
    private CharSequence mTitle;
    private Integer mInputType;
    private String hint;
    private String content;
    private int maxLength = 0;

    public String getEtText() {
        return mEtPassword.getText().toString();
    }

    public InputWifiPasswordDialogFragment setInputType(int inputType) {
        mInputType = inputType;
        return this;
    }

    public static InputWifiPasswordDialogFragment newInstance() {
        InputWifiPasswordDialogFragment dialogFragment = new InputWifiPasswordDialogFragment();
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
    }

    // fixme 改成用setArgument使用bundle传进来, 而不是使用这种方式
    public InputWifiPasswordDialogFragment setTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//		unbinder.unbind();
        ButterKnife.unbind(this);
    }

    public void showError() {
        mLlError.setVisibility(View.VISIBLE);
    }


    /**
     * 显示错误提示
     */
    public void showError(String message) {
        mLlError.setVisibility(View.VISIBLE);
        errorMessage.setText(message);
    }


    /**
     * 显示默认显示文字
     */
    public void showHite(String hint) {
        this.hint = hint;
    }


    /**
     * 设置最大输入数
     */
    public void setMaxLength(int length) {
        this.maxLength = length;
    }


    public void hideError() {
        mLlError.setVisibility(View.INVISIBLE);
    }


    public interface OnConfirmClickListener {
        void onConfirmClick(String content);
    }

    public interface onCancelClickListener {
        void onCancelClick();
    }

    public InputWifiPasswordDialogFragment setOnCancelClickListener(onCancelClickListener onCancelClickListener) {
        mOnCancelClickListener = onCancelClickListener;
        return this;
    }

    public InputWifiPasswordDialogFragment setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ScreenUtil.getScreenWidth(getContext()) - 300;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
//		View decorView = window.getDecorView();
//		decorView.setPadding(100, 0, 100, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialogfragment_input_password, container, false);
        //设置窗口以对话框样式显示
//		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
        // 设置对话框背景色，否则有虚框
        // 这句代码十分重要, 否则圆角背景的圆角永远就只有那么大
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(true);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mInputType != null) {
            mEtPassword.setInputType(mInputType);
        }
        if (hint != null) {
            mEtPassword.setHint(hint);
        }
        if(content!=null){
            mEtPassword.setText(content);
        }
        if (maxLength != 0) {
            mEtPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)}); //最大输入长度
        }
        mTvTitle.setText(mTitle);
        DrawableUtil.addPressedDrawable(getContext(), R.drawable.shape_dialog_btn_cancel, mBtnCancel);
        DrawableUtil.addPressedDrawable(getContext(), R.drawable.shape_dialog_btn_confirm, mBtnConfirm);

        mBtnCancel.setOnClickListener(v -> {
            if (mOnCancelClickListener != null) {
                mOnCancelClickListener.onCancelClick();
            }
        });

        mBtnConfirm.setOnClickListener(v -> {
            if (mOnConfirmClickListener != null) {
                mOnConfirmClickListener.onConfirmClick(mEtPassword.getText().toString());
            }
        });
    }

    public InputWifiPasswordDialogFragment setContent(String content) {
        this.content = content;
        return this;
    }
}
