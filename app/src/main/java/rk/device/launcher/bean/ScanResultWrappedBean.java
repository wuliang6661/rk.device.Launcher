package rk.device.launcher.bean;

import android.net.wifi.ScanResult;

/**
 * Created by mundane on 2017/11/17 上午11:40
 */

public class ScanResultWrappedBean {
	public ScanResult scanResult;
	public boolean isConnected;

	public ScanResultWrappedBean() {
	}

	public ScanResultWrappedBean(ScanResult scanResult, boolean isConnected) {
		this.scanResult = scanResult;
		this.isConnected = isConnected;
	}

	public ScanResultWrappedBean(ScanResult scanResult) {
		this.scanResult = scanResult;
	}

	public ScanResultWrappedBean(boolean isConnected) {
		this.isConnected = isConnected;
	}
}
