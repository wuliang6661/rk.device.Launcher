package rk.device.launcher.ui.fragment;


import android.hardware.input.InputManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.lang.reflect.Constructor;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.utils.LogUtil;

/**
 * Createed by mundane
 */
public class ManualConfigFragment extends Fragment {
	
	
	@Bind(R.id.et_ip)
	EditText mEtIp;
	@Bind(R.id.et_net_mask)
	EditText mEtNetMask;
	@Bind(R.id.et_net_gate)
	EditText mEtNetGate;
	@Bind(R.id.et_dns)
	EditText mEtDns;
	private static String mEthHwAddress = null;
	private static String mEthIpAddress = null;
	private static String mEthNetmask = null;
	private static String mEthGateway = null;
	private static String mEthdns1 = null;
	private static String mEthdns2 = null;
	private final static String nullIpInfo = "0.0.0.0";
	private StaticIpConfiguration mStaticIpConfiguration;
	private IpConfiguration mIpConfiguration;
	private EthernetManager mEthManager;
	
	public ManualConfigFragment() {
		// Required empty public constructor
	}
	
	public static ManualConfigFragment newInstance() {
		ManualConfigFragment fragment = new ManualConfigFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEthManager = (EthernetManager) getContext().getSystemService("ethernet");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_manual_config, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

    @Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		limitInput(mEtIp);
		limitInput(mEtNetMask);
		limitInput(mEtNetGate);
		limitInput(mEtDns);
	}
	
	private void limitInput(EditText editText) {
		editText.setInputType(3);
		editText.addTextChangedListener(new TextWatcher() {
            private String mBeforeText;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mBeforeText = s.toString();
            }
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				LogUtil.d(TAG, text);
				if (TextUtils.isEmpty(text)) {
					return;
				}
                int beforeLength = mBeforeText.length();
                int currentLength = text.length();
                if (currentLength < beforeLength) {
                    return;
                }

                // 连续出现了4个数字
				// .*表示任何字符一次或多次
				String regex1 = "^.*\\d{4,}.*$";
				// 表示整个输入中出现了4个.
				// \\.表示一个. (因为.在正则中有特殊含义, 所以要加一个\, \转义为\\)
				// .*表示.之间出入了任意个任意字符
				String regex2 = "^.*\\..*\\..*\\..*\\..*$";
				// 表示除了数字和.以外的字符出现了一次或多次
				String regex3 = "^.*[^\\d^\\.]+.*$";
				if (text.contains("..") || text.startsWith(".") || isMatchRegex(regex1, text)
				|| isMatchRegex(regex2, text) || isMatchRegex(regex3, text)) {
					String newString = text.substring(0, text.length() - 1);
					editText.setText(newString);
					editText.setSelection(newString.length());
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private final String TAG = "ManualConfigFragment";
	
	private boolean isMatchRegex(String regex, String text) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher.matches();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
	
	public boolean saveIpConfig() {
		mEthIpAddress = mEtIp.getText().toString();
		mEthNetmask = mEtNetMask.getText().toString();
		mEthGateway = mEtNetGate.getText().toString();
		mEthdns1 = mEtDns.getText().toString();
		if (setStaticIpConfiguration()) {
			try {
				mEthManager.setConfiguration(mIpConfiguration);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				T.showShort(e.getMessage());
			}
		}
		return false;
		
	}
	
	private boolean setStaticIpConfiguration() {
		
		mStaticIpConfiguration = new StaticIpConfiguration();
		 /*
		  * get ip address, netmask,dns ,gw etc.
		  */
		Inet4Address inetAddr = getIPv4Address(this.mEthIpAddress);
		int prefixLength = maskStr2InetMask(this.mEthNetmask);
		InetAddress gatewayAddr = getIPv4Address(this.mEthGateway);
		InetAddress dnsAddr = getIPv4Address(this.mEthdns1);
		if (inetAddr == null || gatewayAddr == null || dnsAddr == null) {
//			T.showShort("网络设置出错, 请重新设置");
			return false;
		}
		if (inetAddr.getAddress().toString().isEmpty() || prefixLength == 0 || gatewayAddr.toString().isEmpty()
		|| dnsAddr.toString().isEmpty()) {
			LogUtil.d("ip,mask or dnsAddr is wrong");
			return false;
		}
		
		String dnsStr2 = this.mEthdns2;
//		mStaticIpConfiguration.ipAddress = new LinkAddress(inetAddr, prefixLength);
		Class<?> clazz = null;
		try {
			clazz = Class.forName("android.net.LinkAddress");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Class[] cl = new Class[]{InetAddress.class, int.class};
		Constructor cons = null;
		
		//取得所有构造函数
		try {
			cons = clazz.getConstructor(cl);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		//给传入参数赋初值
		Object[] x = {inetAddr, prefixLength};
		try {
			mStaticIpConfiguration.ipAddress = (LinkAddress) cons.newInstance(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mStaticIpConfiguration.gateway = gatewayAddr;
		mStaticIpConfiguration.dnsServers.add(dnsAddr);
		
		if (!TextUtils.isEmpty(dnsStr2)) {
			mStaticIpConfiguration.dnsServers.add(getIPv4Address(dnsStr2));
		}
		mIpConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, mStaticIpConfiguration, null);
		return true;
	}
	
	private Inet4Address getIPv4Address(String text) {
		try {
			return (Inet4Address) NetworkUtils.numericToInetAddress(text);
		} catch (IllegalArgumentException | ClassCastException e) {
			return null;
		}
	}
	
	/*
	 * convert subMask string to prefix length
     */
	private int maskStr2InetMask(String maskStr) {
		StringBuffer sb;
		String str;
		int inetmask = 0;
		int count = 0;
    	/*
    	 * check the subMask format
    	 */
		Pattern pattern = Pattern.compile("(^((\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$)|^(\\d|[1-2]\\d|3[0-2])$");
		if (pattern.matcher(maskStr).matches() == false) {
			return 0;
		}
		
		String[] ipSegment = maskStr.split("\\.");
		for (int n = 0; n < ipSegment.length; n++) {
			sb = new StringBuffer(Integer.toBinaryString(Integer.parseInt(ipSegment[n])));
			str = sb.reverse().toString();
			count = 0;
			for (int i = 0; i < str.length(); i++) {
				i = str.indexOf("1", i);
				if (i == -1)
					break;
				count++;
			}
			inetmask += count;
		}
		return inetmask;
	}
}
