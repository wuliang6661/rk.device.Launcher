package rk.device.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.api.T;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.carema.utils.FileUtil;

public class UsbBroadCastReceiver extends BroadcastReceiver {
	
	public interface OnDecryptedListener{
		void onDecryptedFinished(List<File> fileList);
	}
	
	public void setOnDecryptedListener(OnDecryptedListener listener) {
		mOnDecryptedListener = listener;
	}
	
	private OnDecryptedListener mOnDecryptedListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// 当sd卡插上的时候
		if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
			!Environment.isExternalStorageRemovable()) {
				File sdCardDir = Environment.getExternalStorageDirectory();
				File roombankerDir = new File(sdCardDir, "roombanker");
				List<File> encryptedFileList = FileUtils.listFilesInDirWithFilter(roombankerDir, ".ao", true);
				if (encryptedFileList == null || encryptedFileList.isEmpty()) {
					return;
				}
				List<File> picFileList = new ArrayList<>();
				for (File encryptedFile : encryptedFileList) {
					// 获取全路径中的文件名
					String fileName = FileUtils.getFileName(encryptedFile);
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
				// 复制完毕
				T.showShort("复制完毕");
				if (mOnDecryptedListener != null && !picFileList.isEmpty()) {
					mOnDecryptedListener.onDecryptedFinished(picFileList);
				}
			}
		}
		
	}
}
