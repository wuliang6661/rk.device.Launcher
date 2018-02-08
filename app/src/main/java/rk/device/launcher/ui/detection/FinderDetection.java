package rk.device.launcher.ui.detection;

import android.os.Bundle;
import android.support.annotation.Nullable;

import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.utils.ResUtil;

/**
 * Created by wuliang on 2018/2/6.
 * <p>
 * 指纹硬件检测
 */

public class FinderDetection extends BaseActivity {


    @Override
    protected int getLayout() {
        return R.layout.activity_finger_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle(ResUtil.getString(R.string.finder_detecation));
    }
}
