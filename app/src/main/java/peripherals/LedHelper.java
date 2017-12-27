/**
 * Created by Papillon on Nov 21, 2017.
 */
 
package peripherals; 


/*
 * @brief This class provides interface to native code of the LED device.
 */
public class LedHelper {
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
     * @brief Init the LED device. It must be called before any other methods. 
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_ledInit();
     * @endcode
     */
    public static native int PER_ledInit();

	/*
     * @brief Deinit the LED device.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_ledDeinit();
     * @endcode
     */
    public static native int PER_ledDeinit();

	/*
     * @brief Switch the LED on or off.
     * @param on An integer that has boolean semantics which specifies whether the LED should be turned on or off.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_ledToggle(int on);
     * @endcode
     */
	public static native int PER_ledToggle(int on);
}

