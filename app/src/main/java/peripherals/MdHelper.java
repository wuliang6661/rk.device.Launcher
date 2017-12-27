/**
 * Created by Papillon on Nov 21, 2017.
 * "C:\Program Files\Java\jdk1.8.0_144\bin\javah.exe" -v -jni -classpath ./jni -d ./jni peripherals.MdHelper
 */
 
package peripherals; 


/*
 * @brief This class provides interface to native code of the motion detection device.
 */
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


	/*
     * @brief Init the motion detection device. It must be called before any other methods. 
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_mdInit();
     * @endcode
     */
    public static native int PER_mdInit();

	/*
     * @brief Deinit the motion detection device.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_mdDeinit();
     * @endcode
     */
    public static native int PER_mdDeinit();

	/*
     * @brief Get motion detection result.
     * @param block An integer that has boolean semantics which specifies whether this method calling should block or not.
     * @param motionDetected An integer that has boolean semantics which receive the motion detection result.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_mdGet(int block, int *pMotionDetected);
     * @endcode
     */
	public static native int PER_mdGet(int block, int[] motionDetected);
}

