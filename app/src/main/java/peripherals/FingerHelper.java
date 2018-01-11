package peripherals; 

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class FingerHelper{


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    /* 初始化指纹模块 */
    public static native int JNIFpInit();
    /* 去初始化指纹模块 */
    public static native int JNIFpDeInit();
    /* 获取指纹模块版本信息，信息中包含最大用户数 */
    public static native String JNIFpGetModuleInfo();
    /*
     * 注册新用户
     * 返回值： "uID#xx"   注册成功，xx表示用户ID
     *         "-2"       超时
     *         "-1"       失败
     */
    public static native int JNIUserRegisterMOFN();
    /*
     * 删除所有用户
     * 返回值： "0"        删除成功
     *         "-2"       超时
     *         "-1"       失败
     */
    public static native int JNIFpUserDeleteAll();
    /*
     * 删除指定ID用户
     * 返回值： "0"        删除成功
     *         "-2"       超时
     *         "-1"       失败
     */
    public static native int JNIFpDelUserByID(int uID);
    /*
     * 删除指定ID用户
     * 返回值： "uID#xx"   指纹匹配成功，xx表示匹配的ID
     *         "-2"       超时
     *         "-1"       失败
     */
    public static native int JNIFpFingerMatch();
    /*
     * 获取当前已注册用户总数
     * 返回值："-1"       失败
     *        其它       成功
     */
    public static native int JNIFpGetTotalUser();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("finger");
    }
    /*
     * 仅用于演示
     */
    public  static void CallbackDemo(int pro)
    {
        System.out.printf("C call Java ...use Call_Back_Invoke %d\n", pro);
    }
    /*
     * 指纹注册过程中回调java方法，用于展示注册过程。方法由Android实现。
     */
    public  static  void CallbackRegister(int progress)
    {
        //TODO
        System.out.printf("register progress = %d\n", progress);
    }
}
