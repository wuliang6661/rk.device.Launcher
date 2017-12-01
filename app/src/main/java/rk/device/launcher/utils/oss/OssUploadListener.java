package rk.device.launcher.utils.oss;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

/**
 * Created by tech60 on 2017/7/13.
 */

public interface OssUploadListener {
    void onSuccess(String filePath);

    void onFailure(PutObjectRequest request, ClientException clientException,
                   ServiceException serviceException);
}
