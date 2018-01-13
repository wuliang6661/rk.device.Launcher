package rk.device.launcher.bean;

import java.io.Serializable;

/**
 * Created by wuliang on 2018/1/13.
 * <p>
 * 鉴权token
 */

public class TokenBo implements Serializable {


    /**
     * access_token : f383f3cc-25cf-4d8d-992e-6037d50e8dff
     * expires_in : 7200
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
