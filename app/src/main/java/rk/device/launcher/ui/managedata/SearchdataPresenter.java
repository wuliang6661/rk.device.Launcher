package rk.device.launcher.ui.managedata;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.R;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.ui.managedata.bean.UserBean;
import rk.device.launcher.ui.managedata.rv.ManageDataItemDecoration;
import rk.device.launcher.ui.managedata.rv.SearchDataRvAdapter;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SearchdataPresenter extends BasePresenterImpl<SearchdataContract.View> implements SearchdataContract.Presenter{

    private List<UserBean> mDataList;
    private SearchDataRvAdapter mRvAdapter;

    @Override
    public void initData(RecyclerView recyclerView) {
        mDataList = new ArrayList<>();
        recyclerView.addItemDecoration(new ManageDataItemDecoration(mContext, R.color.color_171f36));
        testData();
        recyclerView.setAdapter(mRvAdapter = new SearchDataRvAdapter(mDataList));

    }

    @Override
    public void searchData(String keyword) {
        // todo 只是测试
        if (TextUtils.equals("123", keyword)) {
            mDataList.clear();
        } else {
            testData();
        }
        mRvAdapter.notifyDataSetChanged();

    }

    @Override
    public void immedidateSearchData(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            mRvAdapter.setIsSearching(false);
            mRvAdapter.notifyDataSetChanged();
            return;
        } else {
            mRvAdapter.setIsSearching(true);
        }
        searchData(keyword);
    }

    private void testData() {
        for (int i = 0; i < 10; i++) {
            mDataList.add(new UserBean("张三三", "12-30", "19:30:12", "密码开门"));
        }
    }
}
