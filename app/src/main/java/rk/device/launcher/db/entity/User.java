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
	public String uniqueId;
	
	@NotNull
	public String name;

	@Generated(hash = 1692395986)
	public User(Long id, @NotNull String uniqueId, @NotNull String name) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.name = name;
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
}
