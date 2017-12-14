package rk.device.launcher.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;

public class AutoObtainNetworkConfigFragment extends Fragment {


	@Bind(R.id.tv_ip)
	TextView mTvIp;
	@Bind(R.id.tv_sub_code)
	TextView mTvNetMask;
	@Bind(R.id.tv_net_gate)
	TextView mTvNetGate;
	@Bind(R.id.tv_dns)
	TextView mTvDns;
	private EthernetManager mEthernetManager;

	public AutoObtainNetworkConfigFragment() {
		// Required empty public constructor
	}

	public static AutoObtainNetworkConfigFragment newInstance() {
		AutoObtainNetworkConfigFragment fragment = new AutoObtainNetworkConfigFragment();
		return fragment;
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 接收到以太网状态改变的广播
			if(EthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
				int EtherState = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, -1);
				handleEtherStateChange(EtherState);
			}
		}
	};
	
	private void handleEtherStateChange(int etherState) {
		switch (etherState) {
			case EthernetManager.ETHER_STATE_DISCONNECTED:
				mEthHwAddress = nullIpInfo;
				mEthIpAddress = nullIpInfo;
				mEthNetmask = nullIpInfo;
				mEthGateway = nullIpInfo;
				mEthdns1 = nullIpInfo;
				mEthdns2 = nullIpInfo;
				break;
			case EthernetManager.ETHER_STATE_CONNECTING:
				mEthHwAddress = STATE_CONNECTING;
				mEthIpAddress = STATE_CONNECTING;
				mEthNetmask = STATE_CONNECTING;
				mEthGateway = STATE_CONNECTING;
				mEthdns1 = STATE_CONNECTING;
				mEthdns2 = STATE_CONNECTING;
				break;
			case EthernetManager.ETHER_STATE_CONNECTED:
				getEthInfo();
				break;
			default:
				break;
		}
		refreshUI();
	}
	
	private void refreshUI() {
		mTvIp.setText(mEthIpAddress);
		mTvNetMask.setText(mEthNetmask);
		mTvDns.setText(mEthdns1);
		mTvNetGate.setText(mEthGateway);
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEthernetManager = (EthernetManager) getContext().getSystemService("ethernet");
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_auto_obtain_network_config, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//		String ip = NetUtils.getEtherNetIP();
//		mTvIp.setText(ip);
		
		getEthInfo();
        refreshUI();
		IntentFilter intentFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
		getContext().registerReceiver(mReceiver, intentFilter);
	}
	
	public void getEthInfo() {
		IpConfiguration.IpAssignment mode = mEthernetManager.getConfiguration().getIpAssignment();
		if (mode == IpConfiguration.IpAssignment.DHCP) { // getEth from dhcp
			getEthInfoFromDhcp();
		} else if (mode == IpConfiguration.IpAssignment.STATIC) { // TODO: get static IP
			getEthInfoFromStaticIp();
		}
	}


	public void getEthInfoFromStaticIp() {
		StaticIpConfiguration staticIpConfiguration = mEthernetManager.getConfiguration().getStaticIpConfiguration();

		if (staticIpConfiguration == null) {
			return;
		}
		LinkAddress ipAddress = staticIpConfiguration.ipAddress;
		InetAddress gateway = staticIpConfiguration.gateway;
		ArrayList<InetAddress> dnsServers = staticIpConfiguration.dnsServers;

		if (ipAddress != null) {
			mEthIpAddress = ipAddress.getAddress().getHostAddress();
			mEthNetmask = interMask2String(ipAddress.getPrefixLength());
		}
		if (gateway != null) {
			mEthGateway = gateway.getHostAddress();
		}
		mEthdns1 = dnsServers.get(0).getHostAddress();

		if (dnsServers.size() > 1) { /* 只保留两个*/
			mEthdns2 = dnsServers.get(1).getHostAddress();
		}
	}

	// 将子网掩码转换成ip子网掩码形式，比如输入32输出为255.255.255.255
	public String interMask2String(int prefixLength) {
		String netMask = null;
		int inetMask = prefixLength;

		int part = inetMask / 8;
		int remainder = inetMask % 8;
		int sum = 0;

		for (int i = 8; i > 8 - remainder; i--) {
			sum = sum + (int) Math.pow(2, i - 1);
		}

		if (part == 0) {
			netMask = sum + ".0.0.0";
		} else if (part == 1) {
			netMask = "255." + sum + ".0.0";
		} else if (part == 2) {
			netMask = "255.255." + sum + ".0";
		} else if (part == 3) {
			netMask = "255.255.255." + sum;
		} else if (part == 4) {
			netMask = "255.255.255.255";
		}

		return netMask;
	}

	private final static String nullIpInfo = "0.0.0.0";
	private final String STATE_CONNECTING = "正在获取...";
	private static String mEthHwAddress = null;
	private static String mEthIpAddress = null;
	private static String mEthNetmask = null;
	private static String mEthGateway = null;
	private static String mEthdns1 = null;
	private static String mEthdns2 = null;


	public void getEthInfoFromDhcp() {
		String tempIpInfo;
		String iface = "eth0";

		tempIpInfo = SystemProperties.get("dhcp." + iface + ".ipaddress");

		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			mEthIpAddress = tempIpInfo;
		} else {
			mEthIpAddress = nullIpInfo;
		}

		tempIpInfo = SystemProperties.get("dhcp." + iface + ".mask");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			mEthNetmask = tempIpInfo;
		} else {
			mEthNetmask = nullIpInfo;
		}

		tempIpInfo = SystemProperties.get("dhcp." + iface + ".gateway");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			mEthGateway = tempIpInfo;
		} else {
			mEthGateway = nullIpInfo;
		}

		tempIpInfo = SystemProperties.get("dhcp." + iface + ".dns1");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			mEthdns1 = tempIpInfo;
		} else {
			mEthdns1 = nullIpInfo;
		}

		tempIpInfo = SystemProperties.get("dhcp." + iface + ".dns2");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			mEthdns2 = tempIpInfo;
		} else {
			mEthdns2 = nullIpInfo;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getContext().unregisterReceiver(mReceiver);
		ButterKnife.unbind(this);
	}

	public void setIPConfigDHCP() {
		try {
			mEthernetManager.setConfiguration(new IpConfiguration(IpConfiguration.IpAssignment.DHCP, IpConfiguration.ProxySettings.NONE, null, null));
		} catch (Exception e) {
			Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
}
