package rk.device.launcher.tools;

import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.utils.ByteUtil;

/**
 * Created by wuliang on 2017/11/7.
 * <p>
 * 进行手机与蓝牙锁之间的信息体转化
 */

class BlueToothUtils {


    /**
     * 开启蓝牙锁
     */
    static byte[] openLock(int time) {
        byte[] start = new byte[]{0x01, (byte) 0xAA, 0x06, 0x01, 0x00, 0x21};
        byte[] type = new byte[]{0x0A};      //除了第一个0x0A为设置为一键开门，后面数据没卵用
        byte[] id = ByteUtil.int2Bytes(1);    //密码序列
        byte[] val1 = ByteUtil.int2Bytes(1);    //数据1
        byte[] val2 = ByteUtil.int2Bytes(1);    //数据2
        byte[] time_start = ByteUtil.int2Bytes(time - 300);    //密码生效时间   (现在的时间-300秒)  这个有用，开锁时间必须在这有效时间内
        byte[] time_stop = ByteUtil.int2Bytes(time + 300);    //密码失效时间   (现在的时间+300秒)

        byte[] openData;
        openData = ByteUtil.byteMerger(type, id);
        openData = ByteUtil.byteMerger(openData, val1);
        openData = ByteUtil.byteMerger(openData, val2);
        openData = ByteUtil.byteMerger(openData, time_start);
        openData = ByteUtil.byteMerger(openData, time_stop);
        byte crc = CrcTobyte((byte) 0xAA, (byte) 0x06, (byte) 0x01, new byte[]{0x00, 0x21}, openData);

        byte[] end = new byte[]{0x03};
        byte[] sun;   //数据集合合并之后的总值
        sun = ByteUtil.byteMerger(start, new byte[]{crc});
        sun = ByteUtil.byteMerger(sun, openData);
        sun = ByteUtil.byteMerger(sun, end);
        return transBytes(sun);
    }


    /**
     * 获取修改时间的byte数组
     * <p>
     * time : 单位秒
     */
    static byte[] getBlueTimeByte(int time) {
        byte[] start = new byte[]{0x01, (byte) 0xAA, 0x06, 0x00, 0x00, 0x04};
        byte[] times = ByteUtil.int2Bytes(time);    //默认占四个字节
        byte crc = CrcTobyte((byte) 0xAA, (byte) 0x06, (byte) 0x00, new byte[]{0x00, 0x04}, times);
        byte[] end = new byte[]{0x03};
        byte[] sun;   //数据集合合并之后的总值
        sun = ByteUtil.byteMerger(start, new byte[]{crc});
        sun = ByteUtil.byteMerger(sun, times);
        sun = ByteUtil.byteMerger(sun, end);
        return transBytes(sun);
    }


    /**
     * CRC校验
     */
    private static byte CrcTobyte(byte start, byte device_type, byte cmd_type, byte[] len, byte[] data) {
        int n;
        byte u8CRC_sum_Value;
        u8CRC_sum_Value = start;
        u8CRC_sum_Value ^= device_type;
        u8CRC_sum_Value ^= cmd_type;
        u8CRC_sum_Value ^= len[1] & 0xff;
        u8CRC_sum_Value ^= len[0] & 0xff;

        for (n = 0; n < data.length; n++) {
            u8CRC_sum_Value ^= data[n];
        }
        return u8CRC_sum_Value;
    }


    /**
     * 根据byte大小判断是否需要转译
     */
    private static byte[] transBytes(byte[] sun) {
        List<Byte> allSun = new ArrayList<>();
        for (int i = 0; i < sun.length; i++) {
            if (i == 0 || i == sun.length - 1) {
                allSun.add(sun[i]);
            } else {
                if (Math.abs(sun[i]) < 0x10) {
                    allSun.add((byte) 0x02);
                    allSun.add((byte) (sun[i] ^ 0x10));
                } else {
                    allSun.add(sun[i]);
                }
            }
        }
        return ListToArray(allSun);
    }


    /**
     * 将list集合转换为数组
     */
    private static byte[] ListToArray(List<Byte> list) {
        byte[] suns = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            suns[i] = list.get(i);
        }
        return suns;
    }

}
