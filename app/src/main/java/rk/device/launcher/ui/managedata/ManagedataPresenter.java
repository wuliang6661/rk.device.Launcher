package rk.device.launcher.ui.managedata;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.R;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.ui.managedata.bean.OpenDoorTypeBean;
import rk.device.launcher.ui.managedata.bean.UserBean;
import rk.device.launcher.ui.managedata.popup.SelectTypePopupWindow;
import rk.device.launcher.ui.managedata.rv.ManageDataItemDecoration;
import rk.device.launcher.ui.managedata.rv.ManageDataRvAdapter;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class ManagedataPresenter extends BasePresenterImpl<ManagedataContract.View> implements ManagedataContract.Presenter, SelectTypePopupWindow.OnItemClickedListener, PopupWindow.OnDismissListener {

    private ManageDataRvAdapter mRvAdapter;
    private List<UserBean> mDataList;
    private SelectTypePopupWindow mSelectTypePopupWindow;

    @Override
    public void goToSearchActivity() {
        Intent intent = new Intent(mContext, SearchdataActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(RecyclerView recyclerView) {
        mDataList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new ManageDataItemDecoration(mContext, R.color.color_171f36));
        testData();
        mRvAdapter = new ManageDataRvAdapter(mDataList);
        recyclerView.setAdapter(mRvAdapter);

        View contentView = View.inflate(mContext, R.layout.layout_popup_selecttype, null);
        mSelectTypePopupWindow = new SelectTypePopupWindow(contentView, mContext);
        mSelectTypePopupWindow.setOnItemClickedListener(this);
        mSelectTypePopupWindow.setOnDismissListener(this);
    }

    @Override
    public void popupMenu(View anchor) {
        mSelectTypePopupWindow.showAsDropDown(anchor);
    }

    private void testData() {
        for (int i = 0; i < 10; i++) {
            mDataList.add(new UserBean("张三三", "12-30", "19:30:12", "密码开门"));
        }
    }

    @Override
    public void onItemClicked(OpenDoorTypeBean bean) {
        mView.refreshTypeText(bean.name);
        // todo 筛选数据然后刷新adapter
    }

    @Override
    public void onDismiss() {
        mView.dismissPopupWindow();
    }
}
