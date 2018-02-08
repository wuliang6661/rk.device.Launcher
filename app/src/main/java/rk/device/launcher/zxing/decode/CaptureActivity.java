package rk.device.launcher.zxing.decode;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.greenrobot.greendao.query.Query;

import java.util.Collection;
import java.util.Date;

import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.QrCodeBO;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.db.entity.UserDao;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.utils.DateUtil;
import rk.device.launcher.utils.LogUtil;
import rk.device.launcher.utils.ResUtil;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.encrypt.AESOperator;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.utils.verify.OpenUtils;
import rk.device.launcher.zxing.camera.CameraManager;
import rk.device.launcher.zxing.view.ViewfinderView;


public class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = "CaptureActivity";


    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;
    private Camera.Parameters parameters;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvWarning;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_capture;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_capture);
        initView();
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvWarning = (TextView) findViewById(R.id.tv_warning);
        mTvTitle.setText(ResUtil.getString(R.string.qr_code_saomiao));
        mIvBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderView);
        viewfinderView.setCameraManager(cameraManager);
        handler = null;

        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        decodeFormats = null;
        characterSet = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        long t1 = System.currentTimeMillis();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        long t2 = System.currentTimeMillis();
        Log.d(TAG, "onPause: costTime = " + (t2 - t1));
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
//            openCamera(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
            }
        } catch (Exception ioe) {
            Log.w(TAG, ioe);
        }

    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            String resultString = rawResult.getText();
            LogUtil.d(TAG, "resultString = " + resultString);
//            // Then not from history, so beep/vibrate and we have an image to draw on
//            beepManager.playBeepSoundAndVibrate();
//            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            Log.d(TAG, "result = " + resultString);
//            bundle.putString("result", resultString);
//            intent.putExtras(bundle);
//            this.setResult(RESULT_OK, intent);
            parseResult(resultString);
        } else {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        }
//        CaptureActivity.this.finish();

    }

    private void parseResult(String resultString) {
        try {
//            String decryptedString = RSAUtils.decrypt(resultString);
            String decryptedString = AESOperator.decode(resultString);
            LogUtil.d(TAG, "decryptedString = " + decryptedString);
            QrCodeBO qrCodeBO = JSON.parseObject(decryptedString, QrCodeBO.class);
            long endTime = qrCodeBO.endTime;
            long startTime = qrCodeBO.startTime;
            String peopleId = qrCodeBO.peopleId;
            Date endDate = TimeUtils.formatTimeStamp(endTime);
//            T.showShort("获取到结束时间");
            Date currentDate = DateUtil.getCurrentTime();
            String qrUuids = qrCodeBO.uuid;
            String[] qrUuidArr = qrUuids.split(",");
            DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(this);
            String deviceUuid = deviceUuidFactory.getUuid().toString();
            boolean isThisDevice = false;
            for (String qrUuid : qrUuidArr) {
                if (TextUtils.equals(qrUuid, deviceUuid)) {
                    isThisDevice = true;
                    break;
                }
            }
            // 授权时间已过期
            if (currentDate.after(endDate)) {
                showWarning(getString(R.string.auth_timeout));
            } else if (!isThisDevice){
                showWarning(getString(R.string.auth_no));
            } else {
                UserDao userDao = DbHelper.getUserDao();
                Query<User> query = userDao.queryBuilder()
                        .where(UserDao.Properties.UniqueId.eq(peopleId))
                        .build();
                User user = query.unique();
                if (user == null) {
                    showWarning(getString(R.string.auth_lianxi));
                    return;
                }
                LogUtil.d(TAG, "二维码解析成功, 正在发送请求");
                // 调用开门接口, 假如成功, 执行开门逻辑, 显示文字：验证成功；1.5s后跳转首页
                OpenUtils.getInstance().open(VerifyTypeConstant.TYPE_QR_CODE, peopleId, user.getName());
            }

        } catch (Exception e) {
            showWarning(getString(R.string.qrcode_error));
            e.printStackTrace();
        }
    }

    private void showWarning(String message) {
        // 发送信息, 重新扫描
        handler.reScan();
        mTvWarning.setVisibility(View.VISIBLE);
        mTvWarning.setText(message);
        mTvWarning.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvWarning.setText("");
                mTvWarning.setVisibility(View.GONE);
            }
        }, 3000);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
