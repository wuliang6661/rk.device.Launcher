package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

/**
 * Created by mundane on 2017/12/27 下午3:00 授权信息数据表
 */
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long              id;

    @NotNull
    @Index(unique = true)
    private String            uniqueId;              // 这就是那个唯一标识id

    @NotNull
    private String            name;                  // 用户名称

    /**
     * 1: 开门权限，只能开门 2: 巡更权限，向服务器发送一条上报消息，代表已巡更 3: 管理员权限，可以开门、巡更、或进入设置页面更改设置
     */
    @NotNull
    private int               role;                  // 权限类型

    private String            faceID;

    private int               passWord;

    private String            fingerCode;

    private int               uploadStatus;

    private int               status;                //1：正常，2：待添加，3：待更新，4：待删除

    private long              startTime;

    private long              endTime;

    private long              createTime;

    private long              updateTime;

    @Generated(hash = 1648016514)
    public User(Long id, @NotNull String uniqueId, @NotNull String name, int role,
            String faceID, int passWord, String fingerCode, int uploadStatus,
            int status, long startTime, long endTime, long createTime,
            long updateTime) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.name = name;
        this.role = role;
        this.faceID = faceID;
        this.passWord = passWord;
        this.fingerCode = fingerCode;
        this.uploadStatus = uploadStatus;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 586692638)
    public User() {
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole() {
        return this.role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getFaceID() {
        return this.faceID;
    }

    public void setFaceID(String faceID) {
        this.faceID = faceID;
    }

    public int getPassWord() {
        return this.passWord;
    }

    public void setPassWord(int passWord) {
        this.passWord = passWord;
    }

    public String getFingerCode() {
        return this.fingerCode;
    }

    public void setFingerCode(String fingerCode) {
        this.fingerCode = fingerCode;
    }

    public int getUploadStatus() {
        return this.uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    
}
