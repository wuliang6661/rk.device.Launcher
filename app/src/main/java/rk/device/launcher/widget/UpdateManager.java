package rk.device.launcher.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import rk.device.launcher.R;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.bean.VersionBean;
import rk.device.launcher.utils.AppUtils;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.widget.dialog.BaseDialogFragment;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 应用程序更新工具包
 *
 * @version 1.0
 */
public class UpdateManager {

    private static final String TAG = "UpdateManager";

    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL = 1;

    private static UpdateManager updateManager;

    private CompositeSubscription mCompositeSubscription;

    private Context mContext;
    private WeakReference<FragmentManager> mWeakReference = null;
    // private FragmentManager mFragmentManager;
    // 通知对话框
    private Dialog noticeDialog;
    // 下载对话框
    private Dialog downloadDialog;
    // '已经是最新' 或者 '无法获取最新版本' 的对话框
    private Dialog latestOrFailDialog;
    // 进度条
    private ProgressBar mProgress;
    // 显示下载数值
    private TextView mProgressText;
    // 查询动画
    private ProgressDialog mProDialog;
    // 进度值
    private int progress;
    // 下载线程
    private Thread downLoadThread;
    // 终止标记
    private boolean interceptFlag;
    // 提示语
    private String updateMsg = "";
    // 返回的安装包url
    private String apkUrl = "";
    // 下载包保存路径
    private String savePath = "";
    // apk保存完整路径
    private String apkFilePath = "";
    // 临时下载文件路径
    private String tmpFilePath = "";
    // 下载文件大小
    private String apkFileSize;
    // 已下载文件大小
    private String tmpFileSize;

    private int curVersionCode;

    private VersionBean mUpdate;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mProgressText.setText(tmpFileSize + "/" + apkFileSize);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    installApk();
                    break;
                case DOWN_NOSDCARD:
                    downloadDialog.dismiss();
                    Toast.makeText(mContext, "无法下载安装文件，请检查SD卡是否挂载", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        ;
    };

