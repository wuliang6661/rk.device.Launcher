package rk.device.launcher.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rk.device.launcher.R;
import rk.device.launcher.adapter.WifiRvAdapter;
import rk.device.launcher.bean.WifiRvBean;
import rk.device.launcher.widget.itemdecoration.WifiListRvItemDecoration;

public class WifiListFragment extends Fragment {


	@BindView(R.id.rv)
	RecyclerView mRv;
	Unbinder unbinder;
	private List<WifiRvBean> mDataList;
	private WifiRvAdapter mWifiRvAdapter;

	public WifiListFragment() {
		// Required empty public constructor
	}

	public static WifiListFragment newInstance() {
		WifiListFragment fragment = new WifiListFragment();
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_wifi_list, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mDataList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			mDataList.add(new WifiRvBean());
		}
		mWifiRvAdapter = new WifiRvAdapter(mDataList);
		mWifiRvAdapter.setOnItemClickedListener(new WifiRvAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(final int position) {
				final ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
				confirmDialogFragment.setMessage("执行操作会造成设备断网, 是否继续?")
						.setOnCancelClickListener(new ConfirmDialogFragment.onCancelClickListener() {
							@Override
							public void onCancelClick() {
								Toast.makeText(WifiListFragment.this.getContext(), "取消", Toast.LENGTH_SHORT).show();
								confirmDialogFragment.dismiss();
							}
						})
						.setOnConfirmClickListener(new ConfirmDialogFragment.OnConfirmClickListener() {
							@Override
							public void onConfirmClick() {
								confirmDialogFragment.dismiss();
								final InputWifiPasswordDialogFragment inputWifiPasswordDialogFragment = InputWifiPasswordDialogFragment.newInstance();
								inputWifiPasswordDialogFragment.setTitle("请输入“RUANKU”的密码")
										.setOnCancelClickListener(new InputWifiPasswordDialogFragment.onCancelClickListener() {
											@Override
											public void onCancelClick() {
												Toast.makeText(WifiListFragment.this.getContext(), "取消", Toast.LENGTH_SHORT).show();
												inputWifiPasswordDialogFragment.dismiss();
											}
										})
										.setOnConfirmClickListener(new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
											@Override
											public void onConfirmClick() {
												inputWifiPasswordDialogFragment.dismiss();
												mDataList.get(position).isChecked = true;
												mWifiRvAdapter.setNewCheckedPosition(position);
												mWifiRvAdapter.notifyDataSetChanged();
											}
										});
								inputWifiPasswordDialogFragment.show(getFragmentManager(), "");
							}
						});
				confirmDialogFragment.show(getFragmentManager(), "");
			}
		});
		mRv.addItemDecoration(new WifiListRvItemDecoration(getContext()));
		mRv.setAdapter(mWifiRvAdapter);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}
}
