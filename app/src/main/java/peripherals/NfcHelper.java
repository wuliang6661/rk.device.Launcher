/**
 * Created by Papillon on Nov 21, 2017.
 * "C:\Program Files\Java\jdk1.8.0_144\bin\javah.exe" -v -jni -classpath ./jni -d ./jni peripherals.NfcHelper
 */
 
package peripherals; 


public class NfcHelper {
    static {
		try {
			System.loadLibrary("peripherals");			
		}
		catch (UnsatisfiedLinkError e) {
			System.out.println("Failed to load library: " + e);		
			System.exit(-1);
		}
    }
    
    public static native int PER_nfcInit();
        
    public static native int PER_nfcDeinit();
    
	public static native int PER_nfcGetCard(NfcCard card);
}

