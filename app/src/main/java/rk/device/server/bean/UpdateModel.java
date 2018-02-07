package rk.device.server.bean;

/**
 * Created by hanbin on 2018/2/5.
 */

public class UpdateModel {

    /**
     * @params uuid : UUID
     * @params code : 版本code
     * @params ver : 版本号
     * @params file : 升级地址
     * @params note : 升级日志
     */

    private String uuid;
    private int code;
    private String ver;
    private String file;
    private String note;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
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
