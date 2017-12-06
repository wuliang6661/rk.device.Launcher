package rk.device.launcher.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.CloseUtils;
import rk.device.launcher.utils.PackageUtils;

public class DownLoadIntentService extends IntentService {
	private static final String ACTION_DOWNLOAD = "rk.device.launcher.service.action.DOWNLOAD";
	
	private static final String EXTRA_URL = "rk.device.launcher.service.extra.url";
	
	// 升级的是rom还是apk
	private static final String EXTRA_TYPE = "rk.device.launcher.service.extra.type";
	
	private static final int DOWNLOAD_APK_OVER = 2;
	
	private static final int DOWNLOAD_ROM_OVER = 3;
	private String mApkPath;
	
	
	public DownLoadIntentService() {
		super("DownLoadIntentService");
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DOWNLOAD_APK_OVER:
					installApk();
					break;
				case DOWNLOAD_ROM_OVER:
					break;
			}
		}
	};
	
	public static void startDownLoad(Context context, String url, String type) {
		Intent intent = new Intent(context, DownLoadIntentService.class);
		intent.setAction(ACTION_DOWNLOAD);
		intent.putExtra(EXTRA_URL, url);
		intent.putExtra(EXTRA_TYPE, type);
		context.startService(intent);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_DOWNLOAD.equals(action)) {
				final String url = intent.getStringExtra(EXTRA_URL);
				String type = intent.getStringExtra(EXTRA_TYPE);
				if (TextUtils.equals(Constant.KEY_ROM, type)) {
					handleActionDownLoadRom(url);
				} else {
					handleActionDownLoadAPK(url);
				}
			}
		}
	}
	
	private void handleActionDownLoadAPK(String url) {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) { // sd卡已经挂载
			String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/upload/Update/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			mApkPath = savePath + getAppInfo() + ".apk";
			File apkFile = new File(mApkPath);
			// 是否已下载更新文件
			if (apkFile.exists()) {
				installApk();
				return;
			}
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
			        .url(url)
			        .build();
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
				}
				
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (!response.isSuccessful()) {
						Log.d(TAG, "response is not success");
						return;
					}
					InputStream is = null;
					byte[] buffer = new byte[8 * 1024];
					int len = 0;
					FileOutputStream fos = null;
					try {
						long total = response.body().contentLength();
						Log.e(TAG, "total------>" + total);
						long current = 0;
						is = response.body().byteStream();
						fos = new FileOutputStream(apkFile);
						while ((len = is.read(buffer)) != -1) {
							current += len;
							fos.write(buffer, 0, len);
							Log.e(TAG, "current------>" + current);
						}
						fos.flush();
						Log.d(TAG, "下载完成！！！");
						mHandler.sendEmptyMessage(DOWNLOAD_APK_OVER);
					} catch (Exception e) {
						e.printStackTrace();
						Log.d(TAG, "下载失败");
					} finally {
						CloseUtils.closeIOQuietly(is);
						CloseUtils.closeIOQuietly(fos);
					}
				}
			});
			
		}
	}
	
	/**
	 * 安装apk
	 */
	private void installApk() {
		File apkfile = new File(mApkPath);
		if (!apkfile.exists()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
			Uri apkUri =
			FileProvider.getUriForFile(this, PackageUtils.getPageageName() + ".fileprovider", apkfile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			// 由于没有在Activity环境下启动Activity,设置下面的标签
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
			startActivity(intent);
		} else {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
			"application/vnd.android.package-archive");
			startActivity(i);
		}
	}
	
	private String getAppInfo() {
		try {
			String pkName = getPackageName();
			String versionName = getPackageManager().getPackageInfo(
			pkName, 0).versionName;
			int versionCode = getPackageManager().getPackageInfo(
			pkName, 0).versionCode;
			return pkName + "_" + versionName + "_" + versionCode;
		} catch (Exception e) {
		}
		return null;
	}
	
	private final String TAG = "DownLoadIntentService";
	
	
	private void handleActionDownLoadRom(String url) {
		String fileDir = "/data/rk_backup";
		File dir = new File(fileDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		final File downLoadApk = new File(dir, "update.img");
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
		        .url(url)
		        .build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					Log.d(TAG, "response is not success");
					return;
				}
				InputStream is = null;
				byte[] buffer = new byte[8 * 1024];
				int len = 0;
				FileOutputStream fos = null;
				try {
					long total = response.body().contentLength();
					Log.e(TAG, "total------>" + total);
					long current = 0;
					is = response.body().byteStream();
					fos = new FileOutputStream(downLoadApk);
					while ((len = is.read(buffer)) != -1) {
						current += len;
						fos.write(buffer, 0, len);
						Log.e(TAG, "current------>" + current);
					}
					fos.flush();
					Log.d(TAG, "下载完成！！！");
					mHandler.sendEmptyMessage(DOWNLOAD_ROM_OVER);
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, "下载失败");
				} finally {
					CloseUtils.closeIOQuietly(is);
					CloseUtils.closeIOQuietly(fos);
				}
			}
		});
	}
	
}