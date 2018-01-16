package rk.device.launcher.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/1
 *     desc  : 设备相关工具类
 * </pre>
 */
public class DeviceUtils {

    private DeviceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断设备是否root
     *
     * @return the boolean{@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/", "/system/bin/failsafe/",
                "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取设备系统版本号
     *
     * @return 设备系统版本号
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * 获取设备AndroidID
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        return Settings.Secure.getString(Utils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取有线连网的mac地址
     */
    public static String getMacAddressLine() {
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }

                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("wuliang", "有线连接的mac ： " + strMacAddr);
        return strMacAddr;
    }


    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = (InetAddress) en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }


    /**
     * 在不联网的情况下获取mac
     */
    public static String getLocalMacAddress() {
        String mac = null;
        try {
            String path = "sys/class/net/eth0/address";
            FileInputStream fis_name = new FileInputStream(path);
            byte[] buffer_name = new byte[8192];
            int byteCount_name = fis_name.read(buffer_name);
            if (byteCount_name > 0) {
                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
            }
            if (mac == null) {
                fis_name.close();
                return "";
            }
            fis_name.close();
        } catch (Exception io) {
            String path = "sys/class/net/wlan0/address";
            FileInputStream fis_name;
            try {
                fis_name = new FileInputStream(path);
                byte[] buffer_name = new byte[8192];
                int byteCount_name = fis_name.read(buffer_name);
                if (byteCount_name > 0) {
                    mac = new String(buffer_name, 0, byteCount_name, "utf-8");
                }
                fis_name.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (mac == null) {
            return "";
        } else {
            return mac.trim();
        }
    }


    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return MAC地址
     */
    public static String getMacAddress() {
        String macAddress = getMacAddressByWifiInfo();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByNetworkInterface();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        return "please open wifi";
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return MAC地址
     */
    @SuppressLint("HardwareIds")
    private static String getMacAddressByWifiInfo() {
        try {
            WifiManager wifi = (WifiManager) Utils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return MAC地址
     */
    private static String getMacAddressByNetworkInterface() {
        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nis) {
                if (!ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02x:", b));
                    }
                    return res1.deleteCharAt(res1.length() - 1).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备MAC地址
     *
     * @return MAC地址
     */
    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    if (result.successMsg != null) {
                        return result.successMsg;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备厂商
     * <p>如Xiaomi</p>
     *
     * @return 设备厂商
     */

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取设备型号
     * <p>如MI2SC</p>
     *
     * @return 设备型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 关机
     * <p>需要root权限或者系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     */
    public static void shutdown() {
        ShellUtils.execCmd("reboot -p", true);
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Utils.getContext().startActivity(intent);
    }

    /**
     * 重启
     * <p>需要root权限或者系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     */
    public static void reboot() {
        ShellUtils.execCmd("reboot", true);
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        Utils.getContext().sendBroadcast(intent);
    }

    /**
     * 重启
     * <p>需系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     *
     * @param reason 传递给内核来请求特殊的引导模式，如"recovery"
     */
    public static void reboot(String reason) {
        PowerManager mPowerManager = (PowerManager) Utils.getContext().getSystemService(Context.POWER_SERVICE);
        try {
            mPowerManager.reboot(reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启到recovery
     * <p>需要root权限</p>
     */
    public static void reboot2Recovery() {
        ShellUtils.execCmd("reboot recovery", true);
    }

    /**
     * 重启到bootloader
     * <p>需要root权限</p>
     */
    public static void reboot2Bootloader() {
        ShellUtils.execCmd("reboot bootloader", true);
    }
}
