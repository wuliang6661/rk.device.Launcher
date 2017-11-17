package rk.device.launcher.ui.fragment;


import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.adapter.WifiRvAdapter;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.widget.itemdecoration.WifiListRvItemDecoration;

public class WifiListFragment extends Fragment {


	@Bind(R.id.rv)
	RecyclerView mRv;
//	Unbinder unbinder;
	private List<ScanResult> mScanResultList;
	private WifiRvAdapter mWifiRvAdapter;
	private WifiHelper mWifiHelper;
	//	private WifiManager mWifiManager;

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
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//		mWifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		mWifiHelper = new WifiHelper(getContext());
		mScanResultList = mWifiHelper.getFilteredScanResult();
//		mScanResultList = new ArrayList<>();
//		for (ScanResult scanResult : scanResultList) {
//			mScanResultList.add(new ScanResultWrappedBean(scanResult));
//		}

		mWifiRvAdapter = new WifiRvAdapter(mScanResultList, mWifiHelper);
		mWifiRvAdapter.setOnItemClickedListener(new WifiRvAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(final int position, final ScanResult scanResult) {
				// 如果已经连接到该wifi上, 有取消和忘记两种选择
				if (mWifiHelper.isConnected(scanResult)) {
					cancelOrForgetPassword(scanResult);
				} else { // 如果没有连接到该wifi上, 点击进行连接
					connectToThisWifi(scanResult);
				}
			}
		});
		mRv.addItemDecoration(new WifiListRvItemDecoration(getContext()));
		mRv.setAdapter(mWifiRvAdapter);
	}

	private void cancelOrForgetPassword(final ScanResult scanResult) {
		final ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
		confirmDialogFragment.setConfirmBtnText("忘记")
				.setMessage("执行操作会造成设备断网, 是否继续?")
				.setOnCancelClickListener(new ConfirmDialogFragment.onCancelClickListener() {
					@Override
					public void onCancelClick() {
						confirmDialogFragment.dismiss();
					}
				})
				.setOnConfirmClickListener(new ConfirmDialogFragment.OnConfirmClickListener() {
					@Override
					public void onConfirmClick() {
						mWifiHelper.removeNetWork(scanResult);
						mWifiRvAdapter.notifyDataSetChanged();
						confirmDialogFragment.dismiss();
					}
				});
		confirmDialogFragment.show(getFragmentManager(), "");
	}

	private void connectToThisWifi(final ScanResult scanResult) {
		final ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
		confirmDialogFragment.setMessage("执行操作会造成设备断网, 是否继续?")
				.setOnCancelClickListener(new ConfirmDialogFragment.onCancelClickListener() {
					@Override
					public void onCancelClick() {
						confirmDialogFragment.dismiss();
					}
				})
				.setOnConfirmClickListener(new ConfirmDialogFragment.OnConfirmClickListener() {
					@Override
					public void onConfirmClick() {
						confirmDialogFragment.dismiss();
						final InputWifiPasswordDialogFragment inputWifiPasswordDialogFragment = InputWifiPasswordDialogFragment.newInstance();
						inputWifiPasswordDialogFragment.setTitle(String.format("请输入“%s”的密码", scanResult.SSID))
								.setOnCancelClickListener(new InputWifiPasswordDialogFragment.onCancelClickListener() {
									@Override
									public void onCancelClick() {
										inputWifiPasswordDialogFragment.dismiss();
									}
								})
								.setOnConfirmClickListener(new InputWifiPasswordDialogFragment.OnConfirmClickListener() {
									@Override
									public void onConfirmClick(String content) {
										inputWifiPasswordDialogFragment.dismiss();
//												mScanResultList.get(position).isConnected = true;
//												mWifiRvAdapter.setNewCheckedPosition(position);
										mWifiRvAdapter.notifyDataSetChanged();
									}
								});
						inputWifiPasswordDialogFragment.show(getFragmentManager(), "");
					}
				});
		confirmDialogFragment.show(getFragmentManager(), "");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		unbinder.unbind();
	}
}
