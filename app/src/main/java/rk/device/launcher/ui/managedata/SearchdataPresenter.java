package rk.device.launcher.ui.managedata;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.R;
import rk.device.launcher.db.DbRecordHelper;
import rk.device.launcher.db.entity.Record;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.ui.managedata.rv.ManageDataItemDecoration;
import rk.device.launcher.ui.managedata.rv.SearchDataRvAdapter;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SearchdataPresenter extends BasePresenterImpl<SearchdataContract.View> implements SearchdataContract.Presenter{

    private List<Record> mDataList;
    private SearchDataRvAdapter mRvAdapter;
    private List<Record> mDbAllRecordList;

    @Override
    public void initData(RecyclerView recyclerView) {
        mDbAllRecordList = DbRecordHelper.loadAll();
        mDataList = new ArrayList<>();
        recyclerView.addItemDecoration(new ManageDataItemDecoration(mContext, R.color.color_171f36));
        recyclerView.setAdapter(mRvAdapter = new SearchDataRvAdapter(mDataList));

    }

    @Override
    public void searchData(String keyword) {
        mDataList.clear();
        if (!mDbAllRecordList.isEmpty()) {
            for (Record record : mDbAllRecordList) {
                if (record.getPopeName().contains(keyword)) {
                    mDataList.add(record);
                }
            }
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

}
