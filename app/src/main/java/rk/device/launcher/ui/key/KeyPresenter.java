package rk.device.launcher.ui.key;

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
        BaseApiImpl.activationDiveces(uuid, mac, license).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                if (mView != null)
                    mView.onRequestEnd();
            }

            @Override
            public void onError(Throwable e) {
                if (mView != null)
                    mView.onRequestError(e.getMessage());
            }

            @Override
            public void onNext(Object s) {
                if (mView != null)
                    mView.onSuress();
            }
        });
    }
}
