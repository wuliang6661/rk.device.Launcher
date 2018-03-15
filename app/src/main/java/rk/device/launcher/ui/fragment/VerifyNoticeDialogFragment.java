package rk.device.launcher.ui.fragment;

import android.annotation.Nullable;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import rk.device.launcher.R;
import rk.device.launcher.base.BaseDialogFragment;
import rk.device.launcher.utils.ScreenUtil;

/**
 * Created by hanbin on 2018/1/15.
 */

public class VerifyNoticeDialogFragment extends BaseDialogFragment {

    private static VerifyNoticeDialogFragment dialogFragment = null;
    private ImageView statusImg;
    private TextView noticeStatusTv;
    private String statusMsg;
    private int statusImgDrawable;

    /**
     * 获取dialog对象
     */
    public static VerifyNoticeDialogFragment newInstance() {
        if (dialogFragment == null) {
            synchronized (VerifyNoticeDialogFragment.class) {
                if (dialogFragment == null) {
                    dialogFragment = new VerifyNoticeDialogFragment();
                }
            }
        }
        return dialogFragment;
    }

    private VerifyNoticeDialogFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_verify_notice, container, false);
        statusImg = rootView.findViewById(R.id.img_status);
        noticeStatusTv = rootView.findViewById(R.id.tv_notice);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (noticeStatusTv != null) {
            noticeStatusTv.setText(statusMsg);
        }
        if (statusImg != null) {
            statusImg.setImageDrawable(getResources().getDrawable(statusImgDrawable));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ScreenUtil.getScreenWidth(getActivity()) - 300;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public VerifyNoticeDialogFragment setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
        return dialogFragment;
    }

    public VerifyNoticeDialogFragment setStatusImg(int statusImgDrawable) {
        this.statusImgDrawable = statusImgDrawable;
        return dialogFragment;
    }

    private boolean isShowing = false;

    public void showDialog(FragmentManager supportFragmentManager) {
        if (dialogFragment == null) {
            return;
        }
        if (!isShowing) {
            isShowing = true;
            FragmentTransaction ft = supportFragmentManager.beginTransaction();
            ft.add(dialogFragment, "verifyNotice");
            ft.commitAllowingStateLoss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isShowing) {
                        isShowing = false;
                        Fragment fragment = supportFragmentManager
                                .findFragmentByTag("verifyNotice");
                        if (fragment != null) {
                            dialogFragment.hideDialog();
                        }
                    }
                }
            }, 800);
        }
    }

    public void hideDialog() {
        if (dialogFragment == null) {
            return;
        }
        dialogFragment.dismissAllowingStateLoss();
    }
}
