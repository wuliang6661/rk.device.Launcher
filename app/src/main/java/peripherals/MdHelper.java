/**
 * Created by Papillon on Nov 21, 2017.
 * "C:\Program Files\Java\jdk1.8.0_144\bin\javah.exe" -v -jni -classpath ./jni -d ./jni peripherals.MdHelper
 */
 
package peripherals; 


public class MdHelper {
    static {
		try {
			System.loadLibrary("peripherals");			
		}
		catch (UnsatisfiedLinkError e) {
			System.out.println("Failed to load library: " + e);		
			System.exit(-1);
		}
    }
    
    public static native int PER_mdInit();
        
    public static native int PER_mdDeinit();

	// int PER_mdGet(int block, int *pMotionDetected);
	public static native int PER_mdGet(int block, int[] motionDetected);
}

