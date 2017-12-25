package rk.device.launcher.bean;

/**
 * Created by Zola on 2017/7/11.
 */

public class VerifyBO {


    /**
     * msg : 欢迎回家
     * image : http://rkfaceclouds.oss-cn-hangzhou.aliyuncs.com/20171220/1513767301523.jpg
     * isrepeat : true
     * ismatch : true
     * confidence : 100
     * name : 程永飞
     * reqImage : http://rkfaceclouds.oss-cn-hangzhou.aliyuncs.com/20171220/1513767301523.jpg
     */

    private String msg;
    private String image;
    private boolean isrepeat;
    private boolean ismatch;
    private int confidence;
    private String name;
    private String reqImage;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isIsrepeat() {
        return isrepeat;
    }

    public void setIsrepeat(boolean isrepeat) {
        this.isrepeat = isrepeat;
    }

    public boolean isIsmatch() {
        return ismatch;
    }

    public void setIsmatch(boolean ismatch) {
        this.ismatch = ismatch;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReqImage() {
        return reqImage;
    }

    public void setReqImage(String reqImage) {
        this.reqImage = reqImage;
    }
}
