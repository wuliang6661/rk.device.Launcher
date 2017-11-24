package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.utils.ScreenUtil;

/**
 * Created by wuliang on 2017/11/24.
 * <p>
 * 提示用的Dialog弹窗
 */

public class BaseDialogFragment extends DialogFragment {


    @Bind(R.id.message)
    TextView message;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;


    private CharSequence messageStr;
    private CharSequence leftStr;
    private CharSequence rightStr;
    private boolean isCancleAble = true;
    private View.OnClickListener leftListener;
    private View.OnClickListener rightListener;


    /**
     * 获取dialog对象
     */
    public static BaseDialogFragment newInstance() {
        BaseDialogFragment dialogFragment = new BaseDialogFragment();
        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_hint, container, false);
        //设置窗口以对话框样式显示
//		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
        // 设置对话框背景色，否则有虚框
        // 这句代码十分重要, 否则圆角背景的圆角永远就只有那么大
        getDialog().setCanceledOnTouchOutside(isCancleAble);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        message.setText(messageStr);
        if (leftListener == null) {
            btnCancel.setText("知道啦");
            btnCancel.setOnClickListener(view12 -> dismiss());
        } else {
            btnCancel.setText(leftStr);
            btnCancel.setOnClickListener(leftListener);
        }
        if (rightListener == null) {
            btnConfirm.setVisibility(View.GONE);
        } else {
            btnConfirm.setVisibility(View.VISIBLE);
            btnConfirm.setText(rightStr);
            btnConfirm.setOnClickListener(rightListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ScreenUtil.getScreenWidth(getContext()) - 300;
        window.setAttributes(params);
    }

    /**
     * 设置提示文本
     */
    public BaseDialogFragment setMessage(CharSequence message) {
        this.messageStr = message;
        return this;
    }

    /**
     * 设置显示右边确定按钮文本及点击事件
     * <p>
     * （如不设置默认隐藏）
     */
    public BaseDialogFragment setRightButton(CharSequence message, View.OnClickListener listener) {
        this.rightStr = message;
        this.rightListener = listener;
        return this;
    }


    /**
     * 设置左边按钮及点击事件
     * <p>
     * （如不设置默认显示 “知道啦” ，点击弹窗消失）
     */
    public BaseDialogFragment setLeftButton(CharSequence message, View.OnClickListener listener) {
        this.leftStr = message;
        this.leftListener = listener;
        return this;
    }

    /**
     * 设置点击屏幕是否消失
     */
    public BaseDialogFragment setCancleable(boolean cancleable) {
        this.isCancleAble = cancleable;
        return this;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
