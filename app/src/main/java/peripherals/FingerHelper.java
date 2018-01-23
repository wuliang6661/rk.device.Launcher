package peripherals;


import rk.device.launcher.bean.event.FingerRegisterProgressEvent;
import rk.device.launcher.utils.rxjava.RxBus;

public class FingerHelper{

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native int JNIFpInit();
    public static native int JNIFpDeInit(int moduleID);
    public static native String JNIFpGetModuleInfo(int moduleID);
    public static native int JNIUserRegisterMOFN(int moduleID);
    public static native int JNIFpUserDeleteAll(int moduleID);
    public static native int JNIFpDelUserByID(int moduleID, int uID);
    public static native int JNIFpFingerMatch(int moduleID);
    public static native int JNIFpGetTotalUser(int moduleID);
    public static native int JNIFpGetRemainSpace(int moduleID);


    public  static void CallbackDemo(int pro)
    {
        System.out.printf("C call Java ...use Call_Back_Invoke %d\n", pro);
    }
    public  static  void CallbackRegister(int progress)
    {
        //TODO
        System.out.printf("step %d\n", progress);
        RxBus.getDefault().post(new FingerRegisterProgressEvent(progress));
    }
    
    /* Used to load the 'native-lib' library on application startup. */
    static {
        System.loadLibrary("peripherals");
    }    
}
