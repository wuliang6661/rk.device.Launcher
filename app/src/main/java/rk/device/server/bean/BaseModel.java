package rk.device.server.bean;

/**
 * Created by hanbin on 2018/2/5.
 */

public class BaseModel {

    /**
     * @params access_token : token
     * @params uuid : UUID
     */

    private String access_token;
    private String uuid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
