package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

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
    @Unique
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
    private int cdate;

    @Generated(hash = 2006753078)
    public Record(Long id, @NotNull String uniqueId, String popeName,
            @NotNull String peopleId, int openType, String data, int slide_data,
            int cdate) {
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

    public int getCdate() {
        return this.cdate;
    }

    public void setCdate(int cdate) {
        this.cdate = cdate;
    }
}
