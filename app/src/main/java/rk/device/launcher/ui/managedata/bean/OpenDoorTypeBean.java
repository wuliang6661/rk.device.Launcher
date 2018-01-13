package rk.device.launcher.ui.managedata.bean;

/**
 * Created by mundane on 2018/1/5 下午4:21
 */

public class OpenDoorTypeBean {
    public String name;
    public int typeId;
    public boolean isChecked;

    public OpenDoorTypeBean(String name) {
        this.name = name;
    }

    public OpenDoorTypeBean(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public OpenDoorTypeBean(String name, int typeId, boolean isChecked) {
        this.name = name;
        this.typeId = typeId;
        this.isChecked = isChecked;
    }

    public OpenDoorTypeBean(String name, int typeId) {
        this.name = name;
        this.typeId = typeId;
    }
}
