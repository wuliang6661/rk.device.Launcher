package rk.device.launcher.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.NetDismissBean;
import rk.device.launcher.service.BlueToothsBroadcastReceiver;
import rk.device.launcher.service.RKLauncherPushIntentService;
import rk.device.launcher.service.RKLauncherPushService;
import rk.device.launcher.ui.activity.SetNetWorkActivity;
import rk.device.launcher.ui.fragment.BaseDialogFragment;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.NetUtils;
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

public abstract class BaseCompatActivity extends AppCompatActivity {

    private CompositeSubscription mCompositeSubscription;


    /* 提示弹窗*/
    private BaseDialogFragment hintDialog;

    private Subscription subscription;

    /**
     * 返回布局参数
     */
    protected abstract int getLayout();


    /**
     * 初始化界面布局
     */
    protected abstract void initView();

    /**
     * 处理业务逻辑
     */
    protected abstract void initData();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        hideNavigationBar();
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(this);
        setNetListener();
        makeFilters();
        initView();
        initData();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		PushManager.getInstance().initialize(getApplicationContext(), RKLauncherPushService.class);
		PushManager.getInstance().registerPushIntentService(getApplicationContext(), RKLauncherPushIntentService.class);
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
     * 修改标题栏标题
     */
    protected void setTitle(String title) {
        TextView text = (TextView) findViewById(R.id.tv_title);
        text.setText(title);
    }


    /**
     * 显示提示弹窗
     */
    protected BaseDialogFragment showMessageDialog(String message) {
        hintDialog = BaseDialogFragment.newInstance();
        hintDialog.setMessage(message);
        hintDialog.setCancleable(true);
        hintDialog.show(getSupportFragmentManager(), "hint");
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
     * 接收网络是否断开的监听
     */
    private void setNetListener() {
        subscription = RxBus.getDefault().toObserverable(NetDismissBean.class).subscribe(new Action1<NetDismissBean>() {
            @Override
            public void call(NetDismissBean netDismissBean) {
                if (netDismissBean.isConnect()) {
                    if (hintDialog != null && hintDialog.getDialog() != null
                            && hintDialog.getDialog().isShowing()) {
                        hintDialog.dismiss();
                    }
                } else {
                    if (hintDialog != null && hintDialog.getDialog() != null
                            && hintDialog.getDialog().isShowing()) {
                    } else {
                        BaseCompatActivity.this.showNetConnect();
                    }
                }
            }
        }, throwable -> {        //处理异常

        });
    }

    /**
     * 判断网络是否连接
     */
    private void isNetConnect() {
        if (!NetUtils.isNetworkConnected(this)) {
            showNetConnect();
        }
    }


    /**
     * 显示断网提示
     */
    private BaseDialogFragment showNetConnect() {
        hintDialog = BaseDialogFragment.newInstance();
        hintDialog.setCancleable(false);
        hintDialog.setMessage("网络已断开，请重新连接");
        hintDialog.setLeftButton("取消", view -> hintDialog.dismiss());
        hintDialog.setRightButton("去设置", view -> {
            BaseCompatActivity.this.gotoActivity(SetNetWorkActivity.class, false);
            hintDialog.dismiss();
        });
        hintDialog.show(getSupportFragmentManager(), "");
        return hintDialog;
    }

    /**
     * 全局监听蓝牙连接状态
     */
    private void registerBlueListener() {



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
