package rk.device.launcher.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import rk.device.launcher.R;
import rk.device.launcher.base.utils.DensityUtils;
import rk.device.launcher.base.utils.WindowManagerUtils;

/**
 * Created by hanbin on 2017/11/22.
 */
public class DetectedFaceView extends AppCompatImageView {

    private float    PREVIEW_WIDTH  = 320;
    private float    PREVIEW_HEIGHT = 240;
    private Context  mContext;
    private Paint    mLinePaint;
    private Rect[]   mFaces;
    private RectF    mRect          = new RectF();
    private Drawable mFaceIndicator = null;
    private float    heightScale    = 0;
    private float    widthScale     = 0;

    public DetectedFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initPaint();
        mContext = context;
        mFaceIndicator = getResources().getDrawable(R.drawable.face);
        widthScale = WindowManagerUtils.getWindowWidth(mContext) / PREVIEW_WIDTH;
        heightScale = DensityUtils.dp2px(mContext, 550) / PREVIEW_HEIGHT;
        Log.i("widthScale", "widthScale:" + WindowManagerUtils.getWindowWidth(mContext));
        Log.i("widthScale", "widthScale:" + widthScale);
        Log.i("heightScale", "heightScale:" + heightScale);
    }

    public void setFaces(Rect[] faces) {
        this.mFaces = faces;
        invalidate();
    }

    public void setSize(int width,int height){

    }

    public void clearFaces() {
        mFaces = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (mFaces == null || mFaces.length < 1) {
            return;
        }
        canvas.rotate(-0);
        for (int i = 0; i < mFaces.length; i++) {
            mRect.set(mFaces[i]);
            canvas.save();
            Log.i("faceView:", "mRect.left:" + mRect.left + ";mRect.top" + mRect.top
                    + ";mRect.right" + mRect.right + ";mRect.bottom" + mRect.bottom);
            float width = (mRect.right - mRect.left) * widthScale;
            //            float left = WindowManagerUtils.getWindowWidth(mContext) - (mRect.right * widthScale);
            //            float right = left + width;

            float left = mRect.left * widthScale;
            float right = left + width;
            mFaceIndicator.setBounds(Math.round(left), Math.round(mRect.top * heightScale),
                    Math.round(right), Math.round(mRect.bottom * heightScale));
            mFaceIndicator.draw(canvas);
        }
        canvas.restore();
        super.onDraw(canvas);
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //		int color = Color.rgb(0, 150, 255);
        int color = Color.rgb(98, 212, 68);
        //		mLinePaint.setColor(Color.RED);
        mLinePaint.setColor(color);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setAlpha(180);
    }
}
