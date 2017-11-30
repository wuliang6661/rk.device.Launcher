package com.dusun.facerecog.util;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

/**
 * Created by tech60 on 2017/7/13.
 */

public interface OssUploadListener {
    void onSuccess(int position,String filePath);

    void onFailure(PutObjectRequest request, ClientException clientExcepion,
                   ServiceException serviceException);
}
