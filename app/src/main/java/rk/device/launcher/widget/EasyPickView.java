package rk.device.launcher.widget;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;

import rk.device.launcher.R;

/**
 * Created by mundane on 2017/11/10 下午8:05
 */

public class EasyPickView extends View {
	private Paint mTextPaint;
	// 默认字体大小18sp
	private int mTextSize = sp2px(18);
	private int mNormalColor = getResources().getColor(android.R.color.white);
	private int mStrokeWidth = dp2px(5);
	private List<String> mDataList = new ArrayList<>();
	// 文字最大宽度
	private float mMaxTextWidth;
	// 文字上下之间的间距, 默认10dp
	private int mTextPadding = dp2px(10);
	// 文字高度
	private int mTextHeight;
	//	手势解析器
	private GestureDetector mGestureDetector;
	// 视图总宽度的一半
	private int mHalfWidth;
	// 视图总高度的一半
	private int mHalfHeight;
	// 当前选中项
	private int mCurrentIndex = 0;
	// 每一行文字所占用的高度
	private int mCenterPadding;
	// 显示的行数, 默认为3
	private int mShowRowCount = 5;
	// 实际内容高度
	private int mContentHeight;
	private OverScroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mScaledTouchSlop;
	private float mDownY;
	// Y方向上的偏移量
	private float mOffsetY;
	// 在fling之前的Y方向上的偏移量
	private float mOldOffsetY;
	private boolean mIsSliding;
	// 当前偏移个数
	private int mOffsetIndex;
	// 回弹距离
	private float mBounceDistance;
	// 选中那行的颜色
	private int mSelectedColor = Color.parseColor("#338eff");
	// 分隔线的颜色
	private int mDividerColor = 0XFFFFFFFF;
	// 分隔线的高度
	private int mDividerHeight = 2;
	// 是否是循环模式, 默认是
	private boolean mIsRecycleMode;


