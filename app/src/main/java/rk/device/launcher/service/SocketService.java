package rk.device.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import rk.device.launcher.api.BaseApiImpl;
import rk.device.launcher.base.LauncherApplication;
import rk.device.launcher.bean.StatusBo;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.CloseUtils;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.PackageUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.WifiHelper;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rx.Subscriber;

/**
 * Created by hanbin on 2017/9/23.
 */

public class SocketService extends Service {

    private static final String  TAG               = "SocketService";
    /**
     * socket变量
     */
    private Socket               socket            = null;
    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService      mThreadPool;
    //输出流
    private BufferedWriter       writer            = null;
    /**
     * 接收服务器消息 变量
     */
    BufferedReader               reader            = null;
    private DeviceUuidFactory    uuidFactory;
    private String               uuid;
    private static SocketService mService          = null;
    private DeviceUuidFactory    deviceUuidFactory = null;

    public static SocketService getInstance() {
        if (mService == null) {
            mService = new SocketService();
        }
        return mService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (deviceUuidFactory == null) {
            deviceUuidFactory = new DeviceUuidFactory(LauncherApplication.getContext());
        }
        //        init();
    }

    private void init() {
        mService = this;
        //初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        uuidFactory = new DeviceUuidFactory(this);
        uuid = uuidFactory.getUuid() + "";
        Log.i("SocketService", "mac:" + FileUtils.readFile2String("/proc/board_sn", "UTF-8"));
        Log.i("SocketService", "uuid:" + uuid);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, TAG + " onStartCommand");
        //        openService();
        sendDeviceStatusToPlatform();
        return super.onStartCommand(intent, flags, startId);
    }

    Timer         timer   = new Timer();

    TimerTask     task    = new TimerTask() {

                              public void run() {
                                  Message message = new Message();
                                  message.what = 1;
                                  handler.sendMessage(message);
                              }

                          };

    final Handler handler = new Handler() {

                              public void handleMessage(Message msg) {

                                  switch (msg.what) {
                                      case 1:
                                          LogUtil.i(TAG,TAG+" uploadDeviceStatus");
                                          JSONObject params = new JSONObject();
                                          try {
                                              params.put("uuid", deviceUuidFactory.getUuid());
                                              params.put("mac", FileUtils
                                                      .readFile2String("/proc/board_sn", "UTF-8"));
                                              params.put("hw_ver", Build.HARDWARE);
                                              params.put("version_code",
                                                      PackageUtils.getCurrentVersionCode());
                                              params.put("version_name",
                                                      PackageUtils.getCurrentVersion());
                                              params.put("imsi", PackageUtils
                                                      .getImsi(LauncherApplication.getContext()));
                                              params.put("msisdn", PackageUtils
                                                      .getMsisdn(LauncherApplication.getContext()));
                                              params.put("battery", LauncherApplication.sLevel);
                                              params.put("temperature",
                                                      LauncherApplication.sTemperature);
                                              params.put("signal", WifiHelper.obtainWifiInfo(
                                                      LauncherApplication.getContext()));
                                              params.put("card_capacity", 9999);
                                              params.put("whitelist_count", 9999);
                                              params.put("finger_capacity",
                                                      LauncherApplication.totalUserCount);
                                              params.put("finger_count",
                                                      LauncherApplication.remainUserCount);
                                              params.put("opened", 0);
                                              params.put("work_mode", 9999);
                                              params.put("power_mode", LauncherApplication.sIsCharge);
                                          } catch (JSONException e) {

                                          }
                                          BaseApiImpl.uploadDeviceStatus(params).subscribe(new Subscriber<StatusBo>() {
                                              @Override
                                              public void onCompleted() {

                                              }

                                              @Override
                                              public void onError(Throwable e) {

                                              }

                                              @Override
                                              public void onNext(StatusBo statusBo) {

                                              }
                                          });
                                          break;
                                  }
                                  super.handleMessage(msg);
                              }
                          };

    private void sendDeviceStatusToPlatform() {
        timer.schedule(task, 1000, 50000);
    }

    public void closeThreadPool() {
        if (mThreadPool != null) {
            CloseUtils.closeIOQuietly(socket);
            mThreadPool.shutdownNow();
            if (mThreadPool != null) {
                int threadCount = ((ThreadPoolExecutor) mThreadPool).getActiveCount();
                Log.i(TAG, "threadCount:" + threadCount);
            }
            mThreadPool = null;
        }
    }

    /**
     * 判断当前是否连接
     *
     * @return
     */
    public boolean checkConnected() {
        if (socket == null || socket.isClosed()) {
            return false;
        }
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    /**
     * 开启服务
     */
    public void openService() {
        if (mThreadPool == null) {
            init();
        }
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (socket == null || socket.isClosed()) {
                    CloseUtils.closeIOQuietly(socket);
                    socket = new Socket();
                }
                InetSocketAddress address = new InetSocketAddress("121.41.123.16", 3004);
                try {
                    socket.connect(address, 5000);
                    if (socket.isConnected()) {
                        openOutputStream();
                    }
                } catch (IOException e) {
                    LogUtil.e(TAG, e.getMessage());
                    //                    closeThreadPool();
                    //                    openService();
                }
            }
        });
    }

    /**
     * 开启
     */
    private void openOutputStream() {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                Log.i(TAG, TAG + " is connect:" + socket.isConnected());
                if (socket.isClosed()) {
                    socket.close();
                    socket = null;
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("121.41.123.16", 3004), 5000);
                    Log.i(TAG, TAG + " is reconnect");
                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                sendSms();
                Thread.sleep(20000);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            closeThreadPool();
            openService();
        }
    }

    /**
     * 发送信息
     */
    private void sendSms() {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("isCharge", LauncherApplication.sIsCharge);
        jsonObject.put("mac", FileUtils.readFile2String("/proc/board_sn", "UTF-8"));
        jsonObject.put("uuid", uuid);
        jsonObject.put("version", PackageUtils.getCurrentVersion());
        jsonObject.put("battery", LauncherApplication.sLevel);
        jsonObject.put("cid", PushManager.getInstance().getClientid(this));
        jsonObject.put("device_name", SPUtils.getString(Constant.DEVICE_NAME));
        jsonObject.put("address", SPUtils.getString(Constant.KEY_ADDRESS));
        // 步骤2：写入需要发送的数据到输出流对象中
        try {
            writer.write(jsonObject.toString() + "\n");
            writer.flush();
            Log.i(TAG, TAG + " send data success:" + jsonObject.toString());
        } catch (IOException e) {
            LogUtil.e(TAG, e.getMessage());
            closeThreadPool();
            openService();
        }
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (socket.isConnected() && !socket.isClosed()) {
                        StringBuffer responseInfo = new StringBuffer();
                        while (reader.ready()) {
                            responseInfo.append((char) reader.read());
                        }
                        Log.i(TAG, TAG + " server responseInfo:" + responseInfo.toString());
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: SocketService");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        //        try {
        //            CloseUtils.closeIOQuietly(writer, reader, socket);
        //            Log.i("socket isConnected", "socket isConnected:" + socket.isConnected());
        //        } catch (Exception e) {
        //            LogUtil.e(TAG, e.getMessage());
        //        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
