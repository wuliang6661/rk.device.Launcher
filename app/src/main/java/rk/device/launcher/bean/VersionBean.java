package rk.device.launcher.bean;

import java.io.Serializable;

/**
 * Created by wuliang on 2017/11/23.
 * <p>
 * 版本更新检测bean
 */

public class VersionBean implements Serializable {


    /**
     * ver : 1.0.1
     * code : 2
     * file : http://clouddevice.oss-cn-hangzhou.aliyuncs.com/package/apk/app-device-release.apk
     * note : 1.添加新设备：密码打卡锁、智能开合帘
     * <p>
     * 2.动态更新设备状态
     * <p>
     * 3.优化逻辑，提升体验
     * <p>
     * 4.fix bugs
     */

    private String ver;
    private int code;
    private String file;
    private String note;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
