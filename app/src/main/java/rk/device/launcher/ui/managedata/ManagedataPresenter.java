package rk.device.launcher.ui.managedata;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupWindow;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rk.device.launcher.R;
import rk.device.launcher.db.DbRecordHelper;
import rk.device.launcher.db.entity.Record;
import rk.device.launcher.db.entity.RecordDao;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.ui.managedata.bean.OpenDoorTypeBean;
import rk.device.launcher.ui.managedata.popup.SelectTypePopupWindow;
import rk.device.launcher.ui.managedata.rv.ManageDataItemDecoration;
import rk.device.launcher.ui.managedata.rv.ManageDataRvAdapter;
import rk.device.launcher.utils.MD5;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ManagedataPresenter extends BasePresenterImpl<ManagedataContract.View> implements ManagedataContract.Presenter, SelectTypePopupWindow.OnItemClickedListener, PopupWindow.OnDismissListener {

    private ManageDataRvAdapter mRvAdapter;
    private List<Record> mDataList;
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
        testInsertData();
        loadData();
        mRvAdapter = new ManageDataRvAdapter(mDataList);
        recyclerView.setAdapter(mRvAdapter);
        recyclerView.setHasFixedSize(true);

        View contentView = View.inflate(mContext, R.layout.layout_popup_selecttype, null);
        mSelectTypePopupWindow = new SelectTypePopupWindow(contentView, mContext);
        mSelectTypePopupWindow.setOnItemClickedListener(this);
        mSelectTypePopupWindow.setOnDismissListener(this);
    }

    private void testInsertData() {
        for (int i = 0; i < 100; i++) {
            Record record = new Record();
            record.setPopeName("张三三" + i);
            record.setUniqueId(MD5.get16Lowercase(UUID.randomUUID().toString()));
            record.setOpenType(i % 5 + 1); // 1 ~ 5
            record.setPeopleId(MD5.get16Lowercase(UUID.randomUUID().toString()));
            record.setCdate(1514535019L);
            DbRecordHelper.insert(record);
        }
    }

    @Override
    public void popupMenu(View anchor) {
        mSelectTypePopupWindow.showAsDropDown(anchor);
    }

    private void loadData() {
        List<Record> dbRecordList = DbRecordHelper.loadAll();
        mDataList.clear();
        mDataList.addAll(dbRecordList);
    }

    @Override
    public void onItemClicked(OpenDoorTypeBean bean) {
        mView.refreshTypeText(bean.name);
        // 筛选数据然后刷新adapter
        RecordDao recordDao = DbRecordHelper.getRecordDao();
        List<Record> recordList;
        if (bean.typeId == 0) {
            recordList = recordDao.loadAll();
        } else {
            Query<Record> query = recordDao.queryBuilder()
                    .where(RecordDao.Properties.OpenType.eq(bean.typeId))
                    .build();
            recordList = query.list();
        }
        mDataList.clear();
        mDataList.addAll(recordList);
        mRvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDismiss() {
        mView.dismissPopupWindow();
    }
}
