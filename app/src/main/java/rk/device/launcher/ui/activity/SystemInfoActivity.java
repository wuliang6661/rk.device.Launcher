package rk.device.launcher.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.utils.AppUtils;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.QRCodeUtils;
import rk.device.launcher.utils.ScreenUtil;
import rk.device.launcher.widget.UpdateManager;


/**
 *
 */

public class SystemInfoActivity extends BaseCompatActivity implements View.OnClickListener {


    @Bind(R.id.iv_qrcode)
    ImageView mIvQrcode;
    @Bind(R.id.btn_check_update)
    Button mBtnCheckUpdate;
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
    protected void inviView() {
        goBack();
        setTitle("关于设备");
        invition();
    }

    @Override
    protected void inviData() {
        mBtnCheckUpdate.setBackgroundResource(R.drawable.shape_btn_round_corner);

        mIvQrcode.post(() -> {
            Bitmap qrCodeBitmap = QRCodeUtils.createQRCode("mundane", mIvQrcode.getWidth(), mIvQrcode.getHeight());
            mIvQrcode.setImageBitmap(qrCodeBitmap);
        });
        mBtnCheckUpdate.setOnClickListener(this);
    }

    /***
     * 初始化界面
     */
    private void invition() {
        versionName.setText(String.valueOf("V" + AppUtils.getAppVersionName(this)));
        versionCode.setText(String.valueOf((double) AppUtils.getAppVersionCode(this)));
        deviceCpu.setText(Build.MODEL);
        Point point = ScreenUtil.getSizeNew(this);
        deviceResolution.setText(point.x + "*" + point.y);

    }


    @Override
    public void onClick(View view) {
        UpdateManager.getUpdateManager().checkAppUpdate(this, getSupportFragmentManager(), false);
    }
}
