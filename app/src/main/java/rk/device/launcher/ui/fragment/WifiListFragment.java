package rk.device.launcher.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.adapter.WifiRvAdapter;
import rk.device.launcher.api.T;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.NetDismissBean;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.NetUtils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.widget.itemdecoration.WifiListRvItemDecoration;
import rx.Subscription;
import rx.functions.Action1;

public class WifiListFragment extends Fragment {
	
	
	@Bind(R.id.rv)
	RecyclerView mRv;
	@Bind(R.id.ib_refresh)
	ImageButton mIbRefresh;
	private List<ScanResult> mScanResultList;
	private WifiRvAdapter mWifiRvAdapter;
	private WifiHelper mWifiHelper;
	private Context mContext;
	private Subscription mSubscription;
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
		updateWifiList();
		registerWifiReceiver();
		mSubscription = RxBus.getDefault().toObserverable(NetDismissBean.class).subscribe(new Action1<NetDismissBean>() {
			@Override
			public void call(NetDismissBean netDismissBean) {
				if (netDismissBean.isConnect()) {
					updateWifiList();
				}
			}
		}, throwable -> {        //处理异常
			
		});
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
		mScanResultList = new ArrayList<>();
//		mScanResultList = new ArrayList<>();
//		for (ScanResult scanResult : scanResultList) {
//			mScanResultList.add(new ScanResultWrappedBean(scanResult));
//		}
		
		mWifiRvAdapter = new WifiRvAdapter(mScanResultList, mWifiHelper);
		mWifiRvAdapter.setOnItemClickedListener(new WifiRvAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(final int position, final ScanResult scanResult, WifiRvAdapter.ViewHolder viewHolder) {
				// 如果已经连接到该wifi上, 有取消和忘记两种选择
				if (mWifiHelper.isConnected(scanResult)) {
					if (viewHolder != null && viewHolder.ivCheck != null) {
						if (viewHolder.ivCheck.getVisibility() != View.VISIBLE) {
							T.showShort("正在连接中, 请稍候");
						}
					}
//					cancelOrForgetPassword(scanResult);
				} else { // 如果没有连接到该wifi上, 点击进行连接
					// 如果本机已经配置过的话, 直接连接
					if (mWifiHelper.isExist(scanResult) != null) {
						connectToThisWifiDirectly(scanResult);
						return;
					}
					if (mWifiHelper.isLocked(scanResult)) { // 加了密的, 如果本机没有配置过, 需要输入密码进行连接
						connectToThisWifiWithPassword(scanResult);
						return;
					} else { // 没有配置过并且没有加密的wifi
						connectToNoPasswordWifi(scanResult);
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
		mIbRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateWifiList();
			}
		});
	}
	
	private void connectToNoPasswordWifi(ScanResult scanResult) {
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
				int netId = mWifiHelper.CreateWifiInfo2(scanResult, "");
				boolean connectResult = mWifiHelper.ConnectToNetId(netId);
				LogUtil.d(TAG, "connectResult = " + connectResult);
				
				mWifiRvAdapter.notifyDataSetChanged();
				confirmDialogFragment.dismiss();
			}
		});
		confirmDialogFragment.show(getFragmentManager(), "");
		
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
				mWifiRvAdapter.notifyDataSetChanged();
				confirmDialogFragment.dismiss();
			}
		});
		confirmDialogFragment.show(getFragmentManager(), "");
	}
	
	private void cancelOrForgetPassword(final ScanResult scanResult) {
		String ip = NetUtils.getIP(getContext());
		String netMask = NetUtils.getNetMask();
		String gateWay = NetUtils.getGateWay(getContext());
		String dns1 = NetUtils.getDns1(getContext());
		LogUtil.d("ip = " + ip + ", netMask = " + netMask + ", gateWay = " + gateWay + ", dns1 = " + dns1);
		WifiDetailDialogFragment wifiDetailDialogFragment = WifiDetailDialogFragment.newInstance();
		wifiDetailDialogFragment.setWifiName(scanResult.SSID)
		.setIP(ip)
		.setNetMask(netMask)
		.setNetGate(gateWay)
		.setDns(dns1);
		wifiDetailDialogFragment.setOnClickListener(new WifiDetailDialogFragment.OnClickListener() {
			@Override
			public void onLeftClicked() {
				// 这两种方法都可以
//				mWifiHelper.forgetNetWork(scanResult);
				mWifiHelper.removeNetWork(scanResult);
				wifiDetailDialogFragment.dismiss();
			}
			
			@Override
			public void onRightClicked() {
				mWifiHelper.disconnect();
				wifiDetailDialogFragment.dismiss();
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
				inputWifiPasswordDialogFragment.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
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
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//		intentFilter.addAction("android.net.wifi.STATE_CHANGE"); // ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.setPriority(1000); // 设置优先级，最高为1000
		mContext.registerReceiver(mWifiChangeBroadcaseReceiver, intentFilter);
	}
	
	private final String TAG = "WifiListFragment";
	
	private final BroadcastReceiver mWifiChangeBroadcaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				LogUtil.d(TAG, "wifi列表刷新了");
//                updateWifiList();
			} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				LogUtil.d(TAG, "wifi状态发生了变化");
				// 刷新状态显示
//				updateWifiList();
//				if (linkWifi.checkWifiState()) {
//					wifi_on_off_btn.setBackgroundResource(R.drawable.wifi_on);
//				} else {
//					wifi_on_off_btn.setBackgroundResource(R.drawable.wifi_off);
//				}
			}
			updateWifiList();
		}
	};
	
	private void updateWifiList() {
		if (mScanResultList != null && mWifiRvAdapter != null) {
			mScanResultList.clear();
			// 就算wifi没连接也要显示出可以连接的wifi
			mScanResultList.addAll(mWifiHelper.getFilteredScanResult());
			// 这里要用双重校验来判断wifi是否可用
//			if (NetWorkUtil.isWifiConnected(getContext()) && mWifiHelper.isWifiConnected()) {
//				mScanResultList.addAll(mWifiHelper.getFilteredScanResult());
//			} else {
//				LogUtil.d("wifi不可用");
//			}
//			LogUtil.d(TAG, "mScanResultList = " + mScanResultList);
			LogUtil.d(TAG, "mScanResultList.size() = " + mScanResultList.size());
			mWifiRvAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
		if (!mSubscription.isUnsubscribed()) {
			mSubscription.unsubscribe();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(mWifiChangeBroadcaseReceiver);
	}
}
