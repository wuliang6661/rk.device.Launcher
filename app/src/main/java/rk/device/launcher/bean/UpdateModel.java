package rk.device.launcher.bean;

/**
 * Created by hanbin on 17/5/5.
 */

public class UpdateModel {

    /**
     * ver : 1.0.55
     * code : 55
     * file :
     * note : fixed some bug
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
