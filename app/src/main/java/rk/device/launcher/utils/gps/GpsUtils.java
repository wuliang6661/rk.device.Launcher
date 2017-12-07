package rk.device.launcher.utils.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rk.device.launcher.utils.CommonUtils;
import rk.device.launcher.utils.ThreadUtils;

/**
 * 地理位置帮助类
 * Created by hanbin on 2017/11/29.
 */
public class GpsUtils {

    private final String TAG = "GpsUtils";

    private LocationManager mLocationManager = null;
    private Context mContext;

    public GpsUtils(Context context) {
        this.mContext = context;
        if (mLocationManager == null) {
            synchronized (GpsUtils.class) {
                if (mLocationManager == null) {
                    mLocationManager = (LocationManager) context
                            .getSystemService(Context.LOCATION_SERVICE);
                }
            }
        }
    }

    public void initLocation(GpsCallback callback) {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // 既没有打开gps也没有打开网络
        if (!isGpsOpened() && !isNewWorkOpen()) {
            Toast.makeText(mContext, "请打开网络或GPS定位功能!", Toast.LENGTH_SHORT).show();
            //			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //			startActivityForResult(intent, 0);
            return;
        }
        ThreadUtils.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Location location = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        Log.d(TAG, "gps.location = null");
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    Log.d(TAG, "network.location = " + location);
                    Geocoder geocoder = new Geocoder(CommonUtils.getContext(), Locale.getDefault());
                    if (location == null) {
                        return;
                    }
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        callback.onResult(addresses);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isGpsOpened() {
        boolean isOpen = true;
        // 没有开启GPS
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isOpen = false;
        }
        return isOpen;
    }

    private boolean isNewWorkOpen() {
        boolean isOpen = true;
        // 没有开启网络定位
        if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            isOpen = false;
        }
        return isOpen;
    }
}
