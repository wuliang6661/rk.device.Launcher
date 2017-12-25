package rk.device.launcher.bean.event;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by wuliang on 2017/12/22.
 * <p>
 * 传递休眠时图片地址的对象
 */

public class SleepImageEvent implements Serializable {


    private List<File> fileList;

    public SleepImageEvent(List<File> files) {
        this.fileList = files;
    }


    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
}
