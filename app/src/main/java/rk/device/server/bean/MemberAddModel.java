package rk.device.server.bean;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * 添加用户
 */
public class MemberAddModel {

    /**
     * @params access_token : 3354d11737c59f25ed392f56de6bd3c0af457c06
     * @params uuid : 设备唯一ID
     * @params peopleId : 访客人员唯一ID
     * @params popeName : 访客姓名
     * @params popedomType : 权限类型：1开门，2巡更，3指纹身份认证
     * @params type : 授权方式 1卡 2人脸 3数字密码
     * @params data : 根据开门类型写入不同的数据，开门方式1：卡号，开门方式2：人脸，开门方式3：数字密码
     * @params startTime : 开始时间
     * @params endTime : 结束时间
     */

    private String access_token;
    private String uuid;
    private String popeName;
    private int popedomType;
    private int type;
    private String data;
    private String startTime;
    private String endTime;

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

    public String getPopeName() {
        return popeName;
    }

    public void setPopeName(String popeName) {
        this.popeName = popeName;
    }

    public int getPopedomType() {
        return popedomType;
    }

    public void setPopedomType(int popedomType) {
        this.popedomType = popedomType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
