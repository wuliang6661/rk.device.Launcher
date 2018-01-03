package rk.device.launcher.ui.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cvc.EventUtil;
import rk.device.launcher.api.ApiService;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.base.JniHandler;
import rk.device.launcher.bean.AddressBO;
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.VerifyBO;
import rk.device.launcher.bean.WeatherBO;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.BasePresenterImpl;
import rk.device.launcher.service.ElectricBroadcastReceiver;
import rk.device.launcher.service.NetBroadcastReceiver;
import rk.device.launcher.service.NetChangeBroadcastReceiver;
import rk.device.launcher.utils.AppUtils;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.TimeUtils;
import rk.device.launcher.utils.gps.GpsUtils;
import rk.device.launcher.utils.oss.AliYunOssUtils;
import rk.device.launcher.utils.oss.OssUploadListener;
import rk.device.launcher.utils.uuid.DeviceUuidFactory;
import rk.device.launcher.utils.verify.FaceUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {


    private GpsUtils gpsUtils;

    /**
     * UUID工具
     */
    private DeviceUuidFactory uuidFactory = null;
    private String uUid;

    private ElectricBroadcastReceiver mBatteryReceiver;
    private NetChangeBroadcastReceiver netChangeBroadcastRecever;
    private NetBroadcastReceiver netOffReceiver;


    /**
     * 初始化jni
     */
    @Override
    public JniHandler initJni() {
        JniHandler mHandler = JniHandler.getInstance();
        Message msg = new Message();
        msg.what = EventUtil.INIT_JNI;
        mHandler.sendMessageDelayed(msg, 10);
        return mHandler;
    }

    /**
     * 注册电量监听
     */
    @Override
    public ElectricBroadcastReceiver registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        mBatteryReceiver = new ElectricBroadcastReceiver();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mView.getContext().registerReceiver(mBatteryReceiver, intentFilter);
        return mBatteryReceiver;
    }


    /**
     * 注册网络变化监听
     */
    public NetChangeBroadcastReceiver registerNetReceiver() {
        netChangeBroadcastRecever = new NetChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // "android.net.wifi.SCAN_RESULTS"
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // "android.net.conn.CONNECTIVITY_CHANGE"
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // "android.net.wifi.STATE_CHANGE"
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // "android.net.wifi.WIFI_STATE_CHANGED"
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.setPriority(1000); // 设置优先级，最高为1000
        mView.getContext().registerReceiver(netChangeBroadcastRecever, intentFilter);
        return netChangeBroadcastRecever;
    }


    /**
     * 注册网络断开监听
     */
    public void registerNetOffReceiver() {
        netOffReceiver = new NetBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // "android.net.wifi.SCAN_RESULTS"
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // "android.net.conn.CONNECTIVITY_CHANGE"
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // "android.net.wifi.STATE_CHANGE"
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mView.getContext().registerReceiver(netOffReceiver, intentFilter);
    }


    /**
     * 注销各类服务
     */
    void unRegisterReceiver() {
        mView.getContext().unregisterReceiver(mBatteryReceiver);
        mView.getContext().unregisterReceiver(netChangeBroadcastRecever);
        mView.getContext().unregisterReceiver(netOffReceiver);
    }


    /**
     * 获取地理位置
     */
    void initLocation(BaseActivity activity) {
        if (gpsUtils == null) {
            gpsUtils = new GpsUtils(mView.getContext());
        }
        if (gpsUtils.isLoactionAvailable()) { // 定位可用, 通过定位获取地址
            gpsUtils.initLocation(address -> {
                if (address.size() > 0) {
                    String area = address.get(0).getSubAdminArea();
                    SPUtils.putString(Constant.KEY_ADDRESS, area);
                    httpGetWeather(area);
                }
            });
        } else { // 定位不可用, 通过IP获取地址
            getIPLocation(activity);
        }
    }

    /**
     * 定位不可用，通过IP获取地址
     */
    private void getIPLocation(BaseActivity activity) {
        ApiService.address("js").subscribeOn(Schedulers.io())
                .flatMap(s -> {
                    int start = s.indexOf("{");
                    int end = s.indexOf("}");
                    String json = s.substring(start, end + 1);
                    AddressBO addressModel = JSON.parseObject(json, AddressBO.class);
                    Map<String, Object> params = new HashMap<>();
                    params.put("city", addressModel.city);
                    Observable<List<WeatherBO>> observable;
                    try {
                        observable = ApiService.weather(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("error throw ip");
                    }
                    return observable;
                }).observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Subscriber<List<WeatherBO>>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
//                                   isIpError = true;
                               }

                               @Override
                               public void onNext(List<WeatherBO> weatherModel) {
//                                   isIpError = false;
                                   mView.showWeather(weatherModel);
                               }
                           }
                );
    }


    /**
     * 天气Api
     */
    private void httpGetWeather(String area) {
        Map<String, Object> params = new HashMap<>();
        params.put("city", area);
        ApiService.weather(params).subscribe(new Subscriber<List<WeatherBO>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                T.showShort(e.getMessage());
            }

            @Override
            public void onNext(List<WeatherBO> weatherModel) {
                mView.showWeather(weatherModel);
            }
        });
    }


    /**
     * 获取配置接口
     */
    void getData() {
        ApiService.deviceConfiguration(AppUtils.getAppVersionCode(mView.getContext()) + "", null).subscribe(new Subscriber<DeviceInfoBO>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(DeviceInfoBO s) {
                mView.setAnimationIp(s.getMobile());
            }
        });
    }


    private int faceSuress = 0;   //活体检测通过次数，每5次请求一下人脸识别

    /**
     * 人脸数据上传到阿里云进行识别
     */
    void httpUploadPic(byte[] result) {
        faceSuress++;
        if (faceSuress % 2 != 0) {
            return;
        }
        faceSuress = 0;
        AliYunOssUtils.getInstance(mView.getContext()).putObjectFromByteArray(result, new OssUploadListener() {
            @Override
            public void onSuccess(String filePath) {
                Log.d("wuliang", "end aliFace " + TimeUtils.getTime());
                //自定义人脸识别post数据
                if (uuidFactory == null) {
                    uuidFactory = new DeviceUuidFactory(mView.getContext());
                }
                uUid = uuidFactory.getUuid() + "";
                httpFaceVerifyPath(filePath, uUid);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException,
                                  ServiceException serviceException) {
                Log.i("oss-upload-fail", clientException.getMessage());
                Log.i("oss-upload-fail", serviceException.getErrorCode() + ":" + serviceException.getRawMessage());
            }
        });
    }

    /**
     * 判断返回数据是否成功，成功则开门
     */
    private void httpFaceVerifyPath(String filePath, String uuid) {
        String spDevice = SPUtils.getString(Constant.DEVICE_TYPE);
        String myType = "1";   //默认是1
        if (!StringUtils.isEmpty(spDevice)) {
            String[] device = spDevice.split("_");
            myType = device[0];
        }
        Map<String, Object> params = new HashMap<>();
        params.put("image_url", filePath);
        params.put("uuid", uuid);
        params.put("type", myType);
        ApiService.verifyFace(params).subscribe(new Subscriber<VerifyBO>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(VerifyBO model) {
                if (!model.isIsrepeat()) {
                    if (model.isIsmatch()) {
//                        SoundPlayUtils.play(3);//播放声音
                        Log.i("wuliang", "face scusess!!!!");
                        mView.showSuress(model.getName());
                    } else {
                        Log.i("wuliang", "face no  no   no!!!!");
                    }
                }
            }
        });
    }

    /**
     * 注册两张人脸照片进入人脸库（测试）
     */
    void registerFace() {
//        Bitmap aa = BitmapFactory.decodeFile("/data/rk_backup/11.jpg");
//        Bitmap bb = BitmapFactory.decodeFile("/data/rk_backup/22.jpg");
        FaceUtils faceUtils = FaceUtils.getInstance();
//        AFR_FSDKFace aa_face = faceUtils.bitmapToFace(aa);
//        AFR_FSDKFace bb_face = faceUtils.bitmapToFace(bb);
//
//        faceUtils.saveFace("一鸣", aa_face);
//        faceUtils.saveFace("吴亮", bb_face);
        faceUtils.startFaceFR();
        faceUtils.setFaceFeature((name, max_score) -> mView.showSuress(name));
    }
}