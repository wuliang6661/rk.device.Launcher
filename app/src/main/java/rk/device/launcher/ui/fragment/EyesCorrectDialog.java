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
import android.widget.Button;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.utils.ScreenUtil;
import rk.device.launcher.widget.GifView;

/**
 * Created by wuliang on 2017/12/7.
 * <p>
 * 摄像头校验的动画提示
 */

public class EyesCorrectDialog extends DialogFragment implements View.OnClickListener {


    @Bind(R.id.close_dialog)
    ImageView closeDialog;
    @Bind(R.id.gif_img)
    GifView gifImg;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;


    private onCallBack callBack;

    /**
     * 获取dialog对象
     */
    public static EyesCorrectDialog newInstance() {
        return new EyesCorrectDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_eyes_two, container, false);
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

        gifImg.setMovieResource(R.raw.gif_anim);

        btnConfirm.setOnClickListener(this);
        closeDialog.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
            case R.id.close_dialog:
                if (callBack != null) callBack.start();
                dismiss();
                break;
        }
    }

    public void setonCallBack(onCallBack callBack) {
        this.callBack = callBack;
    }


    public interface onCallBack {

        /**
         * 开始校验
         */
        void start();

    }

}
