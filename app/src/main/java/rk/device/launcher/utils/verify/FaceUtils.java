package rk.device.launcher.utils.verify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rk.device.launcher.utils.FileUtils;

/**
 * Created by wuliang on 2017/12/28.
 * <p>
 * 人脸检测及人脸识别的工具类
 */

public class FaceUtils {

    private static final String TAG = "FaceUtils";

    private static final String facePath = "/data/rk_backup/face";

    private static String appid = "7p9bytNNtUW7h4i6QTMeJsWpGZG6zxbcuupyTEwc5Tpi";
    private static String ft_key = "56nNWR9uZaSZC4NidCkvtTcY88X3aAmTcNvoMoGeTMqn";
    private static String fd_key = "56nNWR9uZaSZC4NidCkvtTcfHXnDU7JVhDtvVWjUUyNS";
    private static String fr_key = "56nNWR9uZaSZC4NidCkvtTd9w8ptKjTbNkkqQjDMZBEY";
    private static String age_key = "56nNWR9uZaSZC4NidCkvtTdQFwMChtZ3Zk5gZmZoBT1V";
    private static String gender_key = "56nNWR9uZaSZC4NidCkvtTdXRLcNn6HM1w7rvn7NW5se";

    private static FaceUtils faceUtils;

    /**
     * 已注册的人脸集合
     */
    private List<FaceRegist> mRegister;
    /**
     * 人脸识别
     */
    private AFR_FSDKEngine mFREngine;
    /**
     * 人脸检测
     */
    private AFD_FSDKEngine mFDengine;
    /**
     * 人脸追踪
     */
    private AFT_FSDKEngine mFTengine;
    /**
     * 年龄检测
     */
    private ASAE_FSDKEngine mAgeEngine;
    /**
     * 性别检测
     */
    private ASGE_FSDKEngine mGenderEngine;

    /**
     * 人脸版本信息
     */
    private AFR_FSDKVersion mFRVersion;

    /**
     * 人脸追踪的人脸集合
     */
    private List<AFT_FSDKFace> mFTresult;

    private Context mContext;

    /**
     * 年龄和性别检测
     */
    private List<ASAE_FSDKAge> ages = new ArrayList<>();
    private List<ASGE_FSDKGender> genders = new ArrayList<>();


    public static FaceUtils getInstance() {
        if (faceUtils == null) {
            synchronized (FaceUtils.class) {
                if (faceUtils == null) {
                    faceUtils = new FaceUtils();
                }
            }
        }
        return faceUtils;
    }


    private FaceUtils() {
        loop = null;
    }

