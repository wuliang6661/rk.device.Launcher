package rk.device.launcher.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.bean.WifiRvBean;

/**
 * Created by mundane on 2017/11/13 下午3:57
 */
public class WifiRvAdapter extends RecyclerView.Adapter<WifiRvAdapter.ViewHolder> {

	private List<WifiRvBean> mDataList;
	private Integer mLastCheckedPosition;

	public WifiRvAdapter(List<WifiRvBean> list) {
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
		return R.layout.item_wifi_list;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(provideItemLayout(), parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		holder.bind(mDataList.get(position));
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnItemClickedListener != null) {
					if (mLastCheckedPosition != null && mLastCheckedPosition != position) {
						mDataList.get(mLastCheckedPosition).isChecked = false;
					}
					mLastCheckedPosition = position;
					mOnItemClickedListener.onItemClicked(position);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataList == null || mDataList.isEmpty() ? 0 : mDataList.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.iv_check)
		ImageView ivCheck;
		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void bind(WifiRvBean bean) {
			ivCheck.setVisibility(bean.isChecked ? View.VISIBLE : View.INVISIBLE);
		}
	}
}