	private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	};
	private ArgbEvaluator mArgbEvaluator;
	private Paint mDividerPaint;


	public EasyPickView(Context context) {
		this(context, null);
	}

	public EasyPickView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EasyPickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs,defStyleAttr);
		initData(context);
	}

	private void initData(Context context) {
//		mGestureDetector = new GestureDetector(context, mGestureListener);
//		setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// 监听触摸事件
//				return mGestureDetector.onTouchEvent(event);
//			}
//		});
		mArgbEvaluator = new ArgbEvaluator();
		mTextPaint = createTextPaint(mNormalColor, mTextSize);
		mDividerPaint = createPaint(mDividerColor, Paint.Style.STROKE, mDividerHeight);


		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

		mTextHeight = (int) Math.abs(fontMetrics.bottom - fontMetrics.top);
		mCenterPadding = mTextHeight + mTextPadding;
		mContentHeight = mCenterPadding * mShowRowCount;

		mScroller = new OverScroller(context);
		mMinimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
		mMaximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
		mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyPickView, defStyleAttr, 0);
		mIsRecycleMode = a.getBoolean(R.styleable.EasyPickView_recycleMode, true);
		mShowRowCount = a.getInteger(R.styleable.EasyPickView_showRowCount, 5);
		mDividerColor = a.getColor(R.styleable.EasyPickView_dividerColor, Color.GRAY);
		a.recycle();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(true);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int contentWidth = (int) (mMaxTextWidth + getPaddingLeft() + getPaddingRight());
		if (mode != MeasureSpec.EXACTLY) { // wrap_content
			width = contentWidth;
		}

		mode = MeasureSpec.getMode(heightMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		mContentHeight = mTextHeight * mShowRowCount + mTextPadding * mShowRowCount;
		if (mode != MeasureSpec.EXACTLY) { // wrap_content
			height = mContentHeight + getPaddingTop() + getPaddingBottom();
		}

		mHalfWidth = width / 2;
		mHalfHeight = height / 2;

		setMeasuredDimension(width, height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		addVelocityTracker(event);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!mScroller.isFinished()) {
					mScroller.forceFinished(true);
					finishScroll();
				}
				mDownY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:

				mOffsetY = event.getY() - mDownY;
				if (mIsSliding || Math.abs(mOffsetY) > mScaledTouchSlop) {
					mIsSliding = true;
					reDraw();
				}

				break;
			case MotionEvent.ACTION_UP:
				int scrollYVelocity = 2 * getScrollYVelocity() / 3;
				if (Math.abs(scrollYVelocity) > mMinimumVelocity) {
					mOldOffsetY = mOffsetY;
					mScroller.fling(0, 0, 0, scrollYVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
					invalidate();
				} else {
					finishScroll();
				}

				// todo 没有滑动, 则判断点击事件
				if (!mIsSliding) {

				}
				mIsSliding = false;
				recycleVelocityTracker();
				break;
		}
		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {

			mOffsetY = mOldOffsetY + mScroller.getCurrY();

			if (!mScroller.isFinished()) {
				reDraw();
			} else {
				finishScroll();
			}
		}
	}

	private int getScrollYVelocity() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
		int velocity = (int) mVelocityTracker.getYVelocity();
		return velocity;
	}

	private void reDraw() {
		// mCurrentIndex需要偏移的量
		int i = (int) (mOffsetY / (mTextHeight + mTextPadding));
		if (mIsRecycleMode || mCurrentIndex - i >= 0 && mCurrentIndex - i < mDataList.size()) {
			if (mOffsetIndex != i) {
				mOffsetIndex = i;
				if (mOnScrollChangedListener != null) {
					mOnScrollChangedListener.onScrollChanged(getNowIndex(-mOffsetIndex));
				}
			}
			invalidate();
		} else {
			finishScroll();
		}

	}

	private void finishScroll() {
		// 这里其实还可以优化, 停止惯性滑动后矫正位置不应该那么突兀
		// 判断结束滑动后应该停留在哪个位置
		int centerPadding = mTextHeight + mTextPadding;
		float i = mOffsetY % centerPadding;
		if (i > 0.5f * centerPadding) {
			++mOffsetIndex;
		} else if (i < -0.5f * centerPadding) {
			--mOffsetIndex;
		}


		// 重置curIndex
		mCurrentIndex = getNowIndex(-mOffsetIndex);

//		// 计算回弹的距离
		mBounceDistance = mOffsetIndex * centerPadding - mOffsetY;
//		// Y方向上的偏移量必须是centerPadding的整数倍, 是偏移个数 x centerPadding
		mOffsetY += mBounceDistance;
//		mOffsetY = mOffsetIndex * centerPadding;

		// 更新
		if (mOnScrollChangedListener != null) {
			mOnScrollChangedListener.onScrollFinished(mCurrentIndex);
		}
		// 重绘
		reset();
		invalidate();
	}

	private void reset() {
		mOffsetY = 0;
		mOldOffsetY = 0;
		mOffsetIndex = 0;
		mBounceDistance = 0;
	}

	private int getNowIndex(int offsetIndex) {
		int index = mCurrentIndex + offsetIndex;
		if (mIsRecycleMode) {
			if (index < 0) {
				index = index % mDataList.size() + mDataList.size();
			} else if (index > mDataList.size() - 1) {
				index = index % mDataList.size();
			}
		} else {
			if (index < 0) {
				index = 0;
			} else if (index > mDataList.size() - 1) {
				index = mDataList.size() - 1;
			}
		}

		return index;
	}


	private void addVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	/**
	 * 滚动到指定位置
	 *
	 * @param index 需要滚动到的指定位置
	 */
	public void moveTo(int index) {
		if (index < 0 || index >= mDataList.size() || mCurrentIndex == index) {
			return;
		}

		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}

		finishScroll();

		int dy = 0;
		if (!mIsRecycleMode) {
			dy = (mCurrentIndex - index) * mCenterPadding;
		} else {
			int offsetIndex = mCurrentIndex - index;
			// 看看从哪个方向滚动距离最短
			int d1 = Math.abs(offsetIndex) * mCenterPadding;
			int d2 = (mDataList.size() - Math.abs(offsetIndex)) * mCenterPadding;

			if (offsetIndex > 0) {
				if (d1 < d2)
					dy = d1; // ascent
				else
					dy = -d2; // descent
			} else {
				if (d1 < d2)
					dy = -d1; // descent
				else
					dy = d2; // ascent
			}
		}
		mScroller.startScroll(0, 0, 0, dy, 500);
		invalidate();
	}

	public void moveToImmidiatly(int index) {
		if (index < 0 || index >= mDataList.size() || mCurrentIndex == index) {
			return;
		}

		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}

		finishScroll();
		mCurrentIndex = index;
		invalidate();
		if (mOnScrollChangedListener != null) {
			mOnScrollChangedListener.onScrollChanged(mCurrentIndex);
		}

	}

	public int getIndex() {
		return mCurrentIndex;
	}

	/**
	 * 设置要显示的数据
	 *
	 * @param dataList 要显示的数据
	 */
	public void setDataList(List<String> dataList) {

		if (!dataList.isEmpty() && dataList != null) {

			mDataList.clear();
			mDataList.addAll(dataList);

			if (!mScroller.isFinished()) {
				mScroller.forceFinished(true);
			}
			finishScroll();

			// 更新maxTextWidth
			mMaxTextWidth = 0;
			for (String text : mDataList) {
				float tempWidth = mTextPaint.measureText(text);
				if (tempWidth > mMaxTextWidth) {
					mMaxTextWidth = tempWidth;
				}
			}
			mCurrentIndex = 0;
			if (mOnScrollChangedListener != null) {
				mOnScrollChangedListener.onScrollChanged(mCurrentIndex);
			}
			requestLayout();
			invalidate();
		}

	}

	public void updateDataList(List<String> dataList) {

		if (!dataList.isEmpty() && dataList != null) {

			mDataList.clear();
			mDataList.addAll(dataList);

			// forcefinished和finishScroll这两句代码很重要, 不加的话会有bug
			if (!mScroller.isFinished()) {
				mScroller.forceFinished(true);
			}
			finishScroll();

			// 更新maxTextWidth
			mMaxTextWidth = 0;
			for (String text : mDataList) {
				float tempWidth = mTextPaint.measureText(text);
				if (tempWidth > mMaxTextWidth) {
					mMaxTextWidth = tempWidth;
				}
			}
			if (mCurrentIndex > mDataList.size() - 1) {
				mCurrentIndex = mDataList.size() - 1;
			}
			if (mOnScrollChangedListener != null) {
				mOnScrollChangedListener.onScrollChanged(mCurrentIndex);
			}
			requestLayout();
			invalidate();
		}

	}

	/**
	 * 滚动发生变化时的回调接口
	 */
	public interface OnScrollChangedListener {
		public void onScrollChanged(int curIndex);

		public void onScrollFinished(int curIndex);
	}

	private OnScrollChangedListener mOnScrollChangedListener;

	public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
		mOnScrollChangedListener = onScrollChangedListener;
	}

	public String getValue() {
		if (mDataList != null && !mDataList.isEmpty()) {
			return mDataList.get(mCurrentIndex);
		} else {
			return null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mDataList.isEmpty()) {
			return;
		}
		canvas.clipRect(getPaddingLeft(), getHeight() / 2 - mContentHeight / 2, getWidth() - getPaddingRight(), getHeight() / 2 + mContentHeight / 2);

		int centerPadding = mTextHeight + mTextPadding;
		// 绘制文字，从当前中间项往前、后一共绘制maxShowNum个字
		int half = (mShowRowCount + 1) / 2;
		for (int i = -half; i <= half; i++) {
			int textIndex = mCurrentIndex - mOffsetIndex + i;

			if (mIsRecycleMode) {
				if (textIndex < 0) {
					textIndex = textIndex % mDataList.size() + mDataList.size();
				} else if (textIndex > mDataList.size() - 1) {
					textIndex = textIndex % mDataList.size();
				}
			}

			if (textIndex >= 0 && textIndex < mDataList.size()) {
				// 计算每一行字的中间Y坐标
				int tempY = mHalfHeight + i * mCenterPadding;
				tempY += mOffsetY % centerPadding;
				float fraction = Math.abs((tempY - mHalfHeight) * 1.0f / mCenterPadding);
				fraction = fraction >= 1 ? 1 : fraction;
				int textColor = (int) mArgbEvaluator.evaluate(fraction, mSelectedColor, mNormalColor);
				mTextPaint.setColor(textColor);

				Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

				String text = mDataList.get(textIndex);
				float textWidth = mTextPaint.measureText(text);
				canvas.drawText(text, mHalfWidth - textWidth / 2, tempY - (fontMetrics.ascent + fontMetrics.descent) / 2, mTextPaint);
			}
		}
		// 绘制分割线
		canvas.drawLine(getPaddingLeft(), mHalfHeight - mCenterPadding * 1.0f / 2, getWidth() - getPaddingRight(), mHalfHeight - mCenterPadding * 1.0f / 2, mDividerPaint);
		canvas.drawLine(getPaddingLeft(), mHalfHeight + mCenterPadding * 1.0f / 2, getWidth() - getPaddingRight(), mHalfHeight + mCenterPadding * 1.0f / 2, mDividerPaint);
	}

	private Paint createTextPaint(int paintColor, int textSize) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		paint.setColor(paintColor);
		paint.setTextSize(textSize);
		return paint;
	}

	private Paint createPaint(int paintColor, Paint.Style style, int width) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		paint.setColor(paintColor);
		paint.setStyle(style);
		paint.setStrokeWidth(width);
		//	这行代码会使得进度边缘变圆
		paint.setStrokeCap(Paint.Cap.ROUND);
		return paint;
	}

	private int px2dp(float paValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (paValue / scale + 0.5f);
	}

	private int dp2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private int px2sp(float pxValue) {
		final float frontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / frontScale + 0.5f);
	}

	private int sp2px(float spValue) {
		final float frontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * frontScale + 0.5f);
	}
}
