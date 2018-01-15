package rk.device.launcher.bean;

/**
 * Created by Administrator on 2018/1/13.
 */

public class TokenBo {

    /**
     * @params access_token : token
     * @params expires_in :
     */
    private String access_token;
    private String expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}
