/**
 * Created by Papillon on Nov 16, 2017.
 */

package cvc;

public class CvcHelper {
    static {
        try {
            System.loadLibrary("cvc");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Failed to load library: " + e);
            System.exit(-1);
        }
    }

    // int CVC_init();
    public static native int CVC_init();

    // int CVC_deinit();
    public static native int CVC_deinit();

    // int CVC_calibratorInit(const int patternW, const int patternH);
    public static native int CVC_calibratorInit(int patternW, int patternH);

    // int CVC_calibratorCollectImage();
    public static native int CVC_calibratorCollectImage();

    // int CVC_calibratorStereoCalibrate();
    public static native int CVC_calibratorStereoCalibrate();

    /* threshold 0~100% */
    public static native int CVC_setLivingFaceThreshold(int threshold);

    // int CVC_detectFace(const int id, OSA_Rect *pFace);
    public static native int CVC_detectFace(int id, CvcRect faceRegion, int[] relativeW, int[] relativeH);

    // int CVC_determineLivingFace(int *pPossibility, void *pFace, size_t *pLen);
    public static native int CVC_determineLivingFace(int[] possibility, byte[] face, int[] len);
}

