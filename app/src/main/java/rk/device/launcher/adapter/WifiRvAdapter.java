package rk.device.launcher.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.bean.ScanResultWrappedBean;

/**
 * Created by mundane on 2017/11/13 下午3:57
 */
public class WifiRvAdapter extends RecyclerView.Adapter<WifiRvAdapter.ViewHolder> {

	private List<ScanResultWrappedBean> mDataList;
	private Integer mLastCheckedPosition;

	public WifiRvAdapter(List<ScanResultWrappedBean> list) {
		mDataList = list;
	}

	public interface OnItemClickedListener{
		void onItemClicked(int position);
	}

	private OnItemClickedListener mOnItemClickedListener;

	public void setOnItemClickedListener(OnItemClickedListener listener) {
		mOnItemClickedListener = listener;
	}

	public void setNewCheckedPosition(int position) {
		if (mLastCheckedPosition != null) {
			if (mLastCheckedPosition == position) {
				return;
			} else {
				mDataList.get(mLastCheckedPosition).isConnected = false;
			}
		}
		mDataList.get(position).isConnected = true;
		mLastCheckedPosition = position;
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
					// 存在上一个被选中的条目, 并且上一个被选中的条目不是当前点击的这个条目, 将上一个条目置空
//					if (mLastCheckedPosition != null && mLastCheckedPosition.intValue() != position) {
//						mDataList.get(mLastCheckedPosition).isChecked = false;
//					} else if (mLastCheckedPosition!=null && mLastCheckedPosition == position) {
//						return;
//					}
//					mLastCheckedPosition = position;
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
		@BindView(R.id.tv_wifi_name)
		TextView tvWifiName;
		@BindView(R.id.iv_lock)
		ImageView ivLock;
		@BindView(R.id.iv_signal_strength)
		ImageView ivSignalStrength;
		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void bind(ScanResultWrappedBean bean) {
			ivCheck.setVisibility(bean.isConnected ? View.VISIBLE : View.INVISIBLE);
			tvWifiName.setText(bean.scanResult.SSID);
		}
	}
}