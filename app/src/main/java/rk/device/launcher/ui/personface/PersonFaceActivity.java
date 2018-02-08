package rk.device.launcher.ui.personface;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.BitmapUtil;
import rk.device.launcher.utils.FileUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.cache.CacheUtils;
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
    @Bind(R.id.hint_text)
    TextView hintText;
    @Bind(R.id.iv_back)
    ImageView ivBack;

    private byte[] mData;
    private Bitmap faceBitmap;

    private User user;
    private boolean isUpdate = false;
    private FaceUtils faceUtils;

    private AFR_FSDKFace aa_face;

    SurfaceHolder surfaceholder;
    SurfaceHolderCaremaFont callbackFont;


    @Override
    protected int getLayout() {
        return R.layout.act_person_face;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.add_face));
        String id = getIntent().getExtras().getString("id");
        user = DbHelper.queryUserById(id).get(0);
        faceUtils = FaceUtils.getInstance();

        initView();
        initCrema();
        btnFinishSetting.setOnClickListener(this);
        restartCarema.setOnClickListener(this);
        saveFace.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }


    /**
     * 初始化布局
     */
    private void initView() {
        if (!StringUtils.isEmpty(user.getFaceID())) {
            isUpdate = true;
            setTitle(getString(R.string.face_details));
            faceImg.setImageBitmap(BitmapFactory.decodeFile(CacheUtils.getFaceFile() + "/" + user.getFaceID() + ".png"));
            faceImg.setVisibility(View.VISIBLE);
            hintText.setVisibility(View.INVISIBLE);
            btnFinishSetting.setText(R.string.restart_photo);
            setRightButton(R.drawable.delete_person, this);
        }
    }


    /**
     * 初始化摄像头
     */
    private void initCrema() {
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        callbackFont = new SurfaceHolderCaremaFont();
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
    protected void onDestroy() {
        callbackFont.setCallBack(null);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish_setting:    //拍摄
                if (isUpdate) {
                    restartPic();
                } else {
                    tackPic();
                }
                break;
            case R.id.restart_carema:    //重新拍摄
                restartPic2();
                break;
            case R.id.save_face:     //保存
                saveFace();
                break;
            case R.id.title_right:      //删除人脸
                deleteFace();
                break;
            case R.id.iv_back:    //返回
                if (aa_face == null) {
                    finish();
                } else {
                    showMessageDialog(getString(R.string.exit_face_null), getString(R.string.confirm), v1 -> {
                        dissmissMessageDialog();
                        finish();
                    });
                }
                break;
        }
    }


    /**
     * 已保存人脸的情况下，点击重新拍摄
     */
    private void restartPic() {
        faceImg.setVisibility(View.GONE);
        btnFinishSetting.setVisibility(View.VISIBLE);
        hintText.setVisibility(View.VISIBLE);
        hintText.setText(R.string.face_hint);
        hintText.setBackgroundResource(R.drawable.face_add_hint);
        btnFinishSetting.setText(R.string.paishe);
        setTitle(getString(R.string.add_face));
        hideRightButton();
        buttonLayout.setVisibility(View.GONE);
        isUpdate = false;
    }


    /**
     * 点击拍摄
     */
    private void tackPic() {
        faceBitmap = BitmapUtil.byteToBitmap(mData, 640, 480);
        faceImg.setImageBitmap(faceBitmap);
        faceImg.setVisibility(View.VISIBLE);
        aa_face = faceUtils.bitmapToFace(faceBitmap);
        if (aa_face == null) {
            btnFinishSetting.setText(R.string.restart_photo);
            hintText.setText(R.string.face_no_full);
            hintText.setBackgroundResource(R.drawable.face_add_error);
            isUpdate = true;
        } else {
            btnFinishSetting.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);
            hintText.setText(R.string.face_add_suress);
            hintText.setBackgroundResource(R.drawable.face_add_suress);
        }
    }


    /**
     * 拍摄成功，点击重新拍摄
     */
    private void restartPic2() {
        faceImg.setVisibility(View.GONE);
        btnFinishSetting.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.GONE);
        aa_face = null;
        hintText.setText(R.string.face_hint);
        hintText.setBackgroundResource(R.drawable.face_add_hint);
    }

    /**
     * 保存人脸
     */
    private void saveFace() {
        if (faceBitmap == null) {
            showMessageDialog(getString(R.string.please_face));
            return;
        }
        faceUtils = FaceUtils.getInstance();
        if (!StringUtils.isEmpty(user.getFaceID())) {
            faceUtils.delete(user.getFaceID());
            FileUtils.deleteFile(CacheUtils.getFaceFile() + "/" + user.getFaceID() + ".png");
        }
        if (faceUtils.getmRegister().size() > 1000) {
            hintText.setText(R.string.face_limit);
            hintText.setBackgroundResource(R.drawable.face_add_error);
            return;
        }
        String name = faceUtils.saveFace(aa_face);
        if (StringUtils.isEmpty(name)) {
            hintText.setText(R.string.save_error);
            hintText.setBackgroundResource(R.drawable.face_add_error);
        } else {
            BitmapUtil.saveBitmap(name + ".png", faceBitmap);
            user.setFaceID(name);
            user.setUploadStatus(0);
            DbHelper.insertUser(user);
            finish();
        }
    }

    /**
     * 删除人脸
     */
    private void deleteFace() {
        showMessageDialog(getString(R.string.delete_face), getString(R.string.confirm), v1 -> {
            if (faceUtils.delete(user.getFaceID())) {
                user.setFaceID("");
                DbHelper.insertUser(user);
                dissmissMessageDialog();
                finish();
            } else {
                showMessageDialog(getString(R.string.delete_error));
            }
        });
    }
}