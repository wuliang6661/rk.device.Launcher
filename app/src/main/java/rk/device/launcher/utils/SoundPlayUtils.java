package rk.device.launcher.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import rk.device.launcher.R;


/**
 * Created by zhouyinbo on 2017/7/8.
 */

public class SoundPlayUtils {

    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;

    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }

        // 初始化声音
        mContext = context;

        mSoundPlayer.load(mContext, R.raw.beep, 1);// 1
        mSoundPlayer.load(mContext, R.raw.error, 2);// 2
        mSoundPlayer.load(mContext, R.raw.success, 3);// 3
        mSoundPlayer.load(mContext, R.raw.success_two, 4);// 3

        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }
}
