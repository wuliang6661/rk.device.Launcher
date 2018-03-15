package rk.device.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.guo.android_extend.java.AbsLoop;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.JSONArrayBody;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import rk.device.launcher.bean.event.DestoryEvent;
import rk.device.launcher.utils.LogUtil;
import rk.device.server.api.HttpRequestUri;
import rk.device.server.api.LauncherHttpServer;
import rk.device.server.logic.DeviceLogic;
import rk.device.server.logic.MemberLogic;
import rk.device.server.logic.PublicLogic;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * App本地服务
 */
public class AppHttpServerService extends Service {

    private LauncherHttpServer launcherServer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {
        if (launcherServer == null) {
            launcherServer = LauncherHttpServer.getInstance();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lanucherThread.start();
        return super.onStartCommand(intent, flags, startId);
    }


    AbsLoop lanucherThread = new AbsLoop() {
        @Override
        public void setup() {
            launcherServer.startServer(new LauncherHttpServer.HttpServerReqCallBack() {
                @Override
                public void onError(String uri, Multimap params,
                                    AsyncHttpServerResponse response) {

                }

                @Override
                public void onSuccess(String uri, JSONObject params,
                                      AsyncHttpServerResponse response) {
                    try {
                        switch (uri) {
                            case HttpRequestUri.MEMBER_ADD:
                                response.send(
                                        MemberLogic.getInstance().addMember(params).toJSONString());
                                break;
                            case HttpRequestUri.DELETE:
                                response.send(
                                        MemberLogic.getInstance().delete(params).toJSONString());
                                break;
                            case HttpRequestUri.OPEN:
                                response.send(
                                        DeviceLogic.getInstance().open(params).toJSONString());
                                break;
                            case HttpRequestUri.DEVICE_STATUS:
                                response.send(
                                        DeviceLogic.getInstance().status(params).toJSONString());
                                break;
                            case HttpRequestUri.UPDATE:
                                response.send(
                                        PublicLogic.getInstance().update(params).toJSONString());
                                break;
                            case HttpRequestUri.AD:
                                response.send(PublicLogic.getInstance().ad(params).toJSONString());
                                break;
                            case HttpRequestUri.DELETE_FACE:
                                response.send(MemberLogic.getInstance().deleteFace(params)
                                        .toJSONString());
                                break;
                            case HttpRequestUri.UPDATE_TIME:
                                response.send(PublicLogic.getInstance().updateTime(params).toJSONString());
                                break;
                            default:
                                response.send("Invalid request url.");
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFile(MultipartFormDataBody body,
                                   AsyncHttpServerResponse response) {
                    MemberLogic.getInstance().upload(body, response);
                }
            });
        }

        @Override
        public void loop() {
        }

        @Override
        public void over() {

        }
    };

    @Override
    public void onDestroy() {
        LogUtil.e("AppHttpServerSerivice onDestory");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DestoryEvent messageEvent) {
        launcherServer.stopServer();
        lanucherThread.shutdown();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
