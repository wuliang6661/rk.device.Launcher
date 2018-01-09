package rk.device.launcher.bean.event;

/**
 * Created by hanbin on 2018/1/9.
 * <p/>
 * 当前指纹注册进度
 */

public class FingerRegisterProgressEvent {
    public int progress;

    public FingerRegisterProgressEvent(int progress) {
        this.progress = progress;
    }
}
