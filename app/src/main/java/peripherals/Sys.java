/**
 * Created by Papillon on Nov 21, 2017.
 * "C:\Program Files\Java\jdk1.8.0_144\bin\javah.exe" -v -jni -classpath ./jni -d ./jni peripherals.LedHelper
 */
 
package peripherals; 


/*
 * @brief This class provides interface to native functions for maintenance.
 */
public class Sys {
    static {
		try {
			System.loadLibrary("peripherals");			
		}
		catch (UnsatisfiedLinkError e) {
			System.out.println("Failed to load library: " + e);		
//			System.exit(-1);
		}
    }

	/*
     * @brief Reboot Android to Recovery mode.
     * @return 0 for success, and non-zero for failure.
     */
    public static native int rebootToRecovery();
        
}

