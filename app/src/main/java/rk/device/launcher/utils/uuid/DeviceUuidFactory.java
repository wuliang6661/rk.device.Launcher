package rk.device.launcher.utils.uuid;
/**
 * Created by Arron zhou
 */

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import rk.device.launcher.utils.CloseUtils;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.cache.CacheUtils;


public class DeviceUuidFactory {
    protected static final String PREFS_FILE = "dusundev_id.xml";
    protected static final String DEVICE_UUID_FILE_NAME = ".dusundev_id.txt";
    protected static final String PREFS_DEVICE_ID = "dev_id";
    protected static final String KEY = "dusun'2017";
    protected static UUID uuid;

    public DeviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
//                    final SharedPreferences sp = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
                    String id = SPUtils.getString(PREFS_DEVICE_ID);
                    if (!StringUtils.isEmpty(id)) {
                        uuid = UUID.fromString(id);
                    } else {
                        if (recoverDeviceUuidFromSD() != null) {
                            uuid = UUID.fromString(recoverDeviceUuidFromSD());
                        } else {
                            String macAddress = FileUtils.readFile2String("/proc/board_sn", "UTF-8");
                            // todo fixme
                            if (TextUtils.isEmpty(macAddress)) {
                                macAddress = "00:00:00:00:00:00";
                            }
                            try {
                                if (!"9774d56d682e549c".equals(macAddress)) {
                                    uuid = UUID.nameUUIDFromBytes(macAddress.getBytes("UTF-8"));
                                    try {
                                        saveDeviceUuidToSD(EncryptUtils.encryptDES(uuid.toString(), KEY));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                                    final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getM();
                                    String deviceId = FileUtils.readFile2String("/proc/board_sn", "UTF-8");
                                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("UTF-8")) : UUID.randomUUID();
                                    try {
                                        saveDeviceUuidToSD(EncryptUtils.encryptDES(uuid.toString(), KEY));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        SPUtils.putString(PREFS_DEVICE_ID, uuid.toString());
                    }
                }
            }
        }
    }

    private static String recoverDeviceUuidFromSD() {
        try {
            String sdcardPath = CacheUtils.getBaseCache();
            File sdCardFolder = new File(sdcardPath);
            File uuidFile = new File(sdCardFolder, DEVICE_UUID_FILE_NAME);
            if (!sdCardFolder.exists() || !uuidFile.exists()) {
                return null;
            }
            FileReader fileReader = new FileReader(uuidFile);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[100];
            int readCount;
            while ((readCount = fileReader.read(buffer)) > 0) {
                sb.append(buffer, 0, readCount);
            }
            //通过UUID.fromString来检查uuid的格式正确性
            UUID uuid = UUID.fromString(EncryptUtils.decryptDES(sb.toString(), KEY));
            return uuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveDeviceUuidToSD(String uuid) {
        String dirPath = CacheUtils.getBaseCache();
        File targetFile = new File(dirPath, DEVICE_UUID_FILE_NAME);
        if (!targetFile.exists()) {
            OutputStreamWriter osw = null;
            try {
                osw = new OutputStreamWriter(new FileOutputStream(targetFile), "utf-8");
                try {
                    osw.write(uuid);
                    osw.flush();
                    osw.close();
                    FileUtils.setPermission(targetFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(osw);
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }
}
