/**
 * Created by Papillon on Nov 21, 2017.
 * "C:\Program Files\Java\jdk1.8.0_144\bin\javah.exe" -v -jni -classpath ./jni -d ./jni peripherals.NfcHelper
 */
 
package peripherals; 


/*
 * @brief This class provides interface to native code of the NFC device.
 */
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

	/*
     * @brief Init the NFC device. It must be called before any other methods. 
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_nfcInit();
     * @endcode
     */
    public static native int PER_nfcInit();

	/*
     * @brief Deinit the NFC device.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_nfcDeinit();
     * @endcode
     */
    public static native int PER_nfcDeinit();

	/*
     * @brief Get identified NFC card.
     * @param card The identified NFC card. It will be filled by native code.
     * @return 0 for success, and non-zero for failure.
     * @note The corresponding C prototype is:
     * @code{.c}
     * int PER_nfcGetCard(rk_nfc_card_uid *pCard)
     * @endcode
     */
	public static native int PER_nfcGetCard(NfcCard card);
}

