package rk.device.launcher.ui.managedata;

import android.support.v7.widget.RecyclerView;

import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SearchdataContract {
    interface View extends BaseView {
        
    }

    interface  Presenter extends BasePresenter<View> {
        void initData(RecyclerView rv);

        void searchData(String keyword);

        void immedidateSearchData(String keyword);
    }
}
