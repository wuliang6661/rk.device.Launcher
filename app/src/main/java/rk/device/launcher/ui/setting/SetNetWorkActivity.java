package rk.device.launcher.ui.setting;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.ui.key.KeyActivity;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.bean.event.IpHostEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.fragment.AutoObtainNetworkConfigFragment;
import rk.device.launcher.ui.fragment.ManualConfigFragment;
import rk.device.launcher.ui.fragment.WifiListFragment;
import rk.device.launcher.utils.AndroidBug5497Workaround;
import rk.device.launcher.utils.DrawableUtil;
import rk.device.launcher.utils.KeyBoardHelper;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;

public class SetNetWorkActivity extends BaseActivity implements View.OnClickListener, AndroidBug5497Workaround.KeyBoardChangeListener {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.ll_auto)
    LinearLayout mLlAuto;
    @Bind(R.id.ll_manul)
    LinearLayout mLlManul;
    @Bind(R.id.ll_wifi)
    LinearLayout mLlWifi;
    @Bind(R.id.iv_auto)
    ImageView mIvAuto;
    @Bind(R.id.iv_manul)
    ImageView mIvManul;
    @Bind(R.id.iv_wifi)
    ImageView mIvWifi;
    @Bind(R.id.btn_finish_setting)
    Button mBtnFinishSetting;
    @Bind(R.id.ll_content)
    LinearLayout mLlContent;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    private FragmentManager mFragmentManager;
    private String mFragmentTag = "1";
    private KeyBoardHelper mKeyBoardHelper;
    private AndroidBug5497Workaround mAndroidBug5497Workaround;

    @Override
    protected int getLayout() {
        return R.layout.activity_set_net_work;
    }


    protected void initView() {
        goBack();
        setTitle("网络设置");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAndroidBug5497Workaround = new AndroidBug5497Workaround(this);
        mAndroidBug5497Workaround.setKeyBoardChangeListener(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        mAndroidBug5497Workaround.destroyListener();
        super.onDestroy();
    }

    protected void initData() {
        mFragmentManager = getSupportFragmentManager();
        mLlAuto.setOnClickListener(this);
        mLlManul.setOnClickListener(this);
        mLlWifi.setOnClickListener(this);

        DrawableUtil.addPressedDrawable(this, R.drawable.shape_btn_finish_setting, mBtnFinishSetting);
        mBtnFinishSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentFragment instanceof ManualConfigFragment) {
                    ManualConfigFragment fragment = (ManualConfigFragment) mCurrentFragment;
                    if (!fragment.saveIpConfig()) { // IP参数没有设置成功
                        showMessageDialog("参数设置失败");
                        return;
                    }
                    try {
                        boolean result = Settings.Global.putInt(getContentResolver(), Settings.Global.NETWORK_PREFERENCE, ConnectivityManager.TYPE_ETHERNET);
                        if (!result) {
                            showMessageDialog("切换网络失败");
//							return;
                        }
                    } catch (Exception e) {
                        showMessageDialog("切换网络失败");
                        LogUtil.e(TAG, e.getMessage());
//						return;
                    }

                } else if (mCurrentFragment instanceof AutoObtainNetworkConfigFragment) {
                    AutoObtainNetworkConfigFragment fragment = (AutoObtainNetworkConfigFragment) mCurrentFragment;

                    try {
                        // 设置IP获取方式为自动获取
                        fragment.setIPConfigDHCP();
                        boolean result = Settings.Global.putInt(getContentResolver(), Settings.Global.NETWORK_PREFERENCE, ConnectivityManager.TYPE_ETHERNET);
                        if (!result) {
                            showMessageDialog("切换网络失败");
//							return;
                        }
                    } catch (Exception e) {
                        showMessageDialog("切换网络失败");
                        LogUtil.e(TAG, e.getMessage());
//						return;
                    }

                } else {
                    try {
                        boolean result = Settings.Global.putInt(getContentResolver(), Settings.Global.NETWORK_PREFERENCE, ConnectivityManager.TYPE_WIFI);
                        if (!result) {
                            showMessageDialog("切换网络失败");
//							return;
                        }
                    } catch (Exception e) {
                        showMessageDialog("切换网络失败");
                        LogUtil.e(TAG, e.getMessage());
//						return;
                    }
                }
                RxBus.getDefault().post(new IpHostEvent(true));
                if (getIntent().getBooleanExtra("isFinish", false)) {
                    finish();
                } else {
                    //判断是否是第一次
                    boolean isFirst = (boolean) SPUtils.get(Constant.IS_FIRST_SETTING, true);
                    if (isFirst) {
                        SPUtils.put(Constant.SETTING_NUM, Constant.SETTING_TYPE2);
                        gotoActivity(KeyActivity.class, true);
                    } else {
                        finish();
                    }
                }
            }
        });


        mCurrentCheckedIv = mIvAuto;
        mCurrentCheckedLl = mLlAuto;
        turnToFragment("1");
    }

    private Fragment mCurrentFragment;

    private void turnToFragment(String fragmentTag) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit);
        Fragment toFragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (mCurrentFragment != null) {
            ft.hide(mCurrentFragment);
        }
        if (toFragment != null && !toFragment.isHidden()) {
            return;
        }
        if (toFragment == null) {
            toFragment = initateFragmentByTag(fragmentTag);
            ft.add(R.id.fl_container, toFragment, fragmentTag);
        } else if (toFragment.isHidden()) {
            ft.show(toFragment);
        }
        ft.commit();
        mCurrentFragment = toFragment;


    }

    private Fragment initateFragmentByTag(String fragmentTag) {
        switch (fragmentTag) {
            case "1":
                return AutoObtainNetworkConfigFragment.newInstance();
            case "2":
                return ManualConfigFragment.newInstance();
            case "3":
                return WifiListFragment.newInstance();
        }
        return null;
    }

    private ImageView mCurrentCheckedIv;
    private LinearLayout mCurrentCheckedLl;

    @Override
    public void onClick(View v) {
        if (mCurrentCheckedIv != null) {
            mCurrentCheckedIv.setImageResource(R.drawable.circle_uncheck);
        }
        if (mCurrentCheckedLl != null) {
            mCurrentCheckedLl.setBackgroundResource(R.color.transparent);
        }
        switch (v.getId()) {
            case R.id.ll_auto:
                mIvAuto.setImageResource(R.drawable.circle_checked);
                mLlAuto.setBackgroundResource(R.color.half_transparent_white);
                mCurrentCheckedLl = mLlAuto;
                mCurrentCheckedIv = mIvAuto;
                turnToFragment("1");
                break;
            case R.id.ll_manul:
                mIvManul.setImageResource(R.drawable.circle_checked);
                mLlManul.setBackgroundResource(R.color.half_transparent_white);
                mCurrentCheckedLl = mLlManul;
                mCurrentCheckedIv = mIvManul;
                turnToFragment("2");
                break;
            case R.id.ll_wifi:
                mIvWifi.setImageResource(R.drawable.circle_checked);
                mLlWifi.setBackgroundResource(R.color.half_transparent_white);
                mCurrentCheckedLl = mLlWifi;
                mCurrentCheckedIv = mIvWifi;
                turnToFragment("3");
                break;
        }

    }

    private final String TAG = "SetNetWorkActivity";


    @Override
    public void OnKeyBoardPop() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//				mScrollView.scrollBy(0, mScrollView.getChildAt(0).getHeight());
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 200);
    }

    @Override
    public void OnKeyBoardClose() {

    }
}
