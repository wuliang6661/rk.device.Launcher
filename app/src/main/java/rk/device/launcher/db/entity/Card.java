package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hanbin on 2018/2/28.
 * <p/>
 * 卡
 */
@Entity
public class Card {

    @Id(autoincrement = true)
    private Long   id;

    @NotNull
    private String personId;   //用户唯一标识ID
    @NotNull
    private String number;     //卡号
    private int    status;     //1：正常，2：待添加，3：待更新，4：待删除
    private int    beginTime;  //开始时间
    private int    endTime;    //结束时间
    private int    createTime; //创建时间时间
    private int    updateTime; //更新时间

    @Generated(hash = 221804366)
    public Card(Long id, @NotNull String personId, @NotNull String number, int status,
            int beginTime, int endTime, int updateTime) {
        this.id = id;
        this.personId = personId;
        this.number = number;
        this.status = status;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 52700939)
    public Card() {
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

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public int getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
