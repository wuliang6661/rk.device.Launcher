package rk.device.launcher.tools;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rk.device.launcher.utils.ByteUtil;

/**
 * Created by wuliang on 2017/11/7.
 * <p>
 * 进行手机与蓝牙锁之间的信息体转化
 */

public class BlueToothUtils {

    //    private static byte[] content0 = new byte[]{0x01, -86, 0x02, 0x16, 0x02, 0x11, 0x02, 0x10};
//    private static byte[] content1 = new byte[]{0x21, 0x7f, 0x02, 0x1a, 0x11, 0x22, 0x33, 0x44};
//    private static byte[] content2 = new byte[]{0x11, 0x22, 0x33, 0x44, 0x11, 0x22, 0x33, 0x44};
//    private static byte[] content3 = new byte[]{0x5a, 0x02, 0x12, 0x57, 0x10, 0x5b, -29, -118};
    public static byte[] content4 = new byte[]{0x01, -86, 0x02, 0x16, 0x02, 0x11, 0x02, 0x10, 0x21, 0x7f, 0x02, 0x1a, 0x11, 0x22, 0x33, 0x44, 0x11,
            0x22, 0x33, 0x44, 0x11, 0x22, 0x33, 0x44, 0x5a, 0x02, 0x12, 0x57, 0x10, 0x5b, -29, -118, -112, 0x03};

    //   0x01   : 头
    //    -86   ： adress
    //    0x02, 0x16 :  type
    //   0x02, 0x11   // cmd  type
    //  0x02, 0x10, 0x21   //len
    //   0x7f     //CRC
    // 0x02, 0x1a    type = 10
    //    0x11, 0x22, 0x33, 0x44   //  id
    //  0x11,0x22, 0x33, 0x44    //数据1
    //0x11, 0x22, 0x33, 0x44    //数据2
    //0x5a, 0x02, 0x12, 0x57, 0x10    起始时间
    //  0x5b, -29, -118, -112         截止时间
    // 0x03      //结束


    private static byte[] content0 = new byte[]{0x01, -86, 0x02, 0x16, 0x02, 0x11, 0x02, 0x10, 0x21, 0x7f, 0x02, 0x1a, 0x11, 0x22, 0x33, 0x44, 0x11};
    private static byte[] content1 = new byte[]{0x22, 0x33, 0x44, 0x11, 0x22, 0x33, 0x44, 0x5a, 0x02, 0x12, 0x57, 0x10, 0x5b, -29, -118, -112, 0x03};


//[1,-86,6,1,0,33,49,26,0,0,0,1,0,0,0,1,0,0,0,1,90,29,31,60,90,29,31,65,3]
//        [1,-86,2,22,2,17,2,16,33,49,26,2,16,2,16,2,16,2,17,2,16,2,16,2,16,2,17,2,16,2,16,2,16,2,17,90,29,31,60,90,29,31,65,3]


    /**
     * 开启蓝牙锁
     */
    public static byte[] openLock(int time) {
        byte[] start = new byte[]{0x01, -86, 0x06, 0x01, 0x00, 0x21};
        byte[] type = new byte[]{0x0A};      //除了第一个0x0A为设置为一键开门，后面数据没卵用
        byte[] id = ByteUtil.int2Bytes(11223344);    //密码序列
        byte[] val1 = ByteUtil.int2Bytes(11223344);    //数据1
        byte[] val2 = ByteUtil.int2Bytes(11223344);    //数据2
        byte[] time_start = ByteUtil.int2Bytes(time);    //密码生效时间
        byte[] time_stop = ByteUtil.int2Bytes(time + 5);    //密码失效时间
        byte[] openData;
        openData = ByteUtil.byteMerger(type, id);
        openData = ByteUtil.byteMerger(openData, val1);
        openData = ByteUtil.byteMerger(openData, val2);
        openData = ByteUtil.byteMerger(openData, time_start);
        openData = ByteUtil.byteMerger(openData, time_stop);
        byte[] crc = new byte[]{CrcToByte(start, openData)};
        byte[] end = new byte[]{0x03};
        byte[] sun;   //数据集合合并之后的总值
        sun = ByteUtil.byteMerger(start, crc);
        sun = ByteUtil.byteMerger(sun, openData);
        sun = ByteUtil.byteMerger(sun, end);

        Log.e("wuliang1", Arrays.toString(sun));
        Log.e("wuliang2", Arrays.toString(transBytes(sun)));
        return transBytes(sun);
    }


    public static int getInt(byte[] arr, int index) {
        return (0xff000000 & (arr[index + 0] << 24)) |
                (0x00ff0000 & (arr[index + 1] << 16)) |
                (0x0000ff00 & (arr[index + 2] << 8)) |
                (0x000000ff & arr[index + 3]);

    }


    public static void main(String[] args) {
        byte[] aas = new byte[]{0x11, 0x22, 0x33, 0x44};
        System.out.println(getInt(aas, 0) + "");

    }


    /**
     * 获取修改时间的byte数组
     * <p>
     * time : 单位秒
     */
    public static byte[] getBlueTimeByte(int time) {
        byte[] start = new byte[]{0x01, (byte) 0xAA, 0x06, 0x00, 0x00, 0x04};
        byte[] times = ByteUtil.int2Bytes(time);    //默认占四个字节
//        byte aa = CrcTobyte(-86, 0x06, 0x00, new byte[]{0x00, 0x21}, times);
        byte[] crc = new byte[]{CrcToByte(start, times)};    //Crc校验之后的值
        byte[] end = new byte[]{0x03};
        byte[] sun;   //数据集合合并之后的总值
        sun = ByteUtil.byteMerger(start, crc);
        sun = ByteUtil.byteMerger(sun, times);
        sun = ByteUtil.byteMerger(sun, end);
        return transBytes(sun);
    }


    /**
     * 进行CRC校验
     * <p>
     * start : 数据前的命令拼接
     * data : 需要传递的数据
     */

    private static byte CrcToByte(byte[] start, byte[] data) {
        byte[] sun = ByteUtil.byteMerger(start, data);
        return CRC8.calcCrc8(sun, 1);
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


    //01 aa 0216 0211 02102197021a1122334411223344112233445A02112FC05A0211CA7003
    public static byte[] openLockl(int i) {
        byte[] content = null;
        switch (i) {
            case 0:
                content = content0;
                break;
            case 1:
                content = content1;
                break;
//            case 2:
//                content = content2;
//                break;
//            case 3:
//                content = content3;
//                break;
//            case 4:
//                content = content4;
//                break;
        }
        return content;
    }

    // char转byte

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    private static char XOR(int temp) {
        if (temp < 0x10) {
            temp ^= 0x10;
            temp = 0x02 + temp;
        }
        return (char) temp;
    }
}
