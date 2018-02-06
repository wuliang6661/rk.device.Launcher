package rk.device.server.api;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * Api请求URI
 */
public interface HttpRequestUri {

    /**
     * 用户添加
     */
    String MEMBER_ADD    = "/app/api/v1/member/add";

    /**
     * 删除（用户，卡，密码）
     */
    String DELETE        = "/app/api/v1/member/del";

    /**
     * 一键开门
     */
    String OPEN          = "/app/api/v1/device/open";

    /**
     * 获取设备状态
     */
    String DEVICE_STATUS = "/app/api/v1/device/status";

    /**
     * 升级接口
     */
    String UPDATE        = "/app/api/v1/public/update";

    /**
     * 广告
     */
    String AD            = "/app/api/v1/public/ad";

    /**
     * 上传人脸图片
     */
    String UPLOAD        = "/app/api/v1/public/upload";

}
