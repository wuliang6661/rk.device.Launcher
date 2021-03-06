package rk.device.launcher.utils.carema.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Cache;
import rk.device.launcher.utils.CloseUtils;
import rk.device.launcher.utils.CommonUtils;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "PlayCamera";

    /**
     * sdcard路径
     *
     * @return
     */
    private static String initPath() {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdir();
            }
        }
        return storagePath;
    }

    /**
     * 保存Bitmap到sdcard
     *
     * @param b
     */
    public static void saveBitmap(Bitmap b) {

        String path = initPath();
        long dataTake = System.currentTimeMillis();
        String jpegName = path + "/" + dataTake + ".jpg";
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.i(TAG, "saveBitmap success");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "saveBitmap error");
            e.printStackTrace();
        }

    }
	
	public static Cache getCache() {
		File cacheFile = new File(CommonUtils.getContext().getCacheDir(), "rkcache");
		Cache cache = new Cache(cacheFile, 1024 * 1024 * 10);// 设置缓存大小为10M
		return cache;
	}
	
	/**
	 * 加密或者解密用的都是同一个函数
	 * @param sourceFile
	 * @param encryptedFile
	 */
	public static boolean encryptFile(File sourceFile, File encryptedFile) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(sourceFile));
			bos = new BufferedOutputStream(new FileOutputStream(encryptedFile));
			int len;
			byte[] buffer = new byte[1024 * 8];
			// attention len是bis从文件中实际读到的字节数组的长度, 最后一次的时候是-1
			// 倒数第二次的时候字节数组的长度是不足1024 * 8的
			while ((len = bis.read(buffer)) != -1) {
//                System.out.println("len = " + len);
//                System.out.println("buffer.length = " + buffer.length);
				for (int i = 0; i < len; i++) {
					buffer[i] ^= 123;
				}
				bos.write(buffer, 0, len);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseUtils.closeIO(bis, bos);
		}
		return false;
	}


}
