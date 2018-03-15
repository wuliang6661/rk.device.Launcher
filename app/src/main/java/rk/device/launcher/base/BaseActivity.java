package rk.device.launcher.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.bean.NetDismissBO;
import rk.device.launcher.bean.event.OpenDoorSuccessEvent;
import rk.device.launcher.service.SleepTaskServer;
import rk.device.launcher.ui.fragment.BaseComDialogFragment;
import rk.device.launcher.ui.fragment.VerifyNoticeDialogFragment;
import rk.device.launcher.ui.fragment.WaitDialog;
import rk.device.launcher.ui.setting.SetNetWorkActivity;
import rk.device.launcher.ui.setting.SleepActivity;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.ResUtil;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wuliang on 2017/11/23.
 * <p>
 * <p>
 * 所有activity的基类，此处建立了一个activity的栈，用于管理activity
 * 同时存放所有界面需要的公共方法
 */

public abstract class BaseActivity extends AppCompatActivity {

    private CompositeSubscription mCompositeSubscription;


    /* 提示弹窗*/
    private BaseComDialogFragment hintDialog;

    /**
     * 验证成功或失败提示弹窗
     */
    private VerifyNoticeDialogFragment verifyNoticeDialogFragment = null;

    WaitDialog dialog;

    /**
     * 返回布局参数
     */
    protected abstract int getLayout();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        EventBus.getDefault().register(this);
        hideNavigationBar();
        ButterKnife.bind(this);
        SleepTaskServer.getSleepHandler().sendEmptyMessage(0x11);
        AppManager.getAppManager().addActivity(this);
//        makeFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        PushManager.getInstance().initialize(getApplicationContext(), RKLauncherPushService.class);
//        PushManager.getInstance().registerPushIntentService(getApplicationContext(), RKLauncherPushIntentService.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        AppManager.getAppManager().removeActivity(this);
        ButterKnife.unbind(this);
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            hintDialog.dismissAllowingStateLoss();
        }
        if (verifyNoticeDialogFragment != null && verifyNoticeDialogFragment.getDialog() != null
                && verifyNoticeDialogFragment.getDialog().isShowing()) {
            verifyNoticeDialogFragment.dismissAllowingStateLoss();
        }
        super.onDestroy();
    }


    /**
     * 有人点击重新开始休眠
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this instanceof SleepActivity) {
            return super.dispatchTouchEvent(ev);
        }
        SleepTaskServer.getSleepHandler().sendEmptyMessage(0x11);
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 常用的跳转方法
     */
    public void gotoActivity(Class<?> cls, boolean isFinish) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void gotoActivity(Class<?> cls, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 设置返回键
     */
    protected void goBack() {
        ImageView back = (ImageView) findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 设置返回键
     */
    protected void goBack(View.OnClickListener listener) {
        ImageView back = (ImageView) findViewById(R.id.iv_back);
        back.setOnClickListener(listener);
    }


    /**
     * 修改标题栏标题
     */
    protected void setTitle(String title) {
        TextView text = (TextView) findViewById(R.id.tv_title);
        text.setText(title);
    }


    /**
     * 设置标题栏右边控件，并设置事件
     */
    protected void setRightButton(int resourse, View.OnClickListener listener) {
        ImageView right = (ImageView) findViewById(R.id.title_right);
        right.setVisibility(View.VISIBLE);
        right.setBackgroundResource(resourse);
        right.setOnClickListener(listener);
    }


    /**
     * 设置标题栏右边控件隐藏
     */
    protected void hideRightButton() {
        ImageView right = (ImageView) findViewById(R.id.title_right);
        right.setVisibility(View.GONE);
    }


    /**
     * 显示提示弹窗
     */
    public BaseComDialogFragment showMessageDialog(String message) {
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            return null;
        }
        hintDialog = BaseComDialogFragment.newInstance();
        hintDialog.setMessage(message);
        hintDialog.setCancleable(true);
        if (!hintDialog.isAdded()) {
            showDialog(hintDialog);
        }
        return hintDialog;
    }


    /**
     * 显示需要自定义的提示弹窗
     */
    protected BaseComDialogFragment showMessageDialog(String message, String right, View.OnClickListener listener) {
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            return null;
        }
        hintDialog = BaseComDialogFragment.newInstance();
        hintDialog.setMessage(message);
        hintDialog.setCancleable(true);
        hintDialog.setLeftButton(ResUtil.getString(R.string.cancel), view -> hintDialog.dismiss());
        hintDialog.setRightButton(right, listener);
        if (!hintDialog.isAdded()) {
            showDialog(hintDialog);
        }
        return hintDialog;
    }

    /**
     * 显示需要自定义的提示弹窗
     */
    protected BaseComDialogFragment showMessageDialog(String title, String message, String right, View.OnClickListener listener) {
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            return null;
        }
        hintDialog = BaseComDialogFragment.newInstance();
        hintDialog.setTitle(title);
        hintDialog.setMessage(message);
        hintDialog.setCancleable(true);
        hintDialog.setLeftButton(ResUtil.getString(R.string.cancel), view -> hintDialog.dismiss());
        hintDialog.setRightButton(right, listener);
        if (!hintDialog.isAdded()) {
            showDialog(hintDialog);
        }
        return hintDialog;
    }


    /**
     * 隐藏提示弹窗
     */
    protected void dissmissMessageDialog() {
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            hintDialog.dismiss();
        }
    }

    /**
     * 显示进度条弹窗
     */
    public void showWaitProgress(String text) {
        dialog = WaitDialog.newInstance();
        dialog.setText(text);
        dialog.show(getSupportFragmentManager(), "");
    }


    /**
     * 隐藏进度条弹窗
     */
    public void hintWaitProgress() {
        if (dialog != null && dialog.getDialog() != null
                && dialog.getDialog().isShowing()) {
            dialog.dismiss();
        }
    }


    /**
     * 接收网络是否断开的监听
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetDismissBO netDismissBean) {
        if (netDismissBean.isConnect()) {
            if (hintDialog != null && hintDialog.isVisible()) {
                hintDialog.dismiss();
            }
        } else {
            if (hintDialog != null && hintDialog.isVisible()) {

            } else {
                BaseActivity.this.showNetConnect();
            }
        }
    }


    /**
     * 显示断网提示
     */
    private BaseComDialogFragment showNetConnect() {
        synchronized (this) {
            if (hintDialog != null) {
                return null;
            }
            hintDialog = BaseComDialogFragment.newInstance();
            hintDialog.setCancleable(false);
            hintDialog.setMessage(ResUtil.getString(R.string.network_cancle));
            hintDialog.setLeftButton(ResUtil.getString(R.string.cancel), view -> {
                hintDialog.dismiss();
                hintDialog = null;
            });
            hintDialog.setRightButton(ResUtil.getString(R.string.go_setting), view -> {
                BaseActivity.this.gotoActivity(SetNetWorkActivity.class, false);
                hintDialog.dismiss();
                hintDialog = null;
            });
            if (!hintDialog.isAdded()) {
                showDialog(hintDialog);
            }
            return hintDialog;
        }
    }

    private void showDialog(DialogFragment fragmentA) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(fragmentA, "fragment_name");
        ft.commit();
    }


    /**
     * 隐藏虚拟按键
     */
    public void hideNavigationBar() {
        final View decorView = getWindow().getDecorView();
        final int uiOption = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOption);
        // This code will always hide the navigation bar
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(uiOption);
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    /**
     * 给id设置监听
     */
    protected final void setOnClick(int... ids) {
        if (ids == null) {
            return;
        }
        for (int i : ids) {
            setOnClick(this.findView(i));
        }
    }

    /**
     * 给view设置监听
     */
    protected final void setOnClick(View... params) {
        if (params == null) {
            return;
        }

        for (View view : params) {
            if (view != null && this instanceof View.OnClickListener) {
                view.setOnClickListener((View.OnClickListener) this);
            }
        }
    }

    /**
     * 通过控件的Id获取对应的控件
     */
    @SuppressWarnings("unchecked")
    protected final <U extends View> U findView(int viewId) {
        View view = findViewById(viewId);
        return (U) view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OpenDoorSuccessEvent openDoorSuccessEvent) {
        if (openDoorSuccessEvent.isSuccess == 1) {
            LogUtil.i("OpenDoorSuccessEvent", "OpenDoorSuccessEvent " + AppManager.getAppManager().curremtActivity().getClass().getName());
            showSuccessDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getAppManager().goBackMain();
                }
            }, 2000);
        } else {
            showFailDialog();
        }
    }


    /**
     * 验证通过dialog
     */
    private void showSuccessDialog() {
        if (!PackageUtils.isForeground(BaseActivity.this)) {
            return;
        }
        if (verifyNoticeDialogFragment == null) {
            verifyNoticeDialogFragment = VerifyNoticeDialogFragment.newInstance();
        }
        verifyNoticeDialogFragment.setStatusMsg(ResUtil.getString(R.string.verify_success)).setStatusImg(R.mipmap.verify_success);
        verifyNoticeDialogFragment.showDialog(((FragmentActivity) AppManager.getAppManager().curremtActivity()).getSupportFragmentManager());
    }


    /**
     * 验证失败dialog
     */
    private void showFailDialog() {
        if (verifyNoticeDialogFragment == null) {
            verifyNoticeDialogFragment = VerifyNoticeDialogFragment.newInstance();
        }
        verifyNoticeDialogFragment.setStatusMsg(ResUtil.getString(R.string.verify_error)).setStatusImg(R.drawable.icon_recovery_success);
        verifyNoticeDialogFragment.showDialog(((FragmentActivity) AppManager.getAppManager().curremtActivity()).getSupportFragmentManager());
    }

}
