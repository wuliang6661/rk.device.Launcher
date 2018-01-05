package rk.device.launcher.ui.qrcode;


import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class QrcodeActivity extends MVPBaseActivity<QrcodeContract.View, QrcodePresenter> implements QrcodeContract.View {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.sv)
    SurfaceView mSurfaceView;
    @Bind(R.id.frame_layout)
    FrameLayout mFrameLayout;
    @Bind(R.id.tv_scan_result)
    TextView mTvScanResult;

    @Override
    protected int getLayout() {
        return R.layout.activity_qr_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goBack();
        setTitle("扫描二维码");
        mPresenter.initCamera();
    }


    @Override
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }
}
