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
import rk.device.launcher.ui.managedata.rv.SelectTypeDecoration;
import rk.device.launcher.utils.ResUtil;

/**
 * Created by mundane on 2018/1/5 下午4:05
 */


public class SelectTypePopupWindow extends PopupWindow implements SelectRvAdapter.OnItemClickedListener {
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
        mDataList.add(new OpenDoorTypeBean(ResUtil.getString(R.string.all_classify), 0, true));
        mDataList.add(new OpenDoorTypeBean(ResUtil.getString(R.string.face_open), 3));
        mDataList.add(new OpenDoorTypeBean(ResUtil.getString(R.string.pwd_open), 4));
        mDataList.add(new OpenDoorTypeBean(ResUtil.getString(R.string.card_open), 1));
        mDataList.add(new OpenDoorTypeBean(ResUtil.getString(R.string.finder_open), 2));
        mDataList.add(new OpenDoorTypeBean(ResUtil.getString(R.string.qr_code_open), 5));
        mDataList.add(new OpenDoorTypeBean(ResUtil.getString(R.string.open_yuancheng), 6));
        mRv.setLayoutManager(new LinearLayoutManager(context));
        mRv.addItemDecoration(new SelectTypeDecoration(context, R.color.color_2d5581));
        mRv.setAdapter(mAdapter = new SelectRvAdapter(mDataList));
        mAdapter.setOnItemClickedListener(this);
    }

    public interface OnItemClickedListener {
        void onItemClicked(OpenDoorTypeBean bean);
    }

    private OnItemClickedListener mOnItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mOnItemClickedListener = listener;
    }


    @Override
    public void onItemClicked(int position) {
        OpenDoorTypeBean clickedBean = mDataList.get(position);
        clickedBean.isChecked = true;
        mAdapter.notifyDataSetChanged();
        if (mOnItemClickedListener != null) {
            mOnItemClickedListener.onItemClicked(clickedBean);
        }
        dismiss();
    }
}
