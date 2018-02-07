package rk.device.server.api;

import android.text.TextUtils;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rk.device.launcher.utils.LogUtil;

/**
 * Created by hanbin on 2018/2/5.
 * <p/>
 * App本地服务器
 */

public class LauncherHttpServer {
    private static final String       TAG                 = "LauncherHttpServer";

    private static LauncherHttpServer mInstance;

    public static int                 PORT_LISTEN_DEFAULT = 5000;

    AsyncHttpServer                   server              = new AsyncHttpServer();

    public static LauncherHttpServer getInstance() {
        if (mInstance == null) {
            // 增加类锁,保证只初始化一次
            synchronized (LauncherHttpServer.class) {
                if (mInstance == null) {
                    mInstance = new LauncherHttpServer();
                }
            }
        }
        return mInstance;
    }

    /**
     * 开启本地服务
     */
    public void startServer(HttpServerReqCallBack reqCallBack) {
        server.get("[\\d\\D]*", reqCallBack);
        server.post("[\\d\\D]*", reqCallBack);
        server.listen(PORT_LISTEN_DEFAULT);
    }

    /**
     * Http request's callback
     */
    public abstract static class HttpServerReqCallBack implements HttpServerRequestCallback {
        public abstract void onError(String uri, Multimap params, AsyncHttpServerResponse response);

        public abstract void onSuccess(String uri, Multimap params,
                                       AsyncHttpServerResponse response);

        @Override
        public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
            String uri = request.getPath();
            //获取header
            Multimap headers = request.getHeaders().getMultiMap();
            if (!TextUtils.isEmpty(uri)) {//针对的是接口的处理
                //获取post请求的参数的地方
                if (headers != null) {
                    LogUtil.d(TAG, headers.toString());
                }
                if (uri.equals(HttpRequestUri.UPLOAD)) {
                    MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
                    body.setMultipartCallback(new MultipartFormDataBody.MultipartCallback() {
                        @Override
                        public void onPart(Part part) {
                            if (part.isFile()) {
                                LogUtil.d(TAG, "onPart = ");
                                body.setDataCallback(new DataCallback() {
                                    @Override
                                    public void onDataAvailable(DataEmitter emitter,
                                                                ByteBufferList bb) {
                                        BufferedInputStream fileInputStream = null;
                                        BufferedOutputStream fileOutPutStream = null;
                                        byte[] buff = new byte[1024];
                                        try {
                                            fileInputStream = new BufferedInputStream(
                                                    new ByteArrayInputStream(bb.getAllByteArray()));

                                            fileOutPutStream = new BufferedOutputStream(
                                                    new FileOutputStream(new File(
                                                            "/data/rk_backup/app_cache/scene.png")));
                                            int bytesRead = 0;
                                            while (-1 != (bytesRead = fileInputStream.read(buff, 0,
                                                    buff.length))) {
                                                fileOutPutStream.write(buff, 0, bytesRead);
                                            }
                                            fileOutPutStream.flush();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            try {
                                                fileInputStream.close();
                                                fileOutPutStream.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                });
                                //                            response.send("onPart");
                            } else {
                                response.send("onPart else");
                                //                            Multimap params = part.
                                //                            if (params != null) {
                                //                                LogUtil.d(TAG, "params = " + params.toString());
                                //                            }
                                LogUtil.d(TAG, "onPart =1 ");
                            }
                        }
                    });
                    body.setEndCallback(new CompletedCallback() {
                        @Override
                        public void onCompleted(Exception ex) {
                            response.send("onCompleted");
                        }
                    });
                } else {
                    Multimap params = (Multimap) request.getBody().get();
                    onSuccess(uri, params, response);
                }
            } else {
                response.send("Invalid request url.");
            }
        }
    }

}
