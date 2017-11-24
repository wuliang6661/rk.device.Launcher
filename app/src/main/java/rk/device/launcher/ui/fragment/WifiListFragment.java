package rk.device.launcher.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.adapter.WifiRvAdapter;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.NetUtils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.widget.itemdecoration.WifiListRvItemDecoration;

public class WifiListFragment extends Fragment {


	@Bind(R.id.rv)
	RecyclerView mRv;
//	Unbinder unbinder;
	private List<ScanResult> mScanResultList;
	private WifiRvAdapter mWifiRvAdapter;
	private WifiHelper mWifiHelper;
	private Context mContext;
	//	private WifiManager mWifiManager;

	public WifiListFragment() {
		// Required empty public constructor
	}

	public static WifiListFragment newInstance() {
		WifiListFragment fragment = new WifiListFragment();
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mContext = context;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerWifiReceiver();
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
//					cancelOrForgetPassword(scanResult);
				} else { // 如果没有连接到该wifi上, 点击进行连接
					// 如果本机已经配置过的话, 直接连接
					if (mWifiHelper.isExist(scanResult) != null) {
						connectToThisWifiDirectly(scanResult);
					} else { // 如果本机没有配置过, 需要输入密码进行连接
						connectToThisWifiWithPassword(scanResult);
					}
				}
			}

			@Override
			public void onLongItemClicked(int position, ScanResult scanResult) {
				if (mWifiHelper.isConnected(scanResult)) {
					cancelOrForgetPassword(scanResult);
				}
			}
		});
		mRv.addItemDecoration(new WifiListRvItemDecoration(getContext()));
		mRv.setAdapter(mWifiRvAdapter);
	}

	private void connectToThisWifiDirectly(final ScanResult scanResult) {
		final ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
		confirmDialogFragment
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
						WifiConfiguration config = mWifiHelper.isExist(scanResult);
						mWifiHelper.setMaxPriority(config);
						mWifiHelper.ConnectToNetId(config);
						confirmDialogFragment.dismiss();
					}
				});
		confirmDialogFragment.show(getFragmentManager(), "");
	}

	private void cancelOrForgetPassword(final ScanResult scanResult) {
		String ip = NetUtils.getIP(getContext());
		String netMask = NetUtils.getNetMask(getContext());
		String gateWay = NetUtils.getGateWay(getContext());
		String dns1 = NetUtils.getDns1(getContext());
		LogUtil.d("ip = " + ip +", netMask = " + netMask+", gateWay = " + gateWay + ", dns1 = " + dns1);
		WifiDetailDialogFragment wifiDetailDialogFragment = WifiDetailDialogFragment.newInstance();
		wifiDetailDialogFragment.setWifiName(scanResult.SSID)
				.setIP(ip)
				.setNetMask(netMask)
				.setNetGate(gateWay)
				.setDns(dns1);
		wifiDetailDialogFragment.setOnClickListener(new WifiDetailDialogFragment.OnClickListener() {
			@Override
			public void onLeftClicked() {
				mWifiHelper.forgetNetWork(scanResult);
			}

			@Override
			public void onRightClicked() {

			}
		});
		wifiDetailDialogFragment.show(getFragmentManager(), "");
	}

	private void connectToThisWifiWithPassword(final ScanResult scanResult) {
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
						inputWifiPasswordDialogFragment.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)
								.setTitle(String.format("请输入“%s”的密码", scanResult.SSID))
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
										String password = inputWifiPasswordDialogFragment.getEtText();
										mWifiHelper.connectWifiTest(scanResult.SSID, password);
//										mWifiHelper.connectToWifiWithPwd(scanResult, password);

//										mWifiRvAdapter.notifyDataSetChanged();
									}
								});
						inputWifiPasswordDialogFragment.show(getFragmentManager(), "");
					}
				});
		confirmDialogFragment.show(getFragmentManager(), "");
	}

	private void registerWifiReceiver() {
		IntentFilter labelIntentFilter = new IntentFilter();
		labelIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		labelIntentFilter.addAction("android.net.wifi.STATE_CHANGE"); // ConnectivityManager.CONNECTIVITY_ACTION);
		labelIntentFilter.setPriority(1000); // 设置优先级，最高为1000
		mContext.registerReceiver(mWifiChangeBroadcaseReceiver, labelIntentFilter);
	}


	private final BroadcastReceiver mWifiChangeBroadcaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				LogUtil.d("wifi列表刷新了");
                updateWifiList();
			} else if (action.equals("android.net.wifi.STATE_CHANGE")) {
				LogUtil.d("wifi状态发生了变化");
				// 刷新状态显示
				updateWifiList();
//				if (linkWifi.checkWifiState()) {
//					wifi_on_off_btn.setBackgroundResource(R.drawable.wifi_on);
//				} else {
//					wifi_on_off_btn.setBackgroundResource(R.drawable.wifi_off);
//				}
			}
		}
	};

	private void updateWifiList() {
		if (mScanResultList != null && mWifiRvAdapter != null) {
			mScanResultList = mWifiHelper.getFilteredScanResult();
            mWifiRvAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(mWifiChangeBroadcaseReceiver);
	}
}
