package rk.device.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mundane on 2017/11/14 下午2:36
 */

public class SetDoorRvBean implements Parcelable {
	public boolean isChecked;
	public String text;

	public SetDoorRvBean(String text) {
		this.text = text;
	}

	public SetDoorRvBean(boolean isChecked, String text) {
		this.isChecked = isChecked;
		this.text = text;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
		dest.writeString(this.text);
	}

	protected SetDoorRvBean(Parcel in) {
		this.isChecked = in.readByte() != 0;
		this.text = in.readString();
	}

	public static final Parcelable.Creator<SetDoorRvBean> CREATOR = new Parcelable.Creator<SetDoorRvBean>() {
		@Override
		public SetDoorRvBean createFromParcel(Parcel source) {
			return new SetDoorRvBean(source);
		}

		@Override
		public SetDoorRvBean[] newArray(int size) {
			return new SetDoorRvBean[size];
		}
	};
}
