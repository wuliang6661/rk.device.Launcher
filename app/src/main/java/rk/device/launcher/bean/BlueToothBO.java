package rk.device.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hanbin on 2017/11/24.
 */

public class BlueToothBO implements Parcelable {
    private String name;
    private int    rssi;
    private byte[] scanRecord;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.rssi);
        dest.writeByteArray(this.scanRecord);
        dest.writeString(this.address);
    }

    public BlueToothBO() {
    }

    protected BlueToothBO(Parcel in) {
        this.name = in.readString();
        this.rssi = in.readInt();
        this.scanRecord = in.createByteArray();
        this.address = in.readString();
    }

    public static final Creator<BlueToothBO> CREATOR = new Creator<BlueToothBO>() {
        @Override
        public BlueToothBO createFromParcel(Parcel source) {
            return new BlueToothBO(source);
        }

        @Override
        public BlueToothBO[] newArray(int size) {
            return new BlueToothBO[size];
        }
    };
}
