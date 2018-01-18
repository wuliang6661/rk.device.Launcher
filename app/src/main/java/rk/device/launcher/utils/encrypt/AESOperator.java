package rk.device.launcher.utils.encrypt;


import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mundane on 2017/12/28.
 */
public class AESOperator {
    private static final String KEY = "0123456789abcdef"; // key，可自行修改
    private static final String IV_PARAMETER = "1020304050607080"; // 偏移量,可自行修改, 必须是16位


    private static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String encode(String source) {
        String encryptStr = encrypt(source);
        String concatStr = getRandomString(6).concat(encryptStr);
        return Base64Utils.encode(concatStr.getBytes());
    }

    public static String decode(String data) {
        byte[] decodeBytes = Base64Utils.decode(data);
        String concatStr = new String(decodeBytes);
        String substring = concatStr.substring(6, concatStr.length());
        return decrypt(substring);
    }

    // 加密
    private static String encrypt(String source) {

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = KEY.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encryptedBytes = cipher.doFinal(source.getBytes("utf-8"));
            return Base64Utils.encode(encryptedBytes);// 此处使用BASE64做转码。
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解密
    private static String decrypt(String encryptedString) {
        try {
            byte[] raw = KEY.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64Utils.decode(encryptedString);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }
}
