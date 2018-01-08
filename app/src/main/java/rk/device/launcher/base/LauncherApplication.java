package rk.device.launcher.base;

import android.app.Application;
import android.content.Context;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import rk.device.launcher.R;
import rk.device.launcher.utils.STUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaBack;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;

/**
 * Created by wuliang on 2017/11/11 下午3:49
 * <p>
 * 程序全局监听
 */

public class LauncherApplication extends Application implements CustomActivityOnCrash.EventListener {

    public static Context sContext;

    /**
     * 记录电量数据上报
     */
    public static int sLevel;

    /**
     * 记录是否在充电
     */
    public static int sIsCharge;

    /**
     * 记录是否在录入指纹
     */
    public static int sIsFingerAdd;

    /**
     * 记录是否在录入NFC卡
     */
    public static int sIsNFCAdd;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.setDefaultErrorActivityDrawable(R.mipmap.ic_launcher);
        CustomActivityOnCrash.setEventListener(this);
        Utils.init(this);
        STUtils.init(this);
        FaceUtils.getInstance().init(this);
        FaceUtils.getInstance().loadFaces();
    }

    /**
     * 获取上下文
     *
     * @return Context
     */
    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onLaunchErrorActivity() {
        SurfaceHolderCaremaFont.stopCarema();
        SurfaceHolderCaremaBack.stopCarema();
        FaceUtils.getInstance().stopFaceFR();
//        stopService(new Intent(this, SocketService.class));
//        stopService(new Intent(this, VerifyService.class));
    }

    @Override
    public void onRestartAppFromErrorActivity() {

    }

    @Override
    public void onCloseAppFromErrorActivity() {

    }
}
