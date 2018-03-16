package rk.device.server.api;

/**
 * Created by hanbin on 2018/2/7.
 * <p>
 * 请求返回码
 */

public interface HttpResponseCode {

    /**
     * 请求成功
     */
    int Success = 0;

    /**
     * 请求失败
     */
    int Error = 1;

    /**
     * 对象不存在
     */
    int OBJECT_NO_FOUND = 6;

    /**
     * 对象已存在
     */
    int OBJECT_EXITE = 7;

    /**
     * 无效的操作
     */
    int NO_CAOZUO = 5;

    /**
     * 无效的令牌
     */
    int WUXIAO_LINGPAI = 4;

    /**
     * 无效的消息格式
     */
    int NO_JSON = 3;

}
