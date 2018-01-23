package rk.device.launcher.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseDialogFragment;
import rk.device.launcher.utils.ScreenUtil;
import rk.device.launcher.utils.StringUtils;

/**
 * Created by wuliang on 2017/12/7.
 * <p>
 * 摄像头校验输入行列
 */

public class EyesCorrectDialogOneFra extends BaseDialogFragment implements View.OnClickListener {


    @Bind(R.id.close_dialog)
    ImageView closeDialog;
    @Bind(R.id.hang_num)
    EditText hangNum;
    @Bind(R.id.lie_num)
    EditText lieNum;
    @Bind(R.id.count_num)
    EditText countNum;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    private CallBack callBack;
    private int line;   //行数
    private int lie;    //列数
    private int countNumStr;    //校准次数


    /**
     * 获取dialog对象
     */
    public static EyesCorrectDialogOneFra newInstance() {
        return new EyesCorrectDialogOneFra();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_eyes_one, container, false);
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
                if (isEditMsg()) {
                    if (callBack != null) {
                        callBack.callMessage(line, lie, countNumStr);
                    }
                }
                break;
            case R.id.close_dialog:
                if (callBack != null) {
                    callBack.cancle();
                }
                break;
        }
    }


    /**
     * 判断数据输入正确性
     */
    private boolean isEditMsg() {
        String lineS = hangNum.getText().toString().trim();
        String lieS = lieNum.getText().toString().trim();
        String countS = countNum.getText().toString().trim();
        if (StringUtils.isEmpty(lieS) || StringUtils.isEmpty(lineS) || StringUtils.isEmpty(countS)) {
            T.showShort("请完善数据!");
            return false;
        }
        line = Integer.parseInt(lineS);
        lie = Integer.parseInt(lieS);
        countNumStr = Integer.parseInt(countS);
        if ((line % 2 == 0 && lie % 2 == 0) || (lie % 2 != 0 && line % 2 != 0)) {
            T.showShort("行和列须一个是偶数，一个是奇数!");
            return false;
        }
        if (countNumStr == 0) {
            T.showShort("成功次数不能为0!");
            return false;
        }
        return true;
    }


    /**
     * 设置数据回调
     */
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    public interface CallBack {

        void callMessage(int line, int lie, int countNum);


        void cancle();

    }

}
