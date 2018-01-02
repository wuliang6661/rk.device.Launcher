package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mundane on 2018/1/2 下午2:40
 * 人脸授权数据表
 */
@Entity
public class Authorization {
    @Id(autoincrement = true)
    private Long id;
    
    /**
     * 人脸信息ID
     */
    @NotNull
    private String faceID;

    /**
     * 人脸图片信息
     */
    @NotNull
    private String info;

    @Generated(hash = 2123988780)
    public Authorization(Long id, @NotNull String faceID, @NotNull String info) {
        this.id = id;
        this.faceID = faceID;
        this.info = info;
    }

    @Generated(hash = 631940243)
    public Authorization() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFaceID() {
        return this.faceID;
    }

    public void setFaceID(String faceID) {
        this.faceID = faceID;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
