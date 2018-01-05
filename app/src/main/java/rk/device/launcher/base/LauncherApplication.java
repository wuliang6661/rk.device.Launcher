package rk.device.launcher.base;

import android.app.Application;
import android.content.Context;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import rk.device.launcher.R;
import rk.device.launcher.utils.STUtils;
import rk.device.launcher.utils.Utils;
import rk.device.launcher.utils.verify.FaceUtils;

/**
 * Created by wuliang on 2017/11/11 下午3:49
 * <p>
 * 程序全局监听
 */

public class LauncherApplication extends Application {

    public static Context sContext;

    /**
     * 记录电量数据上报
     */
    public static int     sLevel;

    /**
     * 记录是否在充电
     */
    public static int     sIsCharge;

    /**
     * 记录是否在录入指纹
     */
    public static int     sIsFingerAdd;

    /**
     * 记录是否在录入NFC卡
     */
    public static int     sIsNFCAdd;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.setDefaultErrorActivityDrawable(R.mipmap.ic_launcher);
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

}
