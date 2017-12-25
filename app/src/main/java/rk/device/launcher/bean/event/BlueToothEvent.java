package rk.device.launcher.bean.event;

/**
 * 选择蓝牙
 * Created by hanbin on 2017/11/27.
 */
public class BlueToothEvent {
    public String mac;
    public String name;

    public BlueToothEvent(String mac, String name) {
        this.mac = mac;
        this.name = name;
    }
}
