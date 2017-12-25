package rk.device.launcher.bean;

import android.net.wifi.ScanResult;

/**
 * Created by mundane on 2017/11/17 上午11:40
 */

public class ScanResultWrappedBO {
	public ScanResult scanResult;
	public boolean isConnected;

	public ScanResultWrappedBO() {
	}

	public ScanResultWrappedBO(ScanResult scanResult, boolean isConnected) {
		this.scanResult = scanResult;
		this.isConnected = isConnected;
	}

	public ScanResultWrappedBO(ScanResult scanResult) {
		this.scanResult = scanResult;
	}

	public ScanResultWrappedBO(boolean isConnected) {
		this.isConnected = isConnected;
	}
}
