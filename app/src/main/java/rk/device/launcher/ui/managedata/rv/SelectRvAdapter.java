package rk.device.launcher.ui.managedata.rv;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
    private Integer mLastCheckedPosition;

    public SelectRvAdapter(List<OpenDoorTypeBean> list) {
        mDataList = list;
    }

    public interface OnItemClickedListener{
        void onItemClicked(int position);
    }

    private OnItemClickedListener mOnItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        mOnItemClickedListener = listener;
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
        holder.bind(mDataList.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    if (mLastCheckedPosition != null) {
                        mDataList.get(mLastCheckedPosition).isChecked = false;
                    }
                    mOnItemClickedListener.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null || mDataList.isEmpty() ? 0 : mDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_type)
        TextView tvType;
        @Bind(R.id.iv_check)
        ImageView ivCheck;
        @Bind(R.id.fl)
        FrameLayout fl;
        Context context;
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bind(OpenDoorTypeBean o, int position) {
            tvType.setText(o.name);
            if (o.isChecked) {
                fl.setBackgroundResource(R.color.color_203656);
                ivCheck.setVisibility(View.VISIBLE);
                mLastCheckedPosition = position;
            } else {
                fl.setBackgroundResource(R.color.color_182037);
                ivCheck.setVisibility(View.GONE);
            }
        }
    }
}