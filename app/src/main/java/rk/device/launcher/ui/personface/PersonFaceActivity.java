package rk.device.launcher.ui.personface;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.BitmapUtil;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;


/**
 * MVPPlugin
 * <p>
 * 人脸录入页面
 */

public class PersonFaceActivity extends MVPBaseActivity<PersonFaceContract.View, PersonFacePresenter>
        implements PersonFaceContract.View, View.OnClickListener {


    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.btn_finish_setting)
    Button btnFinishSetting;
    @Bind(R.id.restart_carema)
    Button restartCarema;
    @Bind(R.id.save_face)
    Button saveFace;
    @Bind(R.id.face_img)
    ImageView faceImg;
    @Bind(R.id.button_layout)
    LinearLayout buttonLayout;

    private byte[] mData;
    private Bitmap faceBitmap;

    private User user;


    @Override
    protected int getLayout() {
        return R.layout.act_person_face;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle("添加人脸");
        String id = getIntent().getExtras().getString("id");
        user = DbHelper.queryUserById(id).get(0);

        initCrema();
        btnFinishSetting.setOnClickListener(this);
        restartCarema.setOnClickListener(this);
        saveFace.setOnClickListener(this);
    }

    /**
     * 初始化摄像头
     */
    private void initCrema() {
        SurfaceHolder surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        SurfaceHolderCaremaFont callbackFont = new SurfaceHolderCaremaFont();
        callbackFont.setCallBack(new SurfaceHolderCaremaFont.CallBack() {
            @Override
            public void callMessage(byte[] data, int width, int height) {
                mData = data;
            }

            @Override
            public void callHeightAndWidth(int width, int height) {

            }
        });
        surfaceholder.addCallback(callbackFont);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish_setting:    //拍摄
                faceBitmap = BitmapUtil.byteToBitmap(mData, 640, 480);
                faceImg.setImageBitmap(faceBitmap);
                faceImg.setVisibility(View.VISIBLE);
                btnFinishSetting.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.restart_carema:    //重新拍摄
                faceImg.setVisibility(View.GONE);
                btnFinishSetting.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);
                break;
            case R.id.save_face:     //保存
                if (faceBitmap == null) {
                    showMessageDialog("请先拍摄人脸！");
                    return;
                }
                FaceUtils faceUtils = FaceUtils.getInstance();
                AFR_FSDKFace aa_face = faceUtils.bitmapToFace(faceBitmap);
                if (aa_face == null) {
                    showMessageDialog("未检测到人脸！");
                } else {
                    String name = faceUtils.saveFace(aa_face);
                    user.setFaceID(name);
                    DbHelper.insertUser(user);
                    finish();
                }
                break;
        }
    }
}
