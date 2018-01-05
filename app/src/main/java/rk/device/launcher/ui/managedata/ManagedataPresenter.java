package rk.device.launcher.ui.managedata;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.R;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.ui.managedata.bean.UserBean;
import rk.device.launcher.ui.managedata.popup.SelectTypePopupWindow;
import rk.device.launcher.ui.managedata.rv.ManageDataItemDecoration;
import rk.device.launcher.ui.managedata.rv.ManageDataRvAdapter;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class ManagedataPresenter extends BasePresenterImpl<ManagedataContract.View> implements ManagedataContract.Presenter{

    private ManageDataRvAdapter mRvAdapter;
    private List<UserBean> mDataList;

    @Override
    public void goToSearchActivity() {

    }

    @Override
    public void initData(RecyclerView recyclerView) {
        mDataList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new ManageDataItemDecoration(mContext));
//        testData();
        mRvAdapter = new ManageDataRvAdapter(mDataList);
        recyclerView.setAdapter(mRvAdapter);
    }

    @Override
    public void popupMenu(View anchor) {
        View contentView = View.inflate(mContext, R.layout.layout_popup_selecttype, null);
        SelectTypePopupWindow selectTypePopupWindow = new SelectTypePopupWindow(contentView, mContext);
        selectTypePopupWindow.showAsDropDown(anchor);
    }

    private void testData() {
        for (int i = 0; i < 10; i++) {
            mDataList.add(new UserBean("张三三", "12-30", "19:30:12", "密码开门"));
        }
    }
}
