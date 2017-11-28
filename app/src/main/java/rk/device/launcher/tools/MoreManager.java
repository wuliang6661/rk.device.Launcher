package rk.device.launcher.tools;

import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rk.device.launcher.api.T;
import rk.device.launcher.event.BlueToothEvent;
import rk.device.launcher.global.LauncherApplication;

/**
 * Created by hb on 16/6/29.
 */
public class MoreManager {


    private static BluetoothClient mClient;
    private static BlueToothEvent mEvent;
    private static BleGattProfile mProfile;

    private static final String serviceUuid = "00005500-d102-11e1-9b23-00025b00a5a5";
    private static final String characterUuid = "00005501-d102-11e1-9b23-00025b00a5a5";

    /**
     * 获取单例的蓝牙连接对象
     */
    public static BluetoothClient getBluetoothClient() {
        if (mClient == null) {
            synchronized (MoreManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(LauncherApplication.getContext());
                }
            }
        }
        return mClient;
    }


    /**
     * 设置当前蓝牙连接对象地址数据
     */
    public static void setBlueToothEvent(BlueToothEvent event) {
        mEvent = event;
    }

    /**
     * 设置当前蓝牙连接serviceUUID地址列表
     */
    public static void setProfile(BleGattProfile profile) {
        mProfile = profile;
    }


    /**
     * 调取蓝牙开锁
     */
    public static void openLock(int time) {
        new MoreManager().writeBlueByte(BlueToothUtils.openLock(time));
    }


    /**
     * 同步系统时间
     */
    public static void syncBlueTime(int time) {
        new MoreManager().writeBlueByte(BlueToothUtils.getBlueTimeByte(time));
    }


    /**
     * 向蓝牙写入数据
     */
    private void writeBlueByte(byte[] data) {
        for (int i = 0; i < mProfile.getServices().size(); i++) {
            //            Log.i("BlueDevice", String.valueOf(data.getServices().get(i).getUUID()));
            if (String.valueOf(mProfile.getServices().get(i).getUUID()).equals(serviceUuid)) {
                //                Log.i("BlueDevice", "send");
                UUID uuid = mProfile.getServices().get(i).getUUID();
                writeContent(uuid, mProfile.getServices().get(i).getCharacters(), data);

            }
        }
    }

    int cut;

    private void writeContent(UUID uuid, List<BleGattCharacter> characters, byte[] data) {
        for (int i = 0; i < characters.size(); i++) {
            if (String.valueOf(characters.get(i).getUuid()).equals(characterUuid)) {
                UUID characUuid = characters.get(i).getUuid();
                List<byte[]> list = cutData(data);
                cut = list.size();
                for (int j = 0; j < list.size(); j++) {
//                    Log.e("wuliang", Arrays.toString(list.get(j)));
                    write(j, uuid, characUuid, list.get(j));
                }
            }
        }
    }


    public static List<byte[]> cutData(byte[] data) {
        List<byte[]> returnList = new ArrayList<>();
        List<Byte> tempList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            tempList.add(data[i]);
            if (tempList.size() == 20 || i == data.length - 1) {
                returnList.add(getByteArrFromTempList(tempList));
                tempList.clear();
            }
        }
        return returnList;
    }

    private static byte[] getByteArrFromTempList(List<Byte> tempList) {
        byte[] byteArr = new byte[tempList.size()];
        for (int i = 0; i < tempList.size(); i++) {
            byteArr[i] = tempList.get(i);
        }
        return byteArr;
    }


    private void write(int j, UUID uuid, UUID characUuid, byte[] content) {
        mClient.write(mEvent.mac, uuid, characUuid, content,
                new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                        Log.i("BlueDevice", "code:" + code);
                        if (code == Constants.REQUEST_SUCCESS) {
                            if (j == cut) {
                                mClient.clearRequest(mEvent.mac, Constants.REQUEST_WRITE);
                                T.showShort("发送成功");
                            }
                        } else if (code == Constants.REQUEST_OVERFLOW) {
                            if (j == cut) {
                                mClient.clearRequest(mEvent.mac, Constants.REQUEST_WRITE);
                            } else {
                                write(j, uuid, characUuid, content);
                            }
                        }
                    }

                });
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {

        }
    }


    private void readResult(UUID uuid, UUID characUuid) {
        mClient.read(mEvent.mac, uuid, characUuid, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == Constants.REQUEST_SUCCESS) {
                    Log.e("wuliang", data.toString());
                    mClient.clearRequest(mEvent.mac, Constants.REQUEST_READ);
                }
            }
        });
    }
}
