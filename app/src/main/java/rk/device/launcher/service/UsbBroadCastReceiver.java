package rk.device.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.bean.event.SleepImageEvent;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.carema.utils.FileUtil;

import static android.content.Context.STORAGE_SERVICE;


public class UsbBroadCastReceiver extends BroadcastReceiver {


    // 获取次存储卡路径,一般就是外置 TF 卡了. 不过也有可能是 USB OTG 设备...
    // 其实只要判断第二章卡在挂载状态,就可以用了.
    public String getSecondaryStoragePath(Context context) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            Class<StorageManager> clazz = StorageManager.class;
            Method getVolumePathsMethod = clazz.getMethod("getVolumePaths", new Class<?>[]{});
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, new Object[]{});
            // second element in paths[] is secondary storage path
            return paths.length <= 2 ? null : paths[1];
        } catch (Exception e) {
            Log.e(TAG, "getSecondaryStoragePath() failed", e);
        }
        return null;
    }

    private final String TAG = "UsbBroadCastReceiver";

    private String getFileName(File file) {
        String name = file.getName();
        String[] nameArr = name.split("\\.");
        return nameArr.length >= 2 ? nameArr[0] : null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "收到sd卡插入的广播");
        // 当sd卡插上的时候
        if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
            // 外置tf卡的路径
            String sdCardDirPath = "/mnt/external_sd";
//			String sdCardDirPath = getSecondaryStoragePath(context);
            if (TextUtils.isEmpty(sdCardDirPath)) {
                return;
            }
            File sdcardDir = new File(sdCardDirPath);
            if (sdcardDir.exists()) {
                File roombankerDir = new File(sdCardDirPath, "roombanker");
                List<File> encryptedFileList = FileUtils.listFilesInDirWithFilter(roombankerDir, ".ao", true);
                if (encryptedFileList == null || encryptedFileList.isEmpty()) {
                    return;
                }
                List<File> picFileList = new ArrayList<>();
                for (File encryptedFile : encryptedFileList) {
                    // 获取全路径中的文件名(不要.ao的后缀)
                    String fileName = getFileName(encryptedFile);
                    String destDirPath = "/data/rk_backup/rk_ad";
                    File destDir = new File(destDirPath);
                    if (FileUtils.createOrExistsDir(destDir)) {
                        File decryptedFile = new File(destDir, fileName + ".jpeg");
                        // 成功复制
                        if (FileUtil.encryptFile(encryptedFile, decryptedFile)) {
                            picFileList.add(decryptedFile);
                        }
                    }
                }
                // 复制完毕 修改call分支
                LogUtil.d(TAG, "复制完毕");
                Toast.makeText(context, "复制完毕", Toast.LENGTH_LONG).show();
                if (!picFileList.isEmpty()) {
                    RxBus.getDefault().post(new SleepImageEvent(picFileList));
                }
            }
        }
    }
}
