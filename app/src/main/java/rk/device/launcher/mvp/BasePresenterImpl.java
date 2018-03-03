package rk.device.launcher.mvp;

import android.content.Context;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

// 其实我觉得这个BasePresenter也可以省略掉了, 直接就一个BasePresenterImpl就行了,
// 但是这里的BasePresenterImpl还是改成一个抽象类比较好, 增加一个onStar()的抽象方法
public class BasePresenterImpl<V extends BaseView> implements BasePresenter<V> {
    protected V mView;
    protected Context mContext;

    @Override
    public void attachView(V view) {
        mView = view;
        mContext = mView.getContext();
    }

    @Override
    public void detachView() {
        mView = null;
        mContext = null;
    }
}
