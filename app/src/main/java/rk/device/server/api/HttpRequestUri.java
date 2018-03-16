package rk.device.server.api;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * Api请求URI
 */
public interface HttpRequestUri {

    String VERSION       = "/app/api/v1";

    String GET_TOKEN     = VERSION + "/device/get_token";

    /**
     * 用户添加
     */
    String MEMBER_ADD    = VERSION + "/member/add";

    /**
     * 删除（用户，卡，密码）
     */
    String DELETE        = VERSION + "/member/del";

    /**
     * 一键开门
     */
    String OPEN          = VERSION + "/device/open";

    /**
     * 获取设备状态
     */
    String DEVICE_STATUS = VERSION + "/device/status";

    /**
     * 升级接口
     */
    String UPDATE        = VERSION + "/public/update";

    /**
     * 广告
     */
    String AD            = VERSION + "/public/ad";

    /**
     * 上传人脸图片
     */
    String UPLOAD        = VERSION + "/member/upload";

    /**
     * 删除人脸图片
     */
    String DELETE_FACE   = VERSION + "/member/delFace";

    /**
     * 更新一体机时间
     */
    String UPDATE_TIME   = VERSION + "/public/updatetime";
}
