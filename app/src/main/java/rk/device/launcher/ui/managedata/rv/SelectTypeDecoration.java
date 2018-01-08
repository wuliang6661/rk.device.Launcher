package rk.device.launcher.ui.managedata.rv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mundane on 2018/1/5 下午2:13
 */

public class SelectTypeDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int mDividerHeight;
    public SelectTypeDecoration(Context context, int colorResId) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        // 间隔线颜色
        mPaint.setColor(context.getResources().getColor(colorResId));
        mDividerHeight = 1; // 间隔线高度设置为1px
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
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
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.set(0, mDividerHeight, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, 0, mDividerHeight);
        }
    }
}
