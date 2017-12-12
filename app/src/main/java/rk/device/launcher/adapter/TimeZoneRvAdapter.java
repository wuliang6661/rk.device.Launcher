package rk.device.launcher.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.ui.activity.TimeZoneListActivity;

/**
 * Created by mundane on 2017/12/12 下午4:02
 */
public class TimeZoneRvAdapter extends RecyclerView.Adapter<TimeZoneRvAdapter.ViewHolder> {
	
	private List<HashMap<String, Object>> mDataList;
	
	public interface OnItemClickListerner{
		void onItemClicked(int position);
	}
	
	private OnItemClickListerner mOnItemClickedListener;
	
	public void setOnItemClickedListener(OnItemClickListerner onItemClickedListener) {
		mOnItemClickedListener = onItemClickedListener;
	}
	
	public TimeZoneRvAdapter(List<HashMap<String, Object>> list) {
		mDataList = list;
	}
	
	private
	@LayoutRes
	int provideItemLayout() {
		return R.layout.item_select_timezone;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(provideItemLayout(), parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		HashMap<String, Object> map = mDataList.get(position);
		holder.bind(map);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnItemClickedListener != null) {
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
		@Bind(R.id.tv_displaname)
		TextView tvDisplayName;
		@Bind(R.id.tv_gmt)
		TextView tvGmt;
		public ViewHolder(View itemView) {
			super(itemView);
			// findViewById
			ButterKnife.bind(this, itemView);
		}
		
		public void bind(Map<String, Object> map) {
			String displayName = (String) map.get(TimeZoneListActivity.KEY_DISPLAYNAME);
			String gmt = (String) map.get(TimeZoneListActivity.KEY_GMT);
			tvDisplayName.setText(displayName);
			tvGmt.setText(gmt);
		}
	}
}