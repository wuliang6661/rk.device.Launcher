package rk.device.launcher.ui.main;

import android.os.Message;

import cvc.EventUtil;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    /**
     * 初始化jni
     */
    @Override
    public JniHandler initJni() {
        JniHandler mHandler = JniHandler.getInstance();
        Message msg = new Message();
        msg.what = EventUtil.INIT_JNI;
        mHandler.sendMessageDelayed(msg, 10);
        return mHandler;
    }


}
