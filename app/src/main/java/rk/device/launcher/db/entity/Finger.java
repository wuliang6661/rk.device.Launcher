package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by hanbin on 2018/2/28.
 * <p/>
 * 指纹
 */
@Entity
public class Finger {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String personId;   //用户唯一标识ID

    @NotNull
    private int fingerId;   //指纹ID
    private int status;     //1：正常，2：待添加，3：待更新，4：待删除
    private int number;     //当前指纹，1，2，3
    private String fingerName;//指纹名称
    private int beginTime;  //开始时间
    private int endTime;    //结束时间
    private int createTime; //创建时间时间
    private int updateTime; //更新时间

    @Generated(hash = 1731663882)
    public Finger(Long id, @NotNull String personId, int fingerId, int status, int number,
                  String fingerName, int beginTime, int endTime, int createTime, int updateTime) {
        this.id = id;
        this.personId = personId;
        this.fingerId = fingerId;
        this.status = status;
        this.number = number;
        this.fingerName = fingerName;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 814071080)
    public Finger() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonId() {
        return this.personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public int getFingerId() {
        return this.fingerId;
    }

    public void setFingerId(int fingerId) {
        this.fingerId = fingerId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFingerName() {
        return fingerName;
    }

    public void setFingerName(String fingerName) {
        this.fingerName = fingerName;
    }
}
