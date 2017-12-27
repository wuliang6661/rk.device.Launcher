/**
 * Created by Papillon on Dec 22, 2017.
 */
 
package peripherals; 


/*
 * @brief This class provides interface to native code of the number pad device.
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

	/*
     * @brief Init the number pad device. It must be called before any other methods. 
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_numberpadInit();
     * @endcode
     */
    public static native int PER_numberpadInit();

	/*
     * @brief Deinit the number pad device.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_numberpadDeinit();
     * @endcode
     */
    public static native int PER_numberpadDeinit();

	/*
     * @brief Pass user-pressed numbers to the number pad device.
     * @param keys User-pressed numbers to be passed to the number pad device.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_numberpadPress(const char *pKeys)
     * @endcode
     */
	public static native int PER_numberpadPress(String keys);
}


