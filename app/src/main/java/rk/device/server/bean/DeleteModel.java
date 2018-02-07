package rk.device.server.bean;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 删除（用户，卡，密码）
 */
public class DeleteModel {

    /**
     * @params access_token : token
     * @params uuid : 设备唯一ID
     * @params peopleId : 访客人员唯一ID
     * @params type : 1：用户，2：卡，3：密码
     */

    private String access_token;
    private String uuid;
    private String peopleId;
    private int    type;

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

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
