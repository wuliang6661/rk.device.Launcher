package rk.device.launcher.ui.managedata;

import android.support.v7.widget.RecyclerView;

import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseView;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class ManagedataContract {
    interface View extends BaseView {
        void refreshTypeText(String name);

        void dismissPopupWindow();

        void showProgress();

        void hideProgress();

    }

    interface  Presenter extends BasePresenter<View> {
        void goToSearchActivity();

        void initData(RecyclerView recyclerView);

        void popupMenu(android.view.View anchor);
    }
}
