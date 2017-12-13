package rk.device.launcher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class NetUtils {
	// 判断网络是否连接
	public static boolean isHttpConnected(Context context) {
		try {
			NetworkInfo info = null;
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			State mobile = null;
			if (info != null) {
				mobile = info.getState();
			}

			State wifi = null;
			info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info != null) {
				wifi = info.getState();   //如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
			}

			if (mobile == State.CONNECTED || wifi == State.CONNECTED) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}


	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}


	public static boolean isWifiConnected(Context context) {
		try {
			NetworkInfo info = null;
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			State wifi = null;
			info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info != null) {
				wifi = info.getState();
			}

			if (wifi == State.CONNECTED) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean is3GConnected(Context context) {
		try {
			NetworkInfo info = null;
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			State mobile = null;
			if (info != null) {
				mobile = info.getState();
			}

			if (mobile == State.CONNECTED) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean ping(String ipAndAddress) {
		boolean success = false;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("ping -c 1 -W 1" + ipAndAddress);
			int status = p.waitFor();
			if (status == 0) {
				success = true;
			} else {
				success = false;
			}
		} catch (IOException | InterruptedException e) {
			success = false;
		} finally {
			assert p != null;
			p.destroy();
		}
		return success;
	}

	/**
	 * Creator by zhenwang.xiang on 2017/9/12 14:22
	 * Description: 判断设备 是否使用代理上网
	 */
	public static boolean isWifiProxy(Context context) {
		// 是否大于等于4.0
		final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
		String proxyAddress;
		int proxyPort;
		if (IS_ICS_OR_LATER) {
			proxyAddress = System.getProperty("http.proxyHost");
			String portStr = System.getProperty("http.proxyPort");
			proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
		} else {
			proxyAddress = android.net.Proxy.getHost(context);
			proxyPort = android.net.Proxy.getPort(context);
		}
		return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
	}

	/**
	 * 获取IP
	 *
	 * @param context
	 * @return
	 */
	public static String getIP(Context context) {
		String ip = "0.0.0.0";
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null) {
			LogUtil.d(TAG, "没有可用的网络连接");
			return ip;
		}
		int type = info.getType();
		if (type == ConnectivityManager.TYPE_ETHERNET) {
			ip = getEtherNetIP();
		} else if (type == ConnectivityManager.TYPE_WIFI) {
			ip = getWifiIP(context);
		}
		return ip;
	}

	/**
	 * 获取有线地址
	 *
	 * @return
	 */
	public static String getEtherNetIP() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "0.0.0.0";
	}

	/**
	 * 获取wifiIP地址
	 *
	 * @param context
	 * @return
	 */
	public static String getWifiIP(Context context) {
		WifiManager wifiManager = getWifiManager(context);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int intaddr = wifiInfo.getIpAddress();
		byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff),
				(byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff),
				(byte) (intaddr >> 24 & 0xff)};
		InetAddress addr = null;
		try {
			addr = InetAddress.getByAddress(byteaddr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String mobileIp = addr.getHostAddress();
		return mobileIp;
	}

	public static InetAddress intToInetAddress(int hostAddress) {
		byte[] addressBytes = { (byte)(0xff & hostAddress),
				(byte)(0xff & (hostAddress >> 8)),
				(byte)(0xff & (hostAddress >> 16)),
				(byte)(0xff & (hostAddress >> 24)) };

		try {
			return InetAddress.getByAddress(addressBytes);
		} catch (UnknownHostException e) {
			throw new AssertionError();
		}
	}

	public static WifiManager getWifiManager(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager;
	}

	public static void getEthernetManager(Context context) {
	}

	/**
	 * 获取wifi时的dhcp
	 * @param context
	 * @return
	 */
	public static DhcpInfo getDhcpInfo(Context context) {
		WifiManager wifiManager = getWifiManager(context);
		return wifiManager.getDhcpInfo();
	}


	private static String intToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
				+ (0xFF & paramInt >> 24);
	}

	/**
	 * 获取wifi时的子网掩码
	 * @param context
	 * @return
	 */
	public static String getNetMask(Context context) {
		DhcpInfo dhcpInfo = getDhcpInfo(context);
		int netmask = dhcpInfo.netmask;
		LogUtil.d(TAG, "originalNetmask = " + netmask);
		return intToIp(netmask);
	}
	
	private static final String TAG = "NetUtils";

	/**
	 * 获取wifi时的ip
	 * @param context
	 * @return
	 */
	public static String getIp2(Context context) {
		DhcpInfo dhcpInfo = getDhcpInfo(context);
		return intToIp(dhcpInfo.ipAddress);
	}

	/**
	 * 获取wifi时的网关地址
	 * @param context
	 * @return
	 */
	public static String getGateWay(Context context) {
		DhcpInfo dhcpInfo = getDhcpInfo(context);
		return intToIp(dhcpInfo.gateway);
	}

	/**
	 * 获取wifi时的dns1
	 * @param context
	 * @return
	 */
	public static String getDns1(Context context) {
		DhcpInfo dhcpInfo = getDhcpInfo(context);
		return intToIp(dhcpInfo.dns1);
	}

	/**
	 * 从setting里面拷贝过来的获取wifi时IP的方法
	 * Returns the WIFI IP Addresses, if any, taking into account IPv4 and IPv6 style addresses.
	 * @param context the application context
	 * @return the formatted and newline-separated IP addresses, or null if none.
	 */
	public static String getWifiIpAddresses(Context context) {
//		ConnectivityManager cm = (ConnectivityManager)
//				context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		LinkProperties prop = cm.getLinkProperties(ConnectivityManager.TYPE_WIFI);
//		return formatIpAddresses(prop);
		return null;
	}

	private static String formatIpAddresses(LinkProperties prop) {
//		if (prop == null) return null;
//		Iterator<InetAddress> iter = prop.getAllAddresses().iterator();
//		// If there are no entries, return null
//		if (!iter.hasNext()) return null;
//		// Concatenate all available addresses, comma separated
//		String addresses = "";
//		while (iter.hasNext()) {
//			addresses += iter.next().getHostAddress();
//			if (iter.hasNext()) addresses += "\n";
//		}
//		return addresses;
		return null;
	}
	
	public static String getNetMask() {
		try {
			Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
			while (eni.hasMoreElements()) {
				
				NetworkInterface networkCard = eni.nextElement();
				List<InterfaceAddress> ncAddrList = networkCard.getInterfaceAddresses();
				Iterator<InterfaceAddress> ncAddrIterator = ncAddrList.iterator();
				while (ncAddrIterator.hasNext()) {
					InterfaceAddress networkCardAddress = ncAddrIterator.next();
					InetAddress address = networkCardAddress.getAddress();
					if (!address.isLoopbackAddress()) {
						String hostAddress = address.getHostAddress();
						System.out.println("address        =   " + hostAddress);
						
						if (hostAddress.indexOf(":") > 0) {
							// case : ipv6
							continue;
						} else {
							// case : ipv4
							String maskAddress = calcMaskByPrefixLength(networkCardAddress.getNetworkPrefixLength());
							String subnetAddress = calcSubnetAddress(hostAddress, maskAddress);
							String broadcastAddress = networkCardAddress.getBroadcast().getHostAddress();
							
//							System.out.println("subnetmask     =   "+ maskAddress);
//							System.out.println("subnet         =   "+ subnetAddress);
//							System.out.println("broadcast      =   "+ broadcastAddress+"\n");
							return maskAddress;
						}
					} else {
						String loopback = networkCardAddress.getAddress().getHostAddress();
						System.out.println("loopback addr  =   " + loopback +"\n");
					}
				}
				System.out.println("----- NetworkInterface  Separator ----\n\n");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0.0.0.0";
	}
	
	public static void printIpAddressAndSubnettest() {
		try {
			Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
			while (eni.hasMoreElements()) {
				
				NetworkInterface networkCard = eni.nextElement();
				List<InterfaceAddress> ncAddrList = networkCard.getInterfaceAddresses();
				Iterator<InterfaceAddress> ncAddrIterator = ncAddrList.iterator();
				while (ncAddrIterator.hasNext()) {
					InterfaceAddress networkCardAddress = ncAddrIterator.next();
					InetAddress address = networkCardAddress.getAddress();
					if (!address.isLoopbackAddress()) {
						String hostAddress = address.getHostAddress();
						System.out.println("address        =   " + hostAddress);
						
						if (hostAddress.indexOf(":") > 0) {
							// case : ipv6
							continue;
						} else {
							// case : ipv4
							String maskAddress = calcMaskByPrefixLength(networkCardAddress.getNetworkPrefixLength());
							String subnetAddress = calcSubnetAddress(hostAddress, maskAddress);
							String broadcastAddress = networkCardAddress.getBroadcast().getHostAddress();
							
							System.out.println("subnetmask     =   "+ maskAddress);
							System.out.println("subnet         =   "+ subnetAddress);
							System.out.println("broadcast      =   "+ broadcastAddress+"\n");
						}
					} else {
						String loopback = networkCardAddress.getAddress().getHostAddress();
						System.out.println("loopback addr  =   " + loopback +"\n");
					}
				}
				System.out.println("----- NetworkInterface  Separator ----\n\n");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String calcMaskByPrefixLength(int length) {
		int mask = -1 << (32 - length);
		int partsNum = 4;
		int bitsOfPart = 8;
		int maskParts[] = new int[partsNum];
		int selector = 0x000000ff;
		
		for (int i = 0; i < maskParts.length; i++) {
			int pos = maskParts.length - 1 - i;
			maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
		}
		
		String result = "";
		result = result + maskParts[0];
		for (int i = 1; i < maskParts.length; i++) {
			result = result + "." + maskParts[i];
		}
		return result;
	}
	
	public static String calcSubnetAddress(String ip, String mask) {
		String result = "";
		try {
			// calc sub-net IP
			InetAddress ipAddress = InetAddress.getByName(ip);
			InetAddress maskAddress = InetAddress.getByName(mask);
			
			byte[] ipRaw = ipAddress.getAddress();
			byte[] maskRaw = maskAddress.getAddress();
			
			int unsignedByteFilter = 0x000000ff;
			int[] resultRaw = new int[ipRaw.length];
			for (int i = 0; i < resultRaw.length; i++) {
				resultRaw[i] = (ipRaw[i] & maskRaw[i] & unsignedByteFilter);
			}
			
			// make result string
			result = result + resultRaw[0];
			for (int i = 1; i < resultRaw.length; i++) {
				result = result + "." + resultRaw[i];
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
}
