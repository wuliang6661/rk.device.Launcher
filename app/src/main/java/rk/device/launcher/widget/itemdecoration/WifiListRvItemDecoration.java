package rk.device.launcher.widget.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import rk.device.launcher.R;
import rk.device.launcher.utils.DensityUtil;


/**
 * @author : mundane
 * @time : 2017/9/20 14:51
 * @description :
 * @file : WifiListRvItemDecoration.java
 */

public class WifiListRvItemDecoration extends RecyclerView.ItemDecoration {

	private int mDividerHeight;
	private Paint mPaint;

	public WifiListRvItemDecoration(Context context) {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setColor(context.getResources().getColor(R.color.blue_31598a));
		mDividerHeight = DensityUtil.dp2px(0.5f);
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);
			final int left = parent.getPaddingLeft();
			final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
			final int top = child.getBottom() + layoutParams.bottomMargin;
			final int bottom = top + mDividerHeight;
			c.drawRect(left, top, right, bottom, mPaint);
			if (i == 0) {
                c.drawRect(left, child.getTop()+ layoutParams.topMargin - mDividerHeight, right, child.getTop() + layoutParams.topMargin, mPaint);
			}
		}

	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDrawOver(c, parent, state);
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		int position = parent.getChildAdapterPosition(view);
		if (position == 0) {
			outRect.set(0, mDividerHeight, 0, mDividerHeight);
		} else {
			outRect.set(0, 0, 0, mDividerHeight);
		}
	}
}
