package rk.device.launcher.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mundane on 2017/12/27 下午3:00
 */
@Entity(generateGettersSetters = false)
public class User {
	@Id(autoincrement = true)
	public Long id;
	
	@NotNull
	@Index(unique = true)
	public String uniqueId; // 这就是那个唯一标识id
	
	@NotNull
	public String name;
	
	@NotNull
	public String popedomType;
	
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
			int passWord, long startTime, long endTime, String fingerCode,
			int uploadStatus) {
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

}
