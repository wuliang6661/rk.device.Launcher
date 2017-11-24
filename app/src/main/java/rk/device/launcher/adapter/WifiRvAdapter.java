package rk.device.launcher.adapter;

import android.net.wifi.ScanResult;
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
import rk.device.launcher.utils.WifiHelper;

/**
 * Created by mundane on 2017/11/13 下午3:57
 */
public class WifiRvAdapter extends RecyclerView.Adapter<WifiRvAdapter.ViewHolder> {

	private List<ScanResult> mDataList;
	private Integer mLastCheckedPosition;
	private WifiHelper mWifiHelper;

	public WifiRvAdapter(List<ScanResult> list, WifiHelper WifiHelper) {
		mDataList = list;
		mWifiHelper = WifiHelper;
	}

	public interface OnItemClickedListener{
		void onItemClicked(int position, ScanResult scanResult);

		void onLongItemClicked(int position, ScanResult scanResult);
	}

	private OnItemClickedListener mOnItemClickedListener;

	public void setOnItemClickedListener(OnItemClickedListener listener) {
		mOnItemClickedListener = listener;
	}

//	public void setNewCheckedPosition(int position) {
//		if (mLastCheckedPosition != null) {
//			if (mLastCheckedPosition == position) {
//				return;
//			} else {
//				mDataList.get(mLastCheckedPosition).isConnected = false;
//			}
//		}
//		mDataList.get(position).isConnected = true;
//		mLastCheckedPosition = position;
//	}

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
		final ScanResult scanResult = mDataList.get(position);
		holder.bind(scanResult, mWifiHelper);
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
					mOnItemClickedListener.onItemClicked(position, scanResult);
				}
			}
		});

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (mOnItemClickedListener != null) {
					mOnItemClickedListener.onLongItemClicked(position, scanResult);
				}
				return false;
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataList == null || mDataList.isEmpty() ? 0 : mDataList.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@Bind(R.id.iv_check)
		ImageView ivCheck;
		@Bind(R.id.tv_wifi_name)
		TextView tvWifiName;
		@Bind(R.id.iv_lock)
		ImageView ivLock;
		@Bind(R.id.iv_signal_strength)
		ImageView ivSignalStrength;
		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void bind(ScanResult scanResult, WifiHelper wifiHelper) {
			tvWifiName.setText(scanResult.SSID);
			// 已连接
			ivCheck.setVisibility(wifiHelper.isConnected(scanResult) ? View.VISIBLE : View.INVISIBLE);
			boolean isLocked = wifiHelper.isLocked(scanResult);
			ivLock.setVisibility(isLocked ? View.VISIBLE : View.INVISIBLE);
			//判断信号强度，显示对应的指示图标
			if (Math.abs(scanResult.level) > 100) {
				ivSignalStrength.setImageResource(R.drawable.wifi_signal_1);
			} else if (Math.abs(scanResult.level) > 80) {
				ivSignalStrength.setImageResource(R.drawable.wifi_signal_1);
			} else if (Math.abs(scanResult.level) > 70) {
				ivSignalStrength.setImageResource(R.drawable.wifi_signal_1);
			} else if (Math.abs(scanResult.level) > 60) {
				ivSignalStrength.setImageResource(R.drawable.wifi_signal_2);
			} else if (Math.abs(scanResult.level) > 50) {
				ivSignalStrength.setImageResource(R.drawable.wifi_signal_3);
			} else {
				ivSignalStrength.setImageResource(R.drawable.wifi_signal_3);
			}

		}
	}
}