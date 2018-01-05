package rk.device.launcher.ui.managedata.bean;

/**
 * Created by mundane on 2018/1/5 下午1:42
 */

public class UserBean {
    public String name;
    public String date;
    public String time;
    public String openWay; // 开门方式

    public UserBean(String name, String date, String time, String openWay) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.openWay = openWay;
    }

    public UserBean() {
    }
}
