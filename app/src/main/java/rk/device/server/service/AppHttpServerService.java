package rk.device.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import rk.device.launcher.bean.event.DestoryEvent;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.SPUtils;
import rk.device.server.api.HttpRequestUri;
import rk.device.server.api.LauncherHttpServer;
import rk.device.server.logic.DeviceLogic;
import rk.device.server.logic.PersonLogic;
import rk.device.server.logic.PublicLogic;
import rk.device.server.logic.VoucherLogic;

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


    Thread lanucherThread = new Thread() {
        @Override
        public void run() {
            super.run();
            launcherServer.startServer(new LauncherHttpServer.HttpServerReqCallBack() {
                @Override
                public void onError(String uri, Multimap params, AsyncHttpServerResponse response) {

                }

                @Override
                public void onSuccess(String uri, JSONObject params, AsyncHttpServerResponse response) {
                    if (!uri.equals(HttpRequestUri.GET_TOKEN)) {
                        String token;
                        String localToken = SPUtils.getString(Constant.GRANT_TOKEN);
                        token = params.optString("access_token");
                        if (!token.equals(localToken)) {
                            response.send(PublicLogic.getInstance().returnError("token失效").toJSONString());
                            return;
                        }
                        long tokenTime = SPUtils.getLong(Constant.GRANT_TIME);
                        //token有效期24小时
                        if (System.currentTimeMillis() - tokenTime > 86400000) {
                            response.send(PublicLogic.getInstance().returnError("token过期").toJSONString());
                            return;
                        }
                    }
                    try {
                        switch (uri) {
                            case HttpRequestUri.GET_TOKEN:
                                response.send(PublicLogic.getInstance().getToken().toJSONString());
                                break;
                            case HttpRequestUri.MEMBER_ADD:
                                response.send(PersonLogic.getInstance().addMember(params).toJSONString());
                                break;
                            case HttpRequestUri.MEMBER_UPDATE:
                                response.send(PersonLogic.getInstance().updatePerson(params).toJSONString());
                                break;
                            case HttpRequestUri.MEMBER_DELETE:
                                response.send(PersonLogic.getInstance().deletePerson(params).toJSONString());
                                break;
                            case HttpRequestUri.FACE_ADD:
                                response.send(VoucherLogic.getInstance().addPersonFace(params).toJSONString());
                                break;
                            case HttpRequestUri.FACE_UPDATE:
                                response.send(VoucherLogic.getInstance().updatePersonFace(params).toJSONString());
                                break;
                            case HttpRequestUri.FACE_DELETE:
                                response.send(VoucherLogic.getInstance().deleteFaceImg(params).toJSONString());
                                break;
                            case HttpRequestUri.CARD_ADD:
                                response.send(VoucherLogic.getInstance().addCard(params).toJSONString());
                                break;
                            case HttpRequestUri.CARD_UPDATE:
                                response.send(VoucherLogic.getInstance().updateCards(params).toJSONString());
                                break;
                            case HttpRequestUri.CARD_DELETE:
                                response.send(VoucherLogic.getInstance().deleteCards(params).toJSONString());
                                break;
                            case HttpRequestUri.PASSWORD_ADD:
                                response.send(VoucherLogic.getInstance().addPassWord(params).toJSONString());
                                break;
                            case HttpRequestUri.PASSWORD_UPDATE:
                                response.send(VoucherLogic.getInstance().updatePassWord(params).toJSONString());
                                break;
                            case HttpRequestUri.PASSWORD_DELETE:
                                response.send(VoucherLogic.getInstance().deletePassWord(params).toJSONString());
                                break;
                            case HttpRequestUri.FINGER_DELETE:
                                response.send(VoucherLogic.getInstance().deleteFinger(params).toJSONString());
                                break;
                            case HttpRequestUri.OPEN:
                                response.send(DeviceLogic.getInstance().open(params).toJSONString());
                                break;
                            case HttpRequestUri.DEVICE_STATUS:
                                response.send(DeviceLogic.getInstance().status(params).toJSONString());
                                break;
                            case HttpRequestUri.UPDATE:
                                response.send(DeviceLogic.getInstance().update(params).toJSONString());
                                break;
                            case HttpRequestUri.ADD_GUANGGAO:
                                response.send(DeviceLogic.getInstance().add_guangao(params).toJSONString());
                                break;
                            case HttpRequestUri.UPDATATE_GUANGGAO:
                                response.send(DeviceLogic.getInstance().update_guangao(params).toJSONString());
                                break;
                            case HttpRequestUri.DELETE_GUANGGAO:
                                response.send(DeviceLogic.getInstance().delete_guangao(params).toJSONString());
                                break;
                            case HttpRequestUri.UPDATE_TIME:
                                response.send(DeviceLogic.getInstance().updateTime(params).toJSONString());
                                break;
                            default:
                                response.send("Invalid request url.");
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        response.send("json message error.");
                    }
                }

                @Override
                public void onFile(MultipartFormDataBody body, AsyncHttpServerResponse response) {
                }
            });
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
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
