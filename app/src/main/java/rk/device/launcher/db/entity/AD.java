package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wuliang on 2018/3/17.
 * <p>
 * 存放广告的表
 */

@Entity
public class AD {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    @NotNull
    private String adID;

    @Unique
    @NotNull
    private String image;

    private int beginTime;  //开始时间
    private int endTime;    //结束时间
    private int createTime; //创建时间时间
    private int updateTime; //更新时间


    @Generated(hash = 1268764516)
    public AD(Long id, @NotNull String adID, @NotNull String image, int beginTime,
              int endTime, int createTime, int updateTime) {
        this.id = id;
        this.adID = adID;
        this.image = image;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 293030498)
    public AD() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdID() {
        return this.adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }


}
