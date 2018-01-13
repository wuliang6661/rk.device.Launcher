package rk.device.launcher.ui.managedata.rv;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.db.entity.Record;

/**
 * Created by mundane on 2018/1/5 下午1:17
 */
public class ManageDataRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Record> mDataList;
    private static final int EMPTY_VIEW = 5;

    public ManageDataRvAdapter(List<Record> list) {
        mDataList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (viewType == EMPTY_VIEW) {
            itemView = layoutInflater.inflate(R.layout.layout_rv_empty, parent, false);
            return new EmptyViewHolder(itemView);
        } else {
            itemView = layoutInflater.inflate(R.layout.item_manage_data, parent, false);
            return new NormalViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList == null || mDataList.isEmpty()) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalViewHolder) {
            ((NormalViewHolder)holder).bind(mDataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null || mDataList.isEmpty() ? 1 : mDataList.size();
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_date)
        TextView tvDate;
        @Bind(R.id.tv_type)
        TextView tvType;
        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Record record) {
            tvName.setText(record.getPopeName());
            tvType.setText(record.getOpenTypeText());
            String dateText = record.getDateText();
            if (!TextUtils.isEmpty(dateText)) {
                tvDate.setText(dateText);
            }

        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}