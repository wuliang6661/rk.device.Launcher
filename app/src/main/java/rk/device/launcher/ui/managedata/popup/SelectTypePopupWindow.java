package rk.device.launcher.ui.managedata.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.ui.managedata.bean.OpenDoorTypeBean;
import rk.device.launcher.ui.managedata.rv.SelectRvAdapter;

/**
 * Created by mundane on 2018/1/5 下午4:05
 */


public class SelectTypePopupWindow extends PopupWindow {
    @Bind(R.id.rv)
    RecyclerView mRv;
    private List<OpenDoorTypeBean> mDataList;
    private SelectRvAdapter mAdapter;

    public SelectTypePopupWindow(View contentView, Context context) {
        super(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, contentView);
        initData(context);
    }

    private void initData(Context context) {
        mDataList = new ArrayList<>();
        mDataList.add(new OpenDoorTypeBean("全部类型"));
        mDataList.add(new OpenDoorTypeBean("刷脸开门"));
        mDataList.add(new OpenDoorTypeBean("密码开门"));
        mDataList.add(new OpenDoorTypeBean("刷卡开门"));
        mDataList.add(new OpenDoorTypeBean("指纹开门"));
        mDataList.add(new OpenDoorTypeBean("二维码开门"));
        mDataList.add(new OpenDoorTypeBean("远程开门"));
        mRv.setLayoutManager(new LinearLayoutManager(context));
        mRv.setAdapter(mAdapter = new SelectRvAdapter(mDataList));
    }
}
