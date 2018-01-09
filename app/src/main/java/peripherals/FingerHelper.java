package peripherals;

import rk.device.launcher.bean.event.FingerRegisterProgressEvent;
import rk.device.launcher.utils.rxjava.RxBus;

public class FingerHelper {

    /**
     * A native method that is implemented by the 'native-lib' native library, which
     * is packaged with this application.
     */
    /**
     * 初始化指纹模块
     * 
     * @value 0 : 成功
     * @value 1 : 失败
     */
    public static native String JNIFpInit();

    /* 去初始化指纹模块 */
    public static native String JNIFpDeInit();

    /* 获取指纹模块版本信息，信息中包含最大用户数 */
    public static native String JNIFpGetModuleInfo();

    /*
     * 注册新用户 返回值： "uID#xx" 注册成功，xx表示用户ID "-2" 超时 "-1" 失败
     */
    public static native String JNIUserRegisterMOFN();

    /*
     * 删除所有用户 返回值： "0" 删除成功 "-2" 超时 "-1" 失败
     */
    public static native String JNIFpUserDeleteAll();

    /*
     * 删除指定ID用户 返回值： "0" 删除成功 "-2" 超时 "-1" 失败
     */
    public static native String JNIFpDelUserByID(int uID);

    /*
     * 删除指定ID用户 返回值： "uID#xx" 指纹匹配成功，xx表示匹配的ID "-2" 超时 "-1" 失败
     */
    public static native String JNIFpFingerMatch();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("finger");
    }

    /*
     * 仅用于演示
     */
    public static void CallbackDemo(int pro) {
        System.out.printf("C call Java ...use Call_Back_Invoke %d\n", pro);
    }

    /*
     * 指纹注册过程中回调java方法，用于展示注册过程。方法由Android实现。
     */
    public static void CallbackRegister(int progress) {
        RxBus.getDefault().post(new FingerRegisterProgressEvent(progress));
        //TODO
        System.out.printf("register progress = %d\n", progress);
    }
}
