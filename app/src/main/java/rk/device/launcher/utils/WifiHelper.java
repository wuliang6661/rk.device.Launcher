package rk.device.launcher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mundane on 2017/11/17 下午1:36
 */

public class WifiHelper {
	// 上下文Context对象
	private Context mApplicationContext;
	// WifiManager对象
	private WifiManager mWifiManager;

	public WifiHelper(Context context) {
		mApplicationContext = context.getApplicationContext();
		mWifiManager = (WifiManager) mApplicationContext.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * 判断手机是否连接在Wifi上
	 */
	public boolean isConnectWifi() {
		// 获取ConnectivityManager对象
		ConnectivityManager conMgr = (ConnectivityManager) mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取NetworkInfo对象
		NetworkInfo info = conMgr.getActiveNetworkInfo();
		// 获取连接的方式为wifi
		NetworkInfo.State wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

		if (info != null && info.isAvailable() && wifi == NetworkInfo.State.CONNECTED) {
			return true;
		} else {
			return false;
		}

	}
	
	public boolean setWifiEnabled(boolean enable) {
		boolean result = mWifiManager.setWifiEnabled(enable);
		return result;
	}
	
	public boolean isWifiEndabled() {
		boolean isOpen = true;
		int wifiState = mWifiManager.getWifiState();
		
		if (wifiState == WifiManager.WIFI_STATE_DISABLED
		|| wifiState == WifiManager.WIFI_STATE_DISABLING
		|| wifiState == WifiManager.WIFI_STATE_UNKNOWN
		|| wifiState == WifiManager.WIFI_STATE_ENABLING) {
			isOpen = false;
		}
		
		return isOpen;
	}

	/**
	 * 获取当前手机所连接的wifi信息
	 */
	public WifiInfo getCurrentWifiInfo() {
		return mWifiManager.getConnectionInfo();
	}

	/**
	 * 添加一个网络并连接
	 * 传入参数：WIFI发生配置类WifiConfiguration
	 */
	public boolean addNetwork(WifiConfiguration config) {
		// 连接wifi的重点代码在这里
		int networkId = mWifiManager.addNetwork(config);
		return mWifiManager.enableNetwork(networkId, true);
	}

	/**
	 * 搜索附近的热点信息，并返回所有热点为信息的SSID集合数据
	 */
	public List<String> getScanSSIDsResult() {
		// 扫描的热点数据
		List<ScanResult> resultList;
		// 开始扫描热点
		mWifiManager.startScan();
		resultList = mWifiManager.getScanResults();
		ArrayList<String> ssids = new ArrayList<String>();
		if (resultList != null) {
			for (ScanResult scan : resultList) {
				ssids.add(scan.SSID);// 遍历数据，取得ssid数据集
			}
		}
		return ssids;
	}

	/**
	 * 判断该wifi是否加密
	 *
	 * @param scanResult
	 */
	public boolean isLocked(ScanResult scanResult) {
		// 该wifi是否加密
		boolean isLocked = true;
		if (scanResult.capabilities.contains("WPA2-PSK")) {
			// WPA-PSK加密
		} else if (scanResult.capabilities.contains("WPA-PSK")) {
			// WPA-PSK加密
		} else if (scanResult.capabilities.contains("WPA-EAP")) {
			// WPA-EAP加密
		} else if (scanResult.capabilities.contains("WEP")) {
			// WEP加密
		} else {
			// 无密码
			isLocked = false;
		}
		return isLocked;
	}

	/**
	 * 判断以前是否配置过这个网络
	 *
	 * @param scanResult
	 * @return
	 */
	public WifiConfiguration isExist(ScanResult scanResult) {
		// 获取所有已经配置过的网络
		List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		if (configuredNetworks != null) {
			for (WifiConfiguration existedWifiConfig : configuredNetworks) {
				if (TextUtils.equals(existedWifiConfig.SSID, "\"" + scanResult.SSID + "\"")) {
					return existedWifiConfig;
				}
			}
		}
		return null;
	}

	public boolean isWifiConnected() {
		List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		return configuredNetworks == null ? false : true;
	}

	/**
	 * 这种方法也可以
	 *
	 * @param scanResult
	 */
	public void removeNetWork(ScanResult scanResult) {
		WifiConfiguration existWifiConfig = isExist(scanResult);
		if (existWifiConfig != null) {
			mWifiManager.removeNetwork(existWifiConfig.networkId);
			mWifiManager.saveConfiguration();
		}
	}

	/**
	 * 忘记密码
	 * @param wifiConfiguration
	 */
	public void removeNetWork(WifiConfiguration wifiConfiguration) {
		// 由于系统限制, 忘记密码失败
		mWifiManager.removeNetwork(wifiConfiguration.networkId);
		mWifiManager.saveConfiguration();
	}

	public void disconnect() {
		mWifiManager.disconnect();
	}

	/**
	 * 忘记wifi密码
	 * @param scanResult
	 */
	public void forgetNetWork(ScanResult scanResult) {
		try {
			WifiConfiguration wifiConfiguration = isExist(scanResult);
			Class<? extends WifiManager> clazz = mWifiManager.getClass();
			Class<?> forgetListenerClazz = Class.forName("android.net.wifi.WifiManager$ActionListener");
			Method methodForget = clazz.getDeclaredMethod("forget", int.class, forgetListenerClazz);
			methodForget.setAccessible(true);
			methodForget.invoke(mWifiManager, wifiConfiguration.networkId, null);
//			mWifiManager.forget(wifiConfiguration.networkId, mForgetListener);
			LogUtil.d("反射方法调用成功");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public boolean ConnectToNetId(int netId) {
		return mWifiManager.enableNetwork(netId, true);
	}

	public boolean ConnectToNetId(WifiConfiguration configuration) {
		return mWifiManager.enableNetwork(configuration.networkId, true);
	}



	/**
	 * 判断是否已经连接到这个wifi上面
	 *
	 * @param scanResult
	 * @return
	 */
	public boolean isConnected(ScanResult scanResult) {
		boolean isConnected = false;
		WifiConfiguration existedWifiConfiguration = isExist(scanResult);
		if (existedWifiConfiguration != null && existedWifiConfiguration.networkId == mWifiManager.getConnectionInfo().getNetworkId()) {
			isConnected = true;
		}
		return isConnected;
	}

	public ScanResult getConnectedScanResult() {
		mWifiManager.startScan();
		List<ScanResult> list = mWifiManager.getScanResults();
		if (list != null) {
			for (ScanResult scanResult : list) {
				if (isConnected(scanResult)) {
					return scanResult;
				}
			}
		}
		return null;
	}

	/**
	 * 检测wifi状态 opened return true;
	 */
	public boolean checkWifiState() {
		boolean isOpen = true;
		int wifiState = mWifiManager.getWifiState();

		if (wifiState == WifiManager.WIFI_STATE_DISABLED
				|| wifiState == WifiManager.WIFI_STATE_DISABLING
				|| wifiState == WifiManager.WIFI_STATE_UNKNOWN
				|| wifiState == WifiManager.WIFI_STATE_ENABLING) {
			isOpen = false;
		}

		return isOpen;
	}


	public List<ScanResult> getFilteredScanResult() {
		try {
			mWifiManager.startScan();
			List<ScanResult> list = mWifiManager.getScanResults();
			HashMap<String, ScanResult> scanResultMap = new HashMap<>();
			for (ScanResult scanResult : list) {
				if (!TextUtils.isEmpty(scanResult.SSID)) { // 如果wifi名字不为空
					String key = scanResult.SSID + " " + scanResult.capabilities;
					if (!scanResultMap.containsKey(key)) { // 没有跟它重复的, 直接添加进去
						scanResultMap.put(key, scanResult);
					} else { // 出现重复的SSID了
						ScanResult oldScanResult = scanResultMap.get(key);
						// 将当前遍历到的scanResult的信号强度和之前已经存在的scanResult的信号强度进行比较, 如果信号更强, 就替换掉以前的
						// 信号越强, 绝对值越小
						if (Math.abs(scanResult.level) < Math.abs(oldScanResult.level)) {
							scanResultMap.put(key, scanResult);
						}
					}
				}
			}
			
			List<ScanResult> filteredList = new ArrayList<>();
			for (String key : scanResultMap.keySet()) {
				filteredList.add(scanResultMap.get(key));
			}
			// 按信号强度从强到弱排列
			Collections.sort(filteredList, new Comparator<ScanResult>() {
				@Override
				public int compare(ScanResult o1, ScanResult o2) {
					return Math.abs(o1.level) - Math.abs(o2.level);
				}
			});
			return filteredList;
		} catch (Exception e) {
			LogUtil.e(e.getMessage());
		}
		return null;
	}
	
	private final String TAG = "WifiHelper";

	/**
	 * 得到手机搜索到的ssid集合，从中判断出设备的ssid（dssid）
	 */
	public List<String> accordSsid() {
		List<String> s = getScanSSIDsResult();
		List<String> result = new ArrayList<>();
		for (String str : s) {
			if (checkDssid(str, "dd")) {
				result.add(str);
			}
		}
		return result;
	}

	/**
	 * 检测指定ssid是不是匹配的ssid，目前支持GBELL，TOP,后续可添加。
	 *
	 * @param ssid
	 * @return
	 */
	private boolean checkDssid(String ssid, String condition) {
		if (!TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(condition)) {
			//这里条件根据自己的需求来判断，我这里就是随便写的一个条件
			if (ssid.length() > 8 && (ssid.substring(0, 8).equals(condition))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 连接wifi
	 * 参数：wifi的ssid及wifi的密码
	 */
	public boolean connectWifiTest(final String ssid, final String pwd) {
		boolean isSuccess = false;
		boolean flag = false;
		mWifiManager.disconnect();
		boolean addSucess = addNetwork(CreateWifiInfo(ssid, pwd, 3));
		if (addSucess) {
			while (!flag && !isSuccess) {
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
				String currSSID = getCurrentWifiInfo().getSSID();
				if (currSSID != null)
					currSSID = currSSID.replace("\"", "");
				int currIp = getCurrentWifiInfo().getIpAddress();
				if (currSSID != null && currSSID.equals(ssid) && currIp != 0) {
					//这里还需要做优化处理，增强结果判断
					isSuccess = true;
				} else {
					flag = true;
				}
			}
		}
		return isSuccess;

	}
	
	public int CreateWifiInfo2(ScanResult wifiinfo, String pwd) {
		int type = -1;
		if (wifiinfo.capabilities.contains("WPA2-PSK")) {
			// WPA-PSK加密
			type = TYPE_WPA2_PSK;
		} else if (wifiinfo.capabilities.contains("WPA-PSK")) {
			// WPA-PSK加密
			type = TYPE_WPA_PSK;
		} else if (wifiinfo.capabilities.contains("WPA-EAP")) {
			// WPA-EAP加密
			type = TYPE_WPA_EAP;
		} else if (wifiinfo.capabilities.contains("WEP")) {
			// WEP加密
			type = TYPE_WPA_EAP;
		} else {
			// 无密码
			type = TYPE_NO_PASSWORD;
		}
		
		WifiConfiguration config = CreateWifiInfo(wifiinfo.SSID, wifiinfo.BSSID, pwd, type);
		if (config != null) {
			return mWifiManager.addNetwork(config);
		} else {
			return -1;
		}
	}
	
	public boolean connectToWifiWithPwd(ScanResult scanResult, String password) {
		int type = -1;
		if (scanResult.capabilities.contains("WPA2-PSK")) {
			// WPA-PSK加密
			type = TYPE_WPA2_PSK;
		} else if (scanResult.capabilities.contains("WPA-PSK")) {
			// WPA-PSK加密
			type = TYPE_WPA_PSK;
		} else if (scanResult.capabilities.contains("WPA-EAP")) {
			// WPA-EAP加密
			type = TYPE_WPA_EAP;
		} else if (scanResult.capabilities.contains("WEP")) {
			// WEP加密
			type = TYPE_WPA_EAP;
		} else {
			// 无密码
			type = TYPE_NO_PASSWORD;
		}
		
		WifiConfiguration config = CreateWifiInfo(scanResult.SSID, scanResult.BSSID, password, type);
		int netId = mWifiManager.addNetwork(config);
		return mWifiManager.enableNetwork(netId, true);
	}

	private int getMaxPriority() {
		List<WifiConfiguration> localList = mWifiManager.getConfiguredNetworks();
		int i = 0;
		Iterator<WifiConfiguration> localIterator = localList.iterator();
		while (true) {
			if (!localIterator.hasNext())
				return i;
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localIterator
					.next();
			if (localWifiConfiguration.priority <= i)
				continue;
			i = localWifiConfiguration.priority;
		}
	}

	private int shiftPriorityAndSave() {
		List<WifiConfiguration> localList = mWifiManager.getConfiguredNetworks();
		sortByPriority(localList);
		int i = localList.size();
		for (int j = 0; ; ++j) {
			if (j >= i) {
				mWifiManager.saveConfiguration();
				return i;
			}
			WifiConfiguration localWifiConfiguration = localList.get(j);
			localWifiConfiguration.priority = j;
			mWifiManager.updateNetwork(localWifiConfiguration);
		}
	}

	private void sortByPriority(List<WifiConfiguration> paramList) {
		Collections.sort(paramList, new SjrsWifiManagerCompare());
	}

	class SjrsWifiManagerCompare implements Comparator<WifiConfiguration> {
		public int compare(WifiConfiguration paramWifiConfiguration1, WifiConfiguration paramWifiConfiguration2) {
			return paramWifiConfiguration1.priority - paramWifiConfiguration2.priority;
		}
	}

	public WifiConfiguration setMaxPriority(WifiConfiguration config) {
		int priority = getMaxPriority() + 1;
		if (priority > 99999) {
			priority = shiftPriorityAndSave();
		}

		config.priority = priority; // 2147483647;
		System.out.println("priority=" + priority);

		mWifiManager.updateNetwork(config);

		// 本机之前配置过此wifi热点，直接返回
		return config;
	}

	private final int TYPE_WPA2_PSK = 1;
	private final int TYPE_WPA_PSK = 2;
	private final int TYPE_WPA_EAP = 3;
	private final int TYPE_WEP = 4;
	private final int TYPE_NO_PASSWORD = 5;

	/**
	 * 配置一个连接
	 */
	public WifiConfiguration CreateWifiInfo(String SSID, String BSSID, String password, int type) {

		int priority;

		WifiConfiguration config = this.IsExsits(SSID);
		if (config != null) {
			// Log.w("Wmt", "####之前配置过这个网络，删掉它");
			// wifiManager.removeNetwork(config.networkId); // 如果之前配置过这个网络，删掉它
			// 本机之前配置过此wifi热点，调整优先级后，直接返回
			return setMaxPriority(config);
		}

		config = new WifiConfiguration();
		/* 清除之前的连接信息 */
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		config.status = WifiConfiguration.Status.ENABLED;
		// config.BSSID = BSSID;
		// config.hiddenSSID = true;

		priority = getMaxPriority() + 1;
		if (priority > 99999) {
			priority = shiftPriorityAndSave();
		}
		config.priority = priority; // 2147483647;
		/* 各种加密方式判断 */
		if (type == TYPE_NO_PASSWORD) {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == TYPE_WEP) {
			config.preSharedKey = "\"" + password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == TYPE_WPA_EAP) {
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);
		} else if (type == TYPE_WPA_PSK) {
			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);
		} else if (type == TYPE_WPA2_PSK) {
			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		} else {
			return null;
		}

		return config;
	}

	/**
	 * 创建WifiConfiguration对象
	 * 分为三种情况：1没有密码;2用wep加密;3用wpa加密
	 *
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return
	 */
	public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) { // WIFICIPHER_NOPASS
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) { // WIFICIPHER_WEP
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) { // WIFICIPHER_WPA
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
}
