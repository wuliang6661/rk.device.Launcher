package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.utils.ScreenUtil;

/**
 * Created by wuliang on 2017/12/7.
 * <p>
 * 加载中的dialog
 */

public class WaitDialog extends DialogFragment {


    @Bind(R.id.progress_text)
    TextView progressText;

    String text;

    /**
     * 获取dialog对象
     */
    public static WaitDialog newInstance() {
        return new WaitDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_wait, container, false);
        //设置窗口以对话框样式显示
//		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
        // 设置对话框背景色，否则有虚框
        // 这句代码十分重要, 否则圆角背景的圆角永远就只有那么大
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, rootView);
        return rootView;
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


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressText.setText(text);
    }


    public void setText(String message) {
        this.text = message;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
