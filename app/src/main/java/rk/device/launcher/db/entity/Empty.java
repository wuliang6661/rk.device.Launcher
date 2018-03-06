package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mundane on 2018/3/6 上午10:27
 * 空表, 为了创建rk.db而使用的
 */
@Entity
public class Empty {
    @Id
    private Long id;
    private String name;
    @Generated(hash = 1321916094)
    public Empty(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 781928954)
    public Empty() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
