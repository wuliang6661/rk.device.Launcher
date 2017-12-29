package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mundane on 2017/12/27 下午3:00
 */
@Entity
public class User {
    @Id(autoincrement = true)
    public Long id;

    @NotNull
    @Index(unique = true)
    public String uniqueId;    // 这就是那个唯一标识id

    @NotNull
    public String name;        // 用户名称


    /**
     * 1:  开门权限，只能开门
     * 2:  巡更权限，向服务器发送一条上报消息，代表已巡更
     * 3:  管理员权限，可以开门、巡更、或进入设置页面更改设置
     */
    @NotNull
    public String popedomType; // 权限类型

    public String cardNo;

    public String fingerID;

    public String faceID;

    public int passWord;

    public long startTime;

    public long endTime;

    public String fingerCode;

    public int uploadStatus;

    @Generated(hash = 1246737531)
    public User(Long id, @NotNull String uniqueId, @NotNull String name,
                @NotNull String popedomType, String cardNo, String fingerID, String faceID,
                int passWord, long startTime, long endTime, String fingerCode, int uploadStatus) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.name = name;
        this.popedomType = popedomType;
        this.cardNo = cardNo;
        this.fingerID = fingerID;
        this.faceID = faceID;
        this.passWord = passWord;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fingerCode = fingerCode;
        this.uploadStatus = uploadStatus;
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

    public String getPopedomType() {
        return this.popedomType;
    }

    public void setPopedomType(String popedomType) {
        this.popedomType = popedomType;
    }

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getFingerID() {
        return this.fingerID;
    }

    public void setFingerID(String fingerID) {
        this.fingerID = fingerID;
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

}
