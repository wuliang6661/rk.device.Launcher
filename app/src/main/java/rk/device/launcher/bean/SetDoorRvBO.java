package rk.device.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mundane on 2017/11/14 下午2:36
 */

public class SetDoorRvBO implements Parcelable {
	public boolean isChecked;
	public String text;
	public long sleepTime;

	public SetDoorRvBO(String text, long sleepTime) {
		this.text = text;
		this.sleepTime = sleepTime;
	}

	public SetDoorRvBO(String text) {
		this.text = text;
	}

	public SetDoorRvBO(boolean isChecked, String text) {
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
		dest.writeLong(this.sleepTime);
	}

	protected SetDoorRvBO(Parcel in) {
		this.isChecked = in.readByte() != 0;
		this.text = in.readString();
		this.sleepTime = in.readLong();
	}

	public static final Parcelable.Creator<SetDoorRvBO> CREATOR = new Parcelable.Creator<SetDoorRvBO>() {
		@Override
		public SetDoorRvBO createFromParcel(Parcel source) {
			return new SetDoorRvBO(source);
		}

		@Override
		public SetDoorRvBO[] newArray(int size) {
			return new SetDoorRvBO[size];
		}
	};
}
