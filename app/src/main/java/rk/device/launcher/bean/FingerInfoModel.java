package rk.device.launcher.bean;

/**
 * Created by hanbin on 2018/1/10.
 * <p/>
 * 获取指纹模块版本信息
 */

public class FingerInfoModel {

    /**
     * @param 版本 Version : 7M41.43/stdrl_046.1
     * @param 传感技术 Sensor : TS10xx/A172/DK7
     * @param 型号 RegMode : NCNR
     * @param 最大支持 MaxUser : 194
     * @param Time : Oct 20-14:14:51
     */
    private String Version;
    private String Sensor;
    private String RegMode;
    private int MaxUser;
    private String Time;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public String getSensor() {
        return Sensor;
    }

    public void setSensor(String Sensor) {
        this.Sensor = Sensor;
    }

    public String getRegMode() {
        return RegMode;
    }

    public void setRegMode(String RegMode) {
        this.RegMode = RegMode;
    }

    public int getMaxUser() {
        return MaxUser;
    }

    public void setMaxUser(int MaxUser) {
        this.MaxUser = MaxUser;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }
}
