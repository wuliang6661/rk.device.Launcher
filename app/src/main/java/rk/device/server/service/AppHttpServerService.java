package rk.device.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

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
        init();
    }

    private void init() {
        if (launcherServer == null) {
            launcherServer = LauncherHttpServer.getInstance();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread launcherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                launcherServer.startServer(new LauncherHttpServer.HttpServerReqCallBack() {
                    @Override
                    public void onError(String uri, Multimap params,
                                        AsyncHttpServerResponse response) {

                    }

                    @Override
                    public void onSuccess(String uri, Multimap params,
                                          AsyncHttpServerResponse response) {
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
                    }

                    @Override
                    public void onFile(MultipartFormDataBody body,
                                       AsyncHttpServerResponse response) {
                        MemberLogic.getInstance().upload(body, response);
                    }
                });
            }
        });
        launcherThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