    public static UpdateManager getUpdateManager() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
        updateManager.interceptFlag = false;
        return updateManager;
    }

    /**
     * 检查App更新
     *
     * @param context
     * @param isShowMsg 是否显示提示消息
     */
    public void checkAppUpdate(Context context,
                               FragmentManager fragmentManager, final boolean isShowMsg) {
        this.mContext = context;
        mWeakReference = new WeakReference<FragmentManager>(fragmentManager);
        curVersionCode = PackageUtils.getCurrentVersionCode();
        if (isShowMsg) {
            if (mProDialog == null) {
                mProDialog = ProgressDialog.show(mContext, null, "正在检测，请稍后...",
                        true, true);
                Window window = mProDialog.getWindow();
                window.setGravity(Gravity.CENTER);
            } else if (mProDialog.isShowing()
                    || (latestOrFailDialog != null && latestOrFailDialog
                    .isShowing()))
                return;
        }
        mCompositeSubscription = new CompositeSubscription();
        ApiService.updateApp(AppUtils.getAppVersionCode(mContext) + "").subscribe(new Subscriber<VersionBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(VersionBean s) {
                // 进度条对话框不显示 - 检测结果也不显示
                if (mProDialog != null && !mProDialog.isShowing()) {
                    return;
                }
//						// 关闭并释放释放进度条对话框
                if (isShowMsg && mProDialog != null) {
                    mProDialog.dismiss();
                    mProDialog = null;
                }
                mUpdate = s;
                apkUrl = mUpdate.getFile();
                updateMsg = mUpdate.getNote();
                if (AppUtils.getAppVersionCode(mContext) < s.getCode()) {
                    showNoticeDialog(true);
                } else {
                    if (isShowMsg) {
                        showLatestOrFailDialog(DIALOG_TYPE_LATEST);
                    }
                }
            }
        });
    }

    /**
     * 显示'已经是最新'或者'无法获取版本信息'对话框
     */
    private void showLatestOrFailDialog(int dialogType) {
        if (latestOrFailDialog != null) {
            // 关闭并释放之前的对话框
            latestOrFailDialog.dismiss();
            latestOrFailDialog = null;
        }
        Builder builder = new Builder(mContext);
        builder.setTitle("系统提示");
        if (dialogType == DIALOG_TYPE_LATEST) {
            builder.setMessage("您当前已经是最新版本");
        } else if (dialogType == DIALOG_TYPE_FAIL) {
            builder.setMessage("无法获取版本更新信息");
        }
        builder.setPositiveButton(mContext.getString(R.string.confirm), null);
        latestOrFailDialog = builder.create();
        Window window = latestOrFailDialog.getWindow();
        latestOrFailDialog.show();
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 显示'已经是最新'或者'无法获取版本信息'对话框
     */
    private void showLatestOrFailDialog2(final int dialogType) {
        @SuppressLint("ValidFragment")
        BaseDialogFragment fasd = new BaseDialogFragment() {
            @Override
            protected Builder build(Builder builder) {
                builder.setTitle("系统提示");
                if (dialogType == DIALOG_TYPE_LATEST) {
                    builder.setMessage("您当前已经是最新版本");
                } else if (dialogType == DIALOG_TYPE_FAIL) {
                    builder.setMessage("无法获取版本更新信息");
                }
                builder.setPositiveButton(mContext.getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                return builder;
            }
        };
        fasd.setCancelable(false);
        if (mWeakReference.get() != null) {
            fasd.show(mWeakReference.get(), "");
        }
    }


    /**
     * 显示版本更新通知对话框
     */
    private void showNoticeDialog(final boolean canDelay) {
        Log.d(TAG, "show notice dialog");
        Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setCancelable(false);
        builder.setPositiveButton("立即更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog(canDelay);
            }
        });
        if (canDelay) {
            builder.setNegativeButton("以后再说", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        noticeDialog = builder.create();
        Window window = noticeDialog.getWindow();
        noticeDialog.show();
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadDialog(final boolean canDelay) {
        Builder builder = new Builder(mContext);
        builder.setTitle("正在下载新版本");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.tools_update_layout, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        mProgressText = (TextView) v.findViewById(R.id.update_progress_text);
        builder.setView(v);
        if (canDelay) {
            builder.setNegativeButton(mContext.getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    interceptFlag = true;
                }
            });
        }
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        Window window = downloadDialog.getWindow();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        window.setGravity(Gravity.CENTER);
        downloadDialog.setCancelable(false);
        downloadApk();
    }

    private String getAppInfo() {
        try {
            String pkName = mContext.getPackageName();
            String versionName = mContext.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(
                    pkName, 0).versionCode;
            return pkName + "_" + versionName + "_" + versionCode;
        } catch (Exception e) {
        }
        return null;
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mUpdate == null) {
                return;
            }
            try {
//                String apkName = getAppInfo() + ".apk";
//                String tmpApk = getAppInfo() + ".tmp";
                String apkName = "rk_launcher.apk";
                String tmpApk = "rk_launcher.tmp";
                // 判断是否挂载了SD卡
//                String storageState = Environment.getExternalStorageState();
//                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
//                    savePath = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/upload/Update/";
//                    File file = new File(savePath);
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//                    apkFilePath = savePath + apkName;
//                    tmpFilePath = savePath + tmpApk;
//                }

//                // 没有挂载SD卡，无法下载文件
//                if (apkFilePath == null || apkFilePath == "") {
//                    mHandler.sendEmptyMessage(DOWN_NOSDCARD);
//                    return;
//                }
                apkFilePath = getDirPath("") + apkName;
                tmpFilePath = getDirPath("") + tmpApk;

                File ApkFile = new File(apkFilePath);

                // 是否已下载更新文件
//                if (ApkFile.exists()) {
//                    downloadDialog.dismiss();
//                    installApk();
//                    return;
//                }

                // 输出临时下载文件
                File tmpFile = new File(tmpFilePath);
                FileOutputStream fos = new FileOutputStream(tmpFile);

                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                // 显示文件大小格式：2个小数点显示
                DecimalFormat df = new DecimalFormat("0.00");
                // 进度条下面显示的总文件大小
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    // 进度条下面显示的当前下载文件大小
                    tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    // 当前进度值
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成 - 将临时下载文件转成APK文件
                        if (tmpFile.renameTo(ApkFile)) {
                            // 通知安装
                            mHandler.sendEmptyMessage(DOWN_OVER);
                        }
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    /**
     * 检测本机是否存在Sd卡，没有则将apk下载至内部存储路径
     */
    private String getDirPath(String dir) {
        String directoryPath = "";
        //判断SD卡是否可用
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            directoryPath = mContext.getExternalFilesDir(dir).getAbsolutePath();
            // directoryPath =context.getExternalCacheDir().getAbsolutePath() ;
        } else {
            //没内存卡就存机身内存
//            directoryPath = mContext.getFilesDir() + File.separator + dir;
//            directoryPath = mContext.getCacheDir() + File.separator + dir;
            directoryPath = "/data/rk_backup/" + dir;
        }
        File file = new File(directoryPath);
        if (!file.exists()) {//判断文件目录是否存在
            file.mkdirs();
        }
        Log.i(TAG, "filePath====>" + directoryPath);
        return directoryPath;
    }


    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(mContext, PackageUtils.getPageageName() + ".fileprovider", apkfile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                    "application/vnd.android.package-archive");
            mContext.startActivity(i);
        }
    }

}
