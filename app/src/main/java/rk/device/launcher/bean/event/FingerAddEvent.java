package rk.device.launcher.bean.event;

/**
 * Created by hanbin on 2018/1/3.
 * <p/>
 * 添加指纹命令
 */
public class FingerAddEvent {
    public String resultCode;

    public FingerAddEvent(String resultCode) {
        this.resultCode = resultCode;
    }
}
