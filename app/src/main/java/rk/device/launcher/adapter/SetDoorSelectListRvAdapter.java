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
import rk.device.launcher.bean.SetDoorRvBean;

/**
 * Created by mundane on 2017/11/14 下午2:58
 */
public class SetDoorSelectListRvAdapter extends RecyclerView.Adapter<SetDoorSelectListRvAdapter.ViewHolder> {

	private List<SetDoorRvBean> mDataList;
	private Integer mLastCheckedPosition;

	public SetDoorSelectListRvAdapter(List<SetDoorRvBean> list, int lastCheckedPosition) {
		mDataList = list;
		mLastCheckedPosition = new Integer(lastCheckedPosition);
	}

	public SetDoorSelectListRvAdapter(List<SetDoorRvBean> dataList) {
		mDataList = dataList;
		for (int i = 0; i < dataList.size(); i++) {
			SetDoorRvBean setDoorRvBean = dataList.get(i);
			if (setDoorRvBean.isChecked) {
				mLastCheckedPosition = new Integer(i);
			}
		}
	}

	private @LayoutRes int provideItemLayout() {
		return R.layout.item_select_item_list;
	}

	public interface OnItemClickedListener{
		void onItemClicked(int position);
	}

	private OnItemClickedListener mOnItemClickedListener;

	public void setOnItemClickedListener(OnItemClickedListener listener) {
		mOnItemClickedListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(provideItemLayout(), parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		SetDoorRvBean setDoorRvBean = mDataList.get(position);
//		if (setDoorRvBean.isChecked) {
//			mLastCheckedPosition = position;
//		}
		holder.bind(setDoorRvBean);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnItemClickedListener != null) {
					if (mLastCheckedPosition != null && mLastCheckedPosition.intValue() != position) {
						mDataList.get(mLastCheckedPosition).isChecked = false;
					} else if (mLastCheckedPosition !=null && mLastCheckedPosition == position) {
						return;
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

		@BindView(R.id.tv_name)
		TextView tvName;
		@BindView(R.id.iv_check)
		ImageView ivCheck;
		public ViewHolder(View itemView) {
			super(itemView);
			// findViewById
			ButterKnife.bind(this, itemView);
		}

		public void bind(SetDoorRvBean bean) {
			ivCheck.setVisibility(bean.isChecked ? View.VISIBLE : View.INVISIBLE);

			tvName.setText(bean.text);
		}
	}
}