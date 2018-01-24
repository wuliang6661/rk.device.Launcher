package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import rk.device.launcher.utils.TimeUtils;

/**
 * Created by mundane on 2018/1/2 下午2:19
 * 开门记录数据表格
 */
@Entity
public class Record {
    @Id(autoincrement = true)
    private Long id;

    /**
     * 唯一标识ID
     */
    @NotNull
    @Unique
    private String uniqueId;

    /**
     * 姓名
     */
    private String popeName;

    /**
     * 访客人员唯一ID
     */
    @NotNull
    private String peopleId;

    /**
     * 开门方式
     * 1 卡
     * 2 指纹
     * 3 人脸
     * 4 密码
     * 5 二维码
     * 6 远程开门
     */
    @NotNull
    private int openType;

    /**
     * 根据开门类型写入不同的数据；
     * 开门方式1 卡：录入卡号；
     * 开门方式2 指纹：指纹ID；
     * 开门方式3 人脸：人脸ID；
     * 开门方式4 密码：开门密码；
     * 开门方式5、6 此字段为空；
     */
    private String data;

    /**
     * 刷卡时间
     */
    private int slide_data;

    /**
     * 生成记录的时间
     */
    private long cdate;

    @Transient
    private String dateText;

    @Transient
    private String openTypeText;


    @Generated(hash = 1487740252)
    public Record(Long id, @NotNull String uniqueId, String popeName,
            @NotNull String peopleId, int openType, String data, int slide_data,
            long cdate) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.popeName = popeName;
        this.peopleId = peopleId;
        this.openType = openType;
        this.data = data;
        this.slide_data = slide_data;
        this.cdate = cdate;
    }

    @Generated(hash = 477726293)
    public Record() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPopeName() {
        return this.popeName;
    }

    public void setPopeName(String popeName) {
        this.popeName = popeName;
    }

    public String getPeopleId() {
        return this.peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public int getOpenType() {
        return this.openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
        this.openTypeText = "";
        switch (openType) {
            case 1:
                this.openTypeText = "卡";
                break;
            case 2:
                this.openTypeText = "指纹";
                break;
            case 3:
                this.openTypeText = "人脸";
                break;
            case 4:
                this.openTypeText = "密码";
                break;
            case 5:
                this.openTypeText = "二维码";
                break;
            case 6:
                this.openTypeText = "远程开门";
                break;
            default:
                break;
        }
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getSlide_data() {
        return this.slide_data;
    }

    public void setSlide_data(int slide_data) {
        this.slide_data = slide_data;
    }

    public long getCdate() {
        return this.cdate;
    }

    public void setCdate(long cdate) {
        this.cdate = cdate;
        this.dateText = TimeUtils.getFormatDateByTimeStamp(cdate);
    }

    public String getDateText() {
        this.dateText = TimeUtils.getFormatDateByTimeStamp(cdate);
        return this.dateText;
    }

    public String getOpenTypeText(){
        this.openTypeText = "";
        switch (openType) {
            case 1:
                this.openTypeText = "卡";
                break;
            case 2:
                this.openTypeText = "指纹";
                break;
            case 3:
                this.openTypeText = "人脸";
                break;
            case 4:
                this.openTypeText = "密码";
                break;
            case 5:
                this.openTypeText = "二维码";
                break;
            case 6:
                this.openTypeText = "远程开门";
                break;
            default:
                break;
        }
        return this.openTypeText;
    }



}
