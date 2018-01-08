package rk.device.launcher.ui.faceadd;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.BitmapUtil;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.verify.FaceUtils;
import rk.device.launcher.widget.carema.SurfaceHolderCaremaFont;
import rk.device.launcher.widget.lgrecycleadapter.LGRecycleViewAdapter;
import rk.device.launcher.widget.lgrecycleadapter.LGViewHolder;


/**
 * MVPPlugin
 * <p>
 * 录入人脸界面
 */

public class FaceAddActivity extends MVPBaseActivity<FaceAddContract.View, FaceAddPresenter> implements FaceAddContract.View,
        View.OnClickListener {

    @Bind(R.id.camera_surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.edit_name)
    EditText editName;
    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.crema_face)
    Button cremaFace;
    @Bind(R.id.save_face)
    Button saveFace;
    @Bind(R.id.face_img)
    ImageView faceImg;

    byte[] mData;

    @Override
    protected int getLayout() {
        return R.layout.act_face_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goBack();
        setTitle("录入人脸");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle.setLayoutManager(manager);

        initCrema();
        cremaFace.setOnClickListener(this);
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
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    /**
     * 显示已录入的人脸
     */
    private void setAdapter() {
        List<FaceUtils.FaceRegist> registList = FaceUtils.getInstance().getmRegister();
        LGRecycleViewAdapter<FaceUtils.FaceRegist> adapter = new LGRecycleViewAdapter<FaceUtils.FaceRegist>(registList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_face;
            }

            @Override
            public void convert(LGViewHolder holder, FaceUtils.FaceRegist faceRegist, int position) {
                holder.setText(R.id.textView1, faceRegist.getmName());
            }
        };
        recycle.setAdapter(adapter);
    }


    Bitmap faceBitmap;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.crema_face:
                if (mData != null) {
                    Log.d("wuliang", "data size = " + mData.length);
                }
                faceBitmap = BitmapUtil.byteToBitmap(mData, 640, 480);
                faceImg.setImageBitmap(faceBitmap);
                break;
            case R.id.save_face:
                FaceUtils faceUtils = FaceUtils.getInstance();
                if (faceBitmap == null) {
                    showMessageDialog("请先拍摄人脸！");
                    return;
                }
                AFR_FSDKFace aa_face = faceUtils.bitmapToFace(faceBitmap);
                if (aa_face == null) {
                    showMessageDialog("未检测到人脸！");
                } else if (StringUtils.isEmpty(editName.getText().toString())) {
                    showMessageDialog("请输入人名！");
                } else {
                    faceUtils.saveFace(aa_face);
                    setAdapter();
                }

                break;
        }
    }
}
