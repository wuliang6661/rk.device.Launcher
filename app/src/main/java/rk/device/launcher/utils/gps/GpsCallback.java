package rk.device.launcher.utils.gps;

import android.location.Address;

import java.util.List;

/**
 * 地理位置回调
 *
 * Created by hanbin on 2017/11/29.
 */
public interface GpsCallback {
    void onResult(List<Address> address);
}
