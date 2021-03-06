package rk.device.server.api;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * Api请求URI
 */
public interface HttpRequestUri {

    String VERSION = "/app/api/v1";

    String GET_TOKEN = VERSION + "/device/get_token";

    /**
     * 用户添加
     */
    String MEMBER_ADD = VERSION + "/person/add";

    /**
     * 修改用户
     */
    String MEMBER_UPDATE = VERSION + "/person/edit";

    /**
     * 删除用户
     */
    String MEMBER_DELETE = VERSION + "/person/delete";


    /**
     * 新增人脸
     */
    String FACE_ADD = VERSION + "/credential_face/add";

    /**
     * 修改人脸
     */
    String FACE_UPDATE = VERSION + "/credential_face/edit";

    /**
     * 删除人脸
     */
    String FACE_DELETE = VERSION + "/credential_face/delete";


    /**
     * 新增密码
     */
    String PASSWORD_ADD = VERSION + "/credential_password/add";

    /**
     * 修改密码
     */
    String PASSWORD_UPDATE = VERSION + "/credential_password/edit";


    /**
     * 删除密码
     */
    String PASSWORD_DELETE = VERSION + "/credential_password/delete";


    /**
     * 新增卡
     */
    String CARD_ADD = VERSION + "/credential_card/add";

    /**
     * 修改卡
     */
    String CARD_UPDATE = VERSION + "/credential_card/edit";

    /**
     * 删除卡
     */
    String CARD_DELETE = VERSION + "/credential_card/delete";


    /**
     * 删除指纹
     */
    String FINGER_DELETE = VERSION + "/credential_fingerprint/delete";


    /**
     * 一键开门
     */
    String OPEN = VERSION + "/device/open";

    /**
     * 获取设备状态
     */
    String DEVICE_STATUS = VERSION + "/device/status";

    /**
     * 升级接口
     */
    String UPDATE = VERSION + "/device/upgrade";

    /**
     * 增加广告
     */
    String ADD_GUANGGAO = VERSION + "/ad/add";

    /**
     * 修改广告
     */
    String UPDATATE_GUANGGAO = VERSION + "/ad/edit";

    /**
     * 删除广告
     */
    String DELETE_GUANGGAO = VERSION + "/ad/delete";


    /**
     * 配置设备状态
     */
    String UPDATE_TIME = VERSION + "/device/config";


}
