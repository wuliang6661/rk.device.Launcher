package rk.device.launcher.utils.oss;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import rk.device.launcher.Config;

/**
 * Created by tech60 on 2017/7/13.
 */

public class AliYunOssUtils {
    private static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    private static String testBucket = "rkfaceclouds";
    private static String testObject = "rkface/";
    private static Context mContext;
    private OSS mOss;
    private static AliYunOssUtils mAliYunOss = null;
    private String filePath = "";

    public AliYunOssUtils(Context context) {
        mContext = context;

        // 在移动端建议使用STS方式初始化OSSClient。
        OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
                // 如果因为某种原因加签失败，描述error信息后，返回nil
                // 以下是用本地算法进行的演示
                return OSSUtils.sign(Config.ALIYUNOSS_APP_KEY, Config.ALIYUNOSS_APP_SECKET,
                        content);
            }
        };
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

        OSS oss = new OSSClient(mContext.getApplicationContext(), endpoint, credentialProvider,
                conf);
        this.mOss = oss;
    }

    public static AliYunOssUtils getInstance(Context context) {
        if (mAliYunOss == null) {
            synchronized (AliYunOssUtils.class) {
                if (mAliYunOss == null) {
                    mAliYunOss = new AliYunOssUtils(context);
                }
            }
        }
        return mAliYunOss;
    }

    /**
     * 直接上传二进制数据，使用阻塞的同步接口
     *
     * @param uploadData
     * @param mListener
     */
    public void putObjectFromByteArray(byte[] uploadData, OssUploadListener mListener) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String fileName = str + "/" + System.currentTimeMillis() + getRandomString(6) + ".jpg";
        filePath = "http://" + testBucket + ".oss-cn-hangzhou.aliyuncs.com/" + fileName;
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(testBucket, fileName, uploadData);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = mOss.asyncPutObject(put,
                new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        mListener.onSuccess(filePath);
                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion,
                                          ServiceException serviceException) {
                        mListener.onFailure(request, clientExcepion, serviceException);
                        // 请求异常
                        if (clientExcepion != null) {
                            // 本地异常如网络异常等
                            clientExcepion.printStackTrace();
                        }
                        if (serviceException != null) {
                            // 服务异常
                            serviceException.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 随机数函数
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
