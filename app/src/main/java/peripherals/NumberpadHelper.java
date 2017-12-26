package peripherals;

/**
 * Created by Papillon on Dec 22, 2017.
 */


public class NumberpadHelper {
    static {
        try {
            System.loadLibrary("peripherals");
        }
        catch (UnsatisfiedLinkError e) {
            System.out.println("Failed to load library: " + e);
            System.exit(-1);
        }
    }

    public static native int PER_numberpadInit();

    public static native int PER_numberpadDeinit();

    public static native int PER_numberpadPress(String keys);
}

