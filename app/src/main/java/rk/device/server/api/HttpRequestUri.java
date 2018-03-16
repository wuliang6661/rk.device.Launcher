package rk.device.server.api;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * Api请求URI
 */
public interface HttpRequestUri {

    String VERSION = "/app/api/v1";

    String GET_TOKEN     = VERSION + "/public/get_token";

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
    String FINGER_DELETE = VERSION + "/finger/delete";




    /**
     * 删除（用户，卡，密码）
     */
    String DELETE = VERSION + "/member/del";

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
    String UPDATE = VERSION + "/public/update";

    /**
     * 广告
     */
    String AD = VERSION + "/public/ad";

    /**
     * 上传人脸图片
     */
    String UPLOAD = VERSION + "/member/upload";

    /**
     * 删除人脸图片
     */
    String DELETE_FACE = VERSION + "/member/delFace";

    /**
     * 更新一体机时间
     */
    String UPDATE_TIME = VERSION + "/public/updatetime";
}
