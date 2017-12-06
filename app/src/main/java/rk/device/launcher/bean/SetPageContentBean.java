package rk.device.launcher.bean;

import java.io.Serializable;

/**
 * Created by mundane on 2017/12/6 下午5:03
 */

public class SetPageContentBean implements Serializable {
	public String content;
	
	public SetPageContentBean(String content) {
		this.content = content;
	}
}
