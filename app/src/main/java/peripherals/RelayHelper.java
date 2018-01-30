package peripherals;

public class RelayHelper {


    public static native int RelayInit();

    public static native int RelayDeInit();

    public static native int RelaySetOn();

    public static native int RelaySetOff();

   // public static native int RelayGetState();

    static {
        System.loadLibrary("peripherals");
    }
}