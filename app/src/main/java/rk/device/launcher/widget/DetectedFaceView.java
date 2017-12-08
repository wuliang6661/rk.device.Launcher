package rk.device.launcher.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import cvc.CvcRect;
import rk.device.launcher.R;
import rk.device.launcher.base.utils.DensityUtils;
import rk.device.launcher.utils.ScreenUtil;

/**
 * Created by hanbin on 2017/11/22.
 * <p>
 * 人脸框空
 */
public class DetectedFaceView extends AppCompatImageView {

    private float PREVIEW_WIDTH = 320;
    private float PREVIEW_HEIGHT = 240;
    private Context mContext;
    private RectF mRect = new RectF();
    private Drawable mFaceIndicator = null;


    private int roomHeight;
    private float faceRegionW;
    private float faceRegionH;


    private Rect[] rect = new Rect[1];


    public DetectedFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initPaint();
        mContext = context;
        mFaceIndicator = ContextCompat.getDrawable(context, R.drawable.face);
        roomHeight = DensityUtils.dp2px(mContext, 550);   //默认外部容器高度为550
    }

    public void setFaces(CvcRect cvcRect1, float faceRegionW, int faceRegionH, float width, float height) {
        this.faceRegionW = faceRegionW;
        this.faceRegionH = faceRegionH;
        this.PREVIEW_WIDTH = width;
        this.PREVIEW_HEIGHT = height;
        Rect oneRect = new Rect();
        oneRect.set(cvcRect1.x, cvcRect1.y, cvcRect1.x + cvcRect1.w,
                cvcRect1.y + cvcRect1.h);
        rect[0] = oneRect;
        invalidate();
    }

    /**
     * 设置总体外部容器高度
     */
    public void setRoomHeight(int height) {
        this.roomHeight = height;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (rect == null || rect.length < 1) {
            return;
        }
        if (rect[0] == null) {
            return;
        }
        canvas.rotate(-0);
        for (Rect aRect : rect) {
            mRect.set(aRect);
            canvas.save();
            Log.i("faceView:", "mRect.left:" + mRect.left + ";mRect.top" + mRect.top
                    + ";mRect.right" + mRect.right + ";mRect.bottom" + mRect.bottom);
            float width = faceRegionW / PREVIEW_WIDTH * ScreenUtil.getScreenWidth(mContext);
            float left = ScreenUtil.getScreenWidth(mContext) - mRect.left / PREVIEW_WIDTH * ScreenUtil.getScreenWidth(mContext) - width;
            float top = mRect.top / PREVIEW_HEIGHT * roomHeight;
            float buttom = top + (faceRegionH / PREVIEW_HEIGHT * roomHeight);
            float right = left + width;
            mFaceIndicator.setBounds(Math.round(left), Math.round(top), Math.round(right), Math.round(buttom));
            mFaceIndicator.draw(canvas);
        }
        canvas.restore();
        super.onDraw(canvas);
    }

    private void initPaint() {
        Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //		int color = Color.rgb(0, 150, 255);
        int color = Color.rgb(98, 212, 68);
        //		mLinePaint.setColor(Color.RED);
        mLinePaint.setColor(color);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setAlpha(180);
    }
}
