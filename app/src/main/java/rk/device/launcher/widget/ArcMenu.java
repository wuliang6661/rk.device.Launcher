package rk.device.launcher.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import rk.device.launcher.R;

/**
 * Created by mundane on 2018/1/29 下午12:55
 */

public class ArcMenu extends ViewGroup {

    private View mCenterButton;
    private int mCenterX;
    private int mCenterY;
    private Paint mPaint;
    private float mRadius;
    // 半透明的背景半径
    private float mMaxBgRadius = 300f;
    private float mIconRadius;
    // 6个图标的最大扩散距离
    private float mMaxIconRadius = 250f;
    private OnClickListener mCenterButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ValueAnimator backgroudAnimator;
            ValueAnimator iconAnimator;
            if (mRadius == 0) {
                backgroudAnimator = getBackAnimator(0, mMaxBgRadius);
                iconAnimator = getIconAnimator(0, mMaxIconRadius);
            } else {
                backgroudAnimator = getBackAnimator(mMaxBgRadius, 0);
                iconAnimator = getIconAnimator(mMaxIconRadius, 0);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(backgroudAnimator, iconAnimator);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mCenterButton.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCenterButton.setEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animatorSet.start();
        }
    };
    private final int ANIMATION_DURATION = 300;

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        initPaint();
        init(context, attrs, defStyleAttr);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.transparent_black));
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private ValueAnimator getBackAnimator(float start, float end) {
        long duration = (long) Math.abs(start - end);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end).setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        return valueAnimator;
    }

    private ValueAnimator getIconAnimator(float start, float end) {
        long duration = (long) Math.abs(start - end);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end).setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIconRadius = (float) animation.getAnimatedValue();
                requestLayout();
            }
        });
        return valueAnimator;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = getChildAt(i);
            if (mIconRadius <= 1) {
                child.setVisibility(GONE);
            } else {
                child.setVisibility(VISIBLE);
            }
            //	子按钮总共有childCount - 1个
            // 将180度平均分成180/(childCount - 2) 个
            //	Math.PI就是180度
            //	以左下为例, 子按钮位置顺序从上方往左下角排列
            double angle = Math.PI / (childCount - 2) * i;
            int childY = (int) (mIconRadius * Math.sin(angle));
            int childX = (int) (mIconRadius * Math.cos(angle));
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            child.layout(mCenterX + childX - childWidth / 2, mCenterY - childY - childHeight / 2, mCenterX + childX + childWidth / 2, mCenterY - childY + childHeight / 2);
        }
        mCenterButton = getChildAt(childCount - 1);
        mCenterButton.setOnClickListener(mCenterButtonClickListener);
        int width = mCenterButton.getMeasuredWidth();
        int height = mCenterButton.getMeasuredHeight();
        mCenterX = (int) (getMeasuredWidth() * 2.0f / 3);
        mCenterY = (int) (getMeasuredHeight() - height * 2.0f / 3);
        mCenterButton.layout(mCenterX - width / 2, mCenterY - height / 2, mCenterX + width / 2, mCenterY + height / 2);
        invalidate();
    }

    private static final String TAG = "ArcMenu";

    @Override
    protected void onDraw(Canvas canvas) {
//        RectF rectF = new RectF(mCenterX - 500, mCenterY - 500, mCenterX + 500, mCenterY + 500);
//        canvas.drawArc(rectF, 180, 180, true, mPaint);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        super.onDraw(canvas);
    }
}
