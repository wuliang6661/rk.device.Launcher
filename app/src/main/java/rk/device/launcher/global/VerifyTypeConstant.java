package rk.device.launcher.global;

/**
 * Created by hanbin on 2018/1/15.
 */

public interface VerifyTypeConstant {

    /**
     * 开门方式1 卡
     */
    int TYPE_CARD     = 1;
    /**
     * 开门方式2 指纹
     */
    int TYPE_FINGER   = 2;
    /**
     * 开门方式3 人脸
     */
    int TYPE_FACE     = 3;
    /**
     * 开门方式4 密码
     */
    int TYPE_PASSWORD = 4;
    /**
     * 开门方式5 二维码
     */
    int TYPE_QR_CODE  = 5;
    /**
     * 开门方式6 远程开门
     */
    int TYPE_API      = 6;

}
