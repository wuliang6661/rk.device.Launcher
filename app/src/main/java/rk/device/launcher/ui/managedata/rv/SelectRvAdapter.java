package rk.device.launcher.ui.managedata.rv;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.ui.managedata.bean.OpenDoorTypeBean;

/**
 * Created by mundane on 2018/1/5 下午4:34
 */
public class SelectRvAdapter extends RecyclerView.Adapter<SelectRvAdapter.ViewHolder> {

    private List<OpenDoorTypeBean> mDataList;

    public SelectRvAdapter(List<OpenDoorTypeBean> list) {
        mDataList = list;
    }

    private @LayoutRes int provideItemLayout() {
        return R.layout.item_select_type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(provideItemLayout(), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null || mDataList.isEmpty() ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_type)
        TextView tvType;
        @Bind(R.id.iv_check)
        ImageView ivCheck;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(OpenDoorTypeBean o) {
            tvType.setText(o.name);
        }
    }
}