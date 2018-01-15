package rk.device.launcher.bean.event;

/**
 * Created by hanbin on 2018/1/13.
 * <p/>
 * 用于开门成功之后传递成功消息用的
 */

public class OpenDoorSuccessEvent {

    public String name;
    public int    type;

    public OpenDoorSuccessEvent(String name, int type) {
        this.name = name;
        this.type = type;
    }

}