    /**
     * 注册人脸识别
     */
    public void init(Context context) {
        mRegister = new ArrayList<>();
        mFREngine = new AFR_FSDKEngine();
        mFRVersion = new AFR_FSDKVersion();
        mFTresult = new ArrayList<>();
        mContext = context;
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceUtils.appid, FaceUtils.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
            Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
        }
        registerFaceD();
        registerFaceT();
        registerFaceGen();
        registerFaceAge();
    }


    /**
     * 注册人脸检测
     */
    private void registerFaceD() {
        mFDengine = new AFD_FSDKEngine();
        AFD_FSDKError err = mFDengine.AFD_FSDK_InitialFaceEngine(FaceUtils.appid, FaceUtils.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
        if (err.getCode() != AFD_FSDKError.MOK) {
            Toast.makeText(mContext, "FD初始化失败，错误码：" + err.getCode(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 注册人脸追踪引擎
     */
    private void registerFaceT() {
        mFTengine = new AFT_FSDKEngine();
        AFT_FSDKError err = mFTengine.AFT_FSDK_InitialFaceEngine(FaceUtils.appid, FaceUtils.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
    }


    /**
     * 注册性别检测
     */
    private void registerFaceGen() {
        mGenderEngine = new ASGE_FSDKEngine();
        ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceUtils.appid, FaceUtils.gender_key);
        Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
    }


    /**
     * 注册年龄检测
     */
    private void registerFaceAge() {
        mAgeEngine = new ASAE_FSDKEngine();
        ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceUtils.appid, FaceUtils.age_key);
        Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
    }


    /**
     * 从bitmap中检测出人脸
     */
    public AFR_FSDKFace bitmapToFace(Bitmap mBitmap) {
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
        ImageConverter convert = new ImageConverter();
        convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
        if (convert.convert(mBitmap, data)) {
            Log.d(TAG, "convert ok!");
        }
        convert.destroy();

        List<AFD_FSDKFace> result = new ArrayList<>();
        AFD_FSDKError err = mFDengine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());

        if (!result.isEmpty()) {
            AFR_FSDKFace result1 = new AFR_FSDKFace();
            AFR_FSDKError error1 = mFREngine.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
            Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());

            if (error1.getCode() == AFR_FSDKError.MOK) {
                return result1.clone();
            } else {
                Toast.makeText(mContext, "检测人脸失败，错误码：" + error1.getCode(), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }


    /**
     * 保存人脸到本地，并返回人脸Id
     */
    public String saveFace(AFR_FSDKFace face) {
        Log.d("wuliang", "保存人脸");
        String name = UUID.randomUUID().toString();
        try {
            //check if already registered.
            boolean add = true;
            for (FaceRegist frface : mRegister) {
                if (frface.mName.equals(name)) {
                    frface.mFaceList.add(face);
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.
                FaceRegist frface = new FaceRegist(name);
                frface.mFaceList.add(face);
                mRegister.add(frface);
            }
            if (FileUtils.createOrExistsDir(new File(facePath))) {
                FileUtils.setPermission(facePath);
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(facePath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.mName);
                    }
                    bos.close();
                    fs.close();
                    //save new feature
                    fs = new FileOutputStream(facePath + "/" + name + ".data", true);
                    bos = new ExtOutputStream(fs);
                    bos.writeBytes(face.getFeatureData());
                    FileUtils.setPermission(facePath + "/" + name + ".data");
                    bos.close();
                    fs.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return name;
    }

    /**
     * 删除某个用户的脸
     */
    public boolean delete(String name) {
        try {
            //check if already registered.
            boolean find = false;
            for (FaceRegist frface : mRegister) {
                if (frface.mName.equals(name)) {
                    File delfile = new File(facePath + "/" + name + ".data");
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    mRegister.remove(frface);
                    find = true;
                    break;
                }
            }
            if (find) {
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(facePath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.mName);
                    }
                    bos.close();
                    fs.close();
                }
            }
            return find;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 初始化读出所有的人脸
     */
    public boolean loadFaces() {
        if (FileUtils.createOrExistsDir(new File(facePath))) {
            if (loadInfo()) {
                try {
                    for (FaceRegist face : mRegister) {
                        Log.d(TAG, "load name:" + face.mName + "'s face feature data.");
                        FileInputStream fs = new FileInputStream(facePath + "/" + face.mName + ".data");
                        ExtInputStream bos = new ExtInputStream(fs);
                        AFR_FSDKFace afr = null;
                        do {
                            if (afr != null) {
                                face.mFaceList.add(afr);
                            }
                            afr = new AFR_FSDKFace();
                        } while (bos.readBytes(afr.getFeatureData()));
                        bos.close();
                        fs.close();
                        Log.d(TAG, "load name: size = " + face.mFaceList.size());
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    private boolean loadInfo() {
        if (!mRegister.isEmpty()) {
            return false;
        }
        try {
            File file = new File(facePath + "/face.txt");
            if (!file.exists()) {
                return false;
            }
            FileInputStream fs = new FileInputStream(facePath + "/face.txt");
            ExtInputStream bos = new ExtInputStream(fs);
            //load version
            String version_saved = bos.readString();
            //load all regist name.
            if (version_saved != null) {
                for (String name = bos.readString(); name != null; name = bos.readString()) {
                    if (new File(facePath + "/" + name + ".data").exists()) {
                        mRegister.add(new FaceRegist(name));
                    }
                }
            }
            bos.close();
            fs.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 保存人脸的版本信息作为text文件的开头
     */
    private boolean saveInfo() throws IOException {
        FileOutputStream fs = new FileOutputStream(facePath + "/face.txt");
        ExtOutputStream bos = new ExtOutputStream(fs);
        bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
        FileUtils.setPermission(facePath + "/face.txt");
        bos.close();
        fs.close();
        return true;
    }


    private class FaceRegist {
        String mName;
        List<AFR_FSDKFace> mFaceList;

        FaceRegist(String name) {
            mName = name;
            mFaceList = new ArrayList<>();
        }

        public String getmName() {
            return mName;
        }
    }


    public List<FaceRegist> getmRegister() {
        return mRegister;
    }

    /***********************************************人脸识别(追踪到人脸并识别)**********************************************************/


    private byte[] mImageNV21 = null;
    private AFT_FSDKFace mAFT_FSDKFace = null;
    private int mWidth, mHeight;
    private FRAbsLoop loop;


    public void setCaremaSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }


    /**
     * 人脸追踪
     */
    public Rect[] caremeDataToFace(byte[] data, int width, int height) {
        AFT_FSDKError err = mFTengine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, mFTresult);
//        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
//        Log.d(TAG, "Face=" + mFTresult.size());
        for (AFT_FSDKFace face : mFTresult) {
            Log.d(TAG, "Face:" + face.toString());
        }
        if (mImageNV21 == null) {
            if (!mFTresult.isEmpty()) {
                mAFT_FSDKFace = mFTresult.get(0).clone();
                mImageNV21 = data.clone();
            }
        }
        //copy rects
        Rect[] rects = new Rect[mFTresult.size()];
        for (int i = 0; i < mFTresult.size(); i++) {
            rects[i] = new Rect(mFTresult.get(i).getRect());
        }
        //clear result.
        mFTresult.clear();
        return rects;
    }

    /**
     * 启动人脸识别引擎
     */
    public void startFaceFR() {
        loop = new FRAbsLoop();
        loop.start();
    }

    /**
     * 停止人脸识别引擎
     */
    public void stopFaceFR() {
        loop.shutdown();
    }


    private class FRAbsLoop extends AbsLoop {

        private AFR_FSDKFace result = new AFR_FSDKFace();
        List<ASAE_FSDKFace> face1 = new ArrayList<>();
        List<ASGE_FSDKFace> face2 = new ArrayList<>();

        @Override
        public void setup() {
        }

        @Override
        public void loop() {
            if (mImageNV21 != null) {
                long time = System.currentTimeMillis();
                AFR_FSDKError error = mFREngine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
                Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;
                for (FaceUtils.FaceRegist fr : mRegister) {
                    for (AFR_FSDKFace face : fr.mFaceList) {
                        error = mFREngine.AFR_FSDK_FacePairMatching(result, face, score);
                        Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = fr.mName;
                        }
                    }
                }

                //age & gender
                face1.clear();
                face2.clear();
                face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
                ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
                Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
                Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
                final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");

                if (max > 0.7f) {
                    //fr success.
                    Log.d("wuliang", "fit Score:" + max + ", NAME:" + name + ",age:" + age + ",gender:" + gender);
                    if (featureFace != null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        featureFace.faceSuress(name, max);
                    }
                } else {
                    final String mNameShow = "未识别";

                }
                mImageNV21 = null;
            }
        }

        @Override
        public void over() {
        }
    }


    private FeatureFace featureFace;

    /**
     * 设置人脸识别完成回调
     */
    public void setFaceFeature(FeatureFace faceFeature) {
        this.featureFace = faceFeature;
    }


    public interface FeatureFace {

        void faceSuress(String name, float max_score);

    }


    /**
     * 注销所有人脸识别引擎
     */
    public void destory() {
        mFDengine.AFD_FSDK_UninitialFaceEngine();
        mFREngine.AFR_FSDK_UninitialEngine();
        mFTengine.AFT_FSDK_UninitialFaceEngine();
        mAgeEngine.ASAE_FSDK_UninitAgeEngine();
        mGenderEngine.ASGE_FSDK_UninitGenderEngine();
    }
}