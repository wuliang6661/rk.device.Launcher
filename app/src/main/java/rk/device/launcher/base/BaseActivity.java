package rk.device.launcher.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.bean.NetDismissBO;
import rk.device.launcher.service.BlueToothsBroadcastReceiver;
import rk.device.launcher.service.RKLauncherPushIntentService;
import rk.device.launcher.service.RKLauncherPushService;
import rk.device.launcher.service.SleepTaskServer;
import rk.device.launcher.ui.setting.SetNetWorkActivity;
import rk.device.launcher.ui.setting.SleepActivity;
import rk.device.launcher.ui.fragment.BaseDialogFragment;
import rk.device.launcher.ui.fragment.WaitDialog;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.rxjava.RxBus;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wuliang on 2017/11/23.
 * <p>
 * <p>
 * 所有activity的基类，此处建立了一个activity的栈，用于管理activity
 * 同时存放所有界面需要的公共方法
 */

public abstract class BaseActivity extends RxAppCompatActivity {

    private CompositeSubscription mCompositeSubscription;


    /* 提示弹窗*/
    private BaseDialogFragment hintDialog;

    private Subscription subscription;

    /***
     * 屏幕锁
     */
    private PowerManager.WakeLock wakeLock;

    WaitDialog dialog;

    /**
     * 返回布局参数
     */
    protected abstract int getLayout();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
//        getWindow().addFlags(WindowManager.LayoutParams.F);
        hideNavigationBar();
        ButterKnife.bind(this);
        SleepTaskServer.getSleepHandler(this).sendEmptyMessage(0x11);
        AppManager.getAppManager().addActivity(this);
        setNetListener();
        makeFilters();
        ApiService.setActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireWakeLock();
        PushManager.getInstance().initialize(getApplicationContext(), RKLauncherPushService.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), RKLauncherPushIntentService.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
        ButterKnife.unbind(this);
        unregisterReceiver(mReceiver);
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            hintDialog.dismiss();
        }
    }


    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }

    }

    //释放锁
    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }


    /**
     * 有人点击重新开始休眠
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this instanceof SleepActivity) {
            return super.dispatchTouchEvent(ev);
        }
        SleepTaskServer.getSleepHandler(this).sendEmptyMessage(0x11);
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
    protected BaseDialogFragment showMessageDialog(String message) {
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            return null;
        }
        hintDialog = BaseDialogFragment.newInstance();
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
    protected BaseDialogFragment showMessageDialog(String message, String right, View.OnClickListener listener) {
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            return null;
        }
        hintDialog = BaseDialogFragment.newInstance();
        hintDialog.setMessage(message);
        hintDialog.setCancleable(true);
        hintDialog.setLeftButton("取消", view -> hintDialog.dismiss());
        hintDialog.setRightButton(right, listener);
        if (!hintDialog.isAdded()) {
            showDialog(hintDialog);
        }
        return hintDialog;
    }

    /**
     * 显示需要自定义的提示弹窗
     */
    protected BaseDialogFragment showMessageDialog(String title,String message, String right, View.OnClickListener listener) {
        if (hintDialog != null && hintDialog.getDialog() != null
                && hintDialog.getDialog().isShowing()) {
            return null;
        }
        hintDialog = BaseDialogFragment.newInstance();
        hintDialog.setTitle(title);
        hintDialog.setMessage(message);
        hintDialog.setCancleable(true);
        hintDialog.setLeftButton("取消", view -> hintDialog.dismiss());
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
            Log.d("wuliang", "隐藏转转弹窗");
            dialog.dismiss();
        }
    }


    /**
     * 接收网络是否断开的监听
     */
    private void setNetListener() {
        subscription = RxBus.getDefault().toObserverable(NetDismissBO.class).subscribe(new Action1<NetDismissBO>() {
            @Override
            public void call(NetDismissBO netDismissBean) {
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
        }, throwable -> {        //处理异常
            throwable.printStackTrace();
        });
    }


    /**
     * 显示断网提示
     */
    private BaseDialogFragment showNetConnect() {
        synchronized (this) {
            if (hintDialog != null) {
                return null;
            }
            Log.d("wuliang", "net dialog show!!");
            hintDialog = BaseDialogFragment.newInstance();
            hintDialog.setCancleable(false);
            hintDialog.setMessage("网络已断开，请重新连接");
            hintDialog.setLeftButton("取消", view -> {
                hintDialog.dismiss();
                hintDialog = null;
            });
            hintDialog.setRightButton("去设置", view -> {
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


    private BlueToothsBroadcastReceiver mReceiver;

    //蓝牙监听需要添加的Action
    private IntentFilter makeFilters() {
        mReceiver = new BlueToothsBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        registerReceiver(mReceiver, intentFilter);
        return intentFilter;
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
     *
     * @param ids
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
     *
     * @param params
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
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    protected final <U extends View> U findView(int viewId) {
        View view = findViewById(viewId);
        return (U) view;
    }

}
