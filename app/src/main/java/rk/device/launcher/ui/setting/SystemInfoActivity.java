package rk.device.launcher.ui.setting;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.utils.AppUtils;
import rk.device.launcher.utils.QRCodeUtils;
import rk.device.launcher.utils.ScreenUtil;


/**
 * 关于设备页面
 */

public class SystemInfoActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.iv_qrcode)
    ImageView mIvQrcode;
    //    @Bind(R.id.btn_check_update)
//    Button mBtnCheckUpdate;
    @Bind(R.id.version_name)
    TextView versionName;
    @Bind(R.id.version_code)
    TextView versionCode;
    @Bind(R.id.device_cpu)
    TextView deviceCpu;
    @Bind(R.id.device_resolution)
    TextView deviceResolution;


    @Override
    protected int getLayout() {
        return R.layout.activity_system_info;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    protected void initView() {
        goBack();
        setTitle(getString(R.string.guanyu_devices));
        invition();
    }

    protected void initData() {
//        mBtnCheckUpdate.setBackgroundResource(R.drawable.shape_btn_round_corner);

        mIvQrcode.post(() -> {
            Bitmap qrCodeBitmap = QRCodeUtils.createQRCode("http://mj.roombanker.cn", mIvQrcode.getWidth(), mIvQrcode.getHeight());
            mIvQrcode.setImageBitmap(qrCodeBitmap);
        });
//        mBtnCheckUpdate.setOnClickListener(this);
    }


    /***
     * 初始化界面
     */
    private void invition() {
        versionName.setText(String.valueOf("V" + AppUtils.getAppVersionName(this)));
        versionCode.setText(Build.VERSION.RELEASE);
        deviceCpu.setText(Build.MODEL);
        Point point = ScreenUtil.getSizeNew(this);
        deviceResolution.setText(point.x + "*" + point.y);
    }


    @Override
    public void onClick(View view) {

    }
}
