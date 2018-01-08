package rk.device.launcher.ui.managedata.rv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rk.device.launcher.R;
import rk.device.launcher.ui.managedata.bean.UserBean;

/**
 * Created by mundane on 2018/1/5 下午1:17
 */
public class SearchDataRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserBean> mDataList;
    private static final int EMPTY_VIEW = 5;
    private boolean isSearchingStatus;

    public void setIsSearching(boolean status) {
        this.isSearchingStatus = status;
    }

    public SearchDataRvAdapter(List<UserBean> list) {
        mDataList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (viewType == EMPTY_VIEW) {
            itemView = layoutInflater.inflate(R.layout.layout_search_empty, parent, false);
            return new EmptyViewHolder(itemView);
        } else {
            itemView = layoutInflater.inflate(R.layout.item_manage_data, parent, false);
            return new NormalViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 处于搜索状态
        if (isSearchingStatus) {
            if (mDataList == null || mDataList.isEmpty()) {
                return EMPTY_VIEW;
            }
            return super.getItemViewType(position);
        } else { // 还没有处于搜索状态
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        // 处于搜索状态
        if (isSearchingStatus) {
            return mDataList == null || mDataList.isEmpty() ? 1 : mDataList.size();
        } else { // 还没有处于搜索状态
            return 0;
        }

    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {

        public NormalViewHolder(View itemView) {
            super(itemView);
            // findViewById
        }

        public void bind(UserBean user) {
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}