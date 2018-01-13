package rk.device.launcher.ui.key;

import android.util.Log;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.mvp.BasePresenterImpl;
import rx.Subscriber;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class KeyPresenter extends BasePresenterImpl<KeyContract.View> implements KeyContract.Presenter {

    private static final String TAG = "KeyPresenter";
    
    @Override
    public void activationDiveces(String uuid, String mac, String license) {
        Log.d(TAG, "activationDiveces() called with: uuid = [" + uuid + "], mac = [" + mac + "], license = [" + license + "]");
        BaseApiImpl.activationDiveces(uuid, mac, license).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                mView.onRequestEnd();
            }

            @Override
            public void onError(Throwable e) {
                mView.onRequestError(e.getMessage());
            }

            @Override
            public void onNext(String s) {

            }
        });
    }
}
