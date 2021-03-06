package rk.device.launcher.utils.bluetools;

import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import rk.device.launcher.api.T;
import rk.device.launcher.bean.event.BlueToothEvent;
import rk.device.launcher.base.LauncherApplication;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * Created by wuliang on 16/6/29.
 * <p>
 * 调取蓝牙锁方法封装在这里
 */
public class MoreManager {


    private static BluetoothClient mClient;
    private static BlueToothEvent mEvent;
    private static BleGattProfile mProfile;

//    private static final String serviceUuid = "00005500-d102-11e1-9b23-00025b00a5a5";
//    private static final String characterUuid = "00005501-d102-11e1-9b23-00025b00a5a5";

    private static final String serviceUuid = "00005500-d102-11e1-9b23-00025b00a5a5";
    private static final String characterUuid = "00005501-d102-11e1-9b23-00025b00a5a5";

    private static final String sendCharacterUuid = "00005502-d102-11e1-9b23-00025b00a5a5";


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
     * 获取锁的网络状态
     */
    public static void getLockNetBoll() {
        new MoreManager().writeBlueByte(BlueToothUtils.getLockNet());
    }


    /**
     * 向蓝牙写入数据
     */
    private void writeBlueByte(byte[] data) {
        for (int i = 0; i < mProfile.getServices().size(); i++) {
            if (String.valueOf(mProfile.getServices().get(i).getUUID()).equals(serviceUuid)) {
                UUID uuid = mProfile.getServices().get(i).getUUID();
                writeContent(uuid, mProfile.getServices().get(i).getCharacters(), data);
            }
        }
    }

    private int cut;     //数据分成20每份 ， 这个判断总共有多少份

    private void writeContent(UUID uuid, List<BleGattCharacter> characters, byte[] data) {
        for (int i = 0; i < characters.size(); i++) {
            if (String.valueOf(characters.get(i).getUuid()).equals(sendCharacterUuid)) {
                UUID characUuid = characters.get(i).getUuid();
                List<byte[]> list = cutData(data);
                cut = list.size();
                for (int j = 0; j < list.size(); j++) {
                    write(j, uuid, characUuid, list.get(j));
                }
            }
        }
    }


    /**
     * 切割数据成20等份
     */
    private static List<byte[]> cutData(byte[] data) {
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
                        if (code == REQUEST_SUCCESS) {
                            if (j == cut - 1) {
                                mClient.clearRequest(mEvent.mac, Constants.REQUEST_WRITE);
                                T.showShort("发送成功");
                            }
                        } else if (code == Constants.REQUEST_OVERFLOW) {
                            if (j == cut - 1) {
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
            e.printStackTrace();
        }
    }


    /**
     * 蓝牙建立连接之后，开始监听上报数据
     */
    public static void blueReadClient() {
        for (int i = 0; i < mProfile.getServices().size(); i++) {
            if (String.valueOf(mProfile.getServices().get(i).getUUID()).equals(serviceUuid)) {
                UUID uuid = mProfile.getServices().get(i).getUUID();
                readContent(uuid, mProfile.getServices().get(i).getCharacters());
            }
        }
    }


    private static void readContent(UUID uuid, List<BleGattCharacter> characters) {
        for (int i = 0; i < characters.size(); i++) {
            if (String.valueOf(characters.get(i).getUuid()).equals(characterUuid)) {
                UUID characUuid = characters.get(i).getUuid();
                read(uuid, characUuid);
            }
        }
    }


    private static void read(UUID serviceID, UUID charactersId) {
        mClient.notify(mEvent.mac, serviceID, charactersId, new BleNotifyResponse() {
            @Override
            public void onResponse(int code) {
                Log.e("wuliang", "code == " + code);
            }

            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                Log.e("wuliang", Arrays.toString(value));
            }
        });
    }
}