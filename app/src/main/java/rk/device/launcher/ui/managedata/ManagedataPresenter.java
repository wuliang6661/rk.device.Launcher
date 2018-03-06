package rk.device.launcher.ui.managedata;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.rx.RxDao;

import rk.device.launcher.R;
import rk.device.launcher.db.DbRecordHelper;
import rk.device.launcher.db.entity.Record;
import rk.device.launcher.db.entity.RecordDao;
import rk.device.launcher.db.entity.RecordDao.Properties;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.ui.managedata.bean.OpenDoorTypeBean;
import rk.device.launcher.ui.managedata.popup.SelectTypePopupWindow;
import rk.device.launcher.ui.managedata.rv.ManageDataItemDecoration;
import rk.device.launcher.ui.managedata.rv.ManageDataRvAdapter;
import rk.device.launcher.utils.MD5;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new ManageDataItemDecoration(mContext, R.color.color_171f36));
//        testInsertData();
        loadData(recyclerView);


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

    private void loadData(RecyclerView recyclerView) {
        RxDao<Record, Long> recordRxDao = DbRecordHelper.getRecordDao().rx();
        recordRxDao.loadAll()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Record>>() {
                    @Override
                    public void call(List<Record> records) {
                        if (mView != null)
                            mView.hideProgress();
                        mDataList.clear();
                        Collections.reverse(records);
                        mDataList.addAll(records);
                        mRvAdapter = new ManageDataRvAdapter(mDataList);
                        recyclerView.setAdapter(mRvAdapter);
                    }
                });
    }

    @Override
    public void onItemClicked(OpenDoorTypeBean bean) {
        if (mView != null)
            mView.refreshTypeText(bean.name);
        // 筛选数据然后刷新adapter
        RecordDao recordDao = DbRecordHelper.getRecordDao();
        List<Record> recordList;
        Query<Record> query;
        if (bean.typeId == 0) {
            query = recordDao.queryBuilder().orderDesc(Properties.Cdate).build();
            recordList = query.list();
        } else {
            query = recordDao.queryBuilder().orderDesc(Properties.Cdate)
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
        if (mView != null)
            mView.dismissPopupWindow();
    }
}
