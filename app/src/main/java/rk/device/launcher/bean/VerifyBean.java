package rk.device.launcher.bean;

/**
 * Created by Zola on 2017/7/11.
 */

public class VerifyBean {

    /**
     * ismatch : false
     * session_id :
     * errormsg : ERROR_PERSON_NOT_EXISTED
     * errorcode : -1303
     * confidence : 0
     */

    private boolean ismatch;
    private String session_id;
    private String errormsg;
    private int errorcode;
    private int confidence;

    public boolean isIsmatch() {
        return ismatch;
    }

    public void setIsmatch(boolean ismatch) {
        this.ismatch = ismatch;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

}
