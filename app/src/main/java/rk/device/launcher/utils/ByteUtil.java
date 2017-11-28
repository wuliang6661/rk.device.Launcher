package rk.device.launcher.utils;

import java.util.Arrays;


/**
 * @author xuyiming
 * @version v1.0
 * @see ClassName：ByteUtil
 * @see Function：处理byte的工具类
 * @see Date：2015-06-02 14:57:27
 * @since JDK 1.7
 */
public class ByteUtil {

    /**
     * 合并两个byte[]
     *
     * @param byte_1
     * @param byte_2
     * @return 合并后的byte数组，byte_1在前，byte_2在后
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 返回高位补0的长度为i的字节数组
     *
     * @param bytes
     * @param i
     * @return
     */
    public static byte[] addlen(byte[] bytes, int i) {
        byte[] byte_3 = new byte[i - bytes.length];
        return byteMerger(byte_3, bytes);
    }

    /**
     * 字符串前补0到str的长度为i
     *
     * @param str
     * @param i
     * @return
     */
    public static String addlen(String str, int i) {
        if (str.length() < i) ;
        i = i - str.length();
        for (int a = 0; a < i; a++) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1)
                + (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 1) & 0x1)
                + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 将int数值转换为占N个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
     *
     * @param value 要转换的值
     * @return byte数组
     */
    public static byte[] int2BytesLittleEndian(int value, Integer byteLength) {
        byte[] byte_src = new byte[byteLength];

        for (Integer i = 0; i < byteLength; i++) {
            byte_src[i] = (byte) ((value & (0xFF << 8 * i)) >> 8 * i);
        }
        return byte_src;
    }

    public static byte[] int2BytesLittleEndian(int value) {
        byte[] byte_src = new byte[4];

        for (Integer i = 0; i < 4; i++) {
            byte_src[i] = (byte) ((value & (0xFF << 8 * i)) >> 8 * i);
        }
        return byte_src;
    }

    /**
     * @param @param  value
     * @param @return
     * @return byte[]
     * @throws @author omen www.liyidong.com
     * @Title: int2Bytes
     * @Description: bigEndian 高位在前，低位在后
     * @date 2016年4月12日 下午3:27:49
     */
    public static byte[] int2Bytes(int value, int bLength) {
        byte[] src = new byte[bLength];
        for (int j = 0; bLength > 0; bLength--) {
            int i = bLength - 1;
            src[i] = (byte) ((value >> (j * 8)) & 0xFF);
            j++;
        }
        return src;
    }

    /**
     * @param @param  value
     * @param @return
     * @return byte[]
     * @throws @author omen www.liyidong.com
     * @Title: int2Bytes
     * @Description: bigEndian 高位在前，低位在后
     * @date 2016年4月12日 下午3:27:49
     */
    public static byte[] int2Bytes(int value) {
        return int2Bytes(value, 4);
    }

    /**
     * 将byte数组转换为long数值，本方法适用于(高位在前，低位在后)的顺序。无符号long
     *
     * @param byte_src 要转换的byte数组
     * @return int值
     */
    public static long bytes2UnsignedLong(byte[] byte_src) {
        Integer num = 0;

        for (Integer i = 0; i < byte_src.length; i++) {
            if (byte_src[i] < 0)
                num = num + ((256 + byte_src[i]) << 8 * (byte_src.length - 1 - i));
            else
                num = num + (byte_src[i] << 8 * (byte_src.length - 1 - i));
        }
        return num & 0x0FFFFFFFFl;
    }

    /**
     * 将byte数组转换为int数值，本方法适用于(高位在前，低位在后)的顺序。
     *
     * @param byte_src 要转换的byte数组
     * @return int值
     */
    public static Integer bytes2Int(byte[] byte_src) {
        Integer num = 0;

        for (Integer i = 0; i < byte_src.length; i++) {
            if (byte_src[i] < 0)
                num = num + ((256 + byte_src[i]) << 8 * (byte_src.length - 1 - i));
            else
                num = num + (byte_src[i] << 8 * (byte_src.length - 1 - i));
        }
        return num;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 将byte数组转换为int数值，本方法适用于(高位在前，低位在后)的顺序。
     *
     * @param src 要转换的byte数组
     * @return int值
     */
    public static Integer bytesToInt2(byte[] src) {
        Integer num = 0;

        for (Integer i = 0; i < src.length; i++) {
            if (src[i] < 0)
                num = num + ((256 + src[i]) << 8 * (src.length - 1 - i));
            else
                num = num + (src[i] << 8 * (src.length - 1 - i));
        }
        return num;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt3(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8));
        return value;
    }

    /**
     * 将short转成byte[2]
     *
     * @param a
     * @param b
     * @param offset b中的偏移量
     */
    public static void short2Bytes(short a, byte[] b, int offset) {
        b[offset] = (byte) (a >> 8);
        b[offset + 1] = (byte) (a);
    }

    /**
     * 将short转成byte[2]
     *
     * @param a
     * @param b
     * @param offset b中的偏移量
     */
    public static byte[] short2Bytes(short a) {
        byte[] b = new byte[2];
        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);
        return b;
    }

    /**
     * 将byte[2]转换成short
     *
     * @param b
     * @return
     */
    public static short bytes2Short(byte[] b) {
        return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
    }

    public static long bytes2long(byte[] b) {
        long temp = 0;
        long res = 0;
        for (int i = 0; i < b.length; i++) {
            res <<= 8;
            temp = b[i] & 0xff;
            res |= temp;
        }
        return res;
    }

    public static byte[] long2bytes(long num) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (num >>> (56 - (i * 8)));
        }
        return b;
    }

    public static void inverted(byte[] bytes) {
        for (int i = 0; i < (bytes.length) / 2; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
    }

    public static void main(String[] args) {
        /*Integer i = 0x6001;
		byte[] bts = intToBytes(i, 2);
		System.out.println(bts[0] + "," + bts[1]);*/
		
		/*byte[] bt = new byte[5];
		byte[] bts = intToBytes(51001, 5);
		bt[0] = bts[4];
		bt[1] = bts[3];
		bt[2] = bts[2];
		bt[3] = bts[1];
		bt[4] = bts[0];
		System.out.println(bytesToInt(bt));*/

        long timeStamp = System.currentTimeMillis();
        System.out.println("time stamp=" + timeStamp);
        byte[] timeB = long2bytes(timeStamp);
        System.out.println(StringUtils.parseByte2HexStr(timeB));
        System.out.println(bytes2long(timeB));

        byte[] a = {1, 3, 5, 2, 4, 9};
        inverted(a);
        System.out.println(Arrays.toString(a));

        Short as = 12;
        byte[] bs = short2Bytes(as);
        System.out.println(Arrays.toString(bs));

        int i = 53333;
        System.out.println(StringUtils.parseByte2HexStr(int2Bytes(i, 2)));
    }
}
