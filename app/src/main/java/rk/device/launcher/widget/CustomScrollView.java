package rk.device.launcher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import rk.device.launcher.utils.LogUtil;

/**
 * Created by mundane on 2018/2/27 上午10:37
 */

public class CustomScrollView extends ScrollView {
    private static final String TAG = "CustomScrollView";
    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 最小的滑动距离
     */
    private static final int SCROLLLIMIT = 10;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // t > oldt
        if (oldt> t && oldt - t > SCROLLLIMIT) {// 向下
            if (mScrollListener != null) {
                mScrollListener.onScrollDown();
            }
        } else if (oldt < t && t - oldt> SCROLLLIMIT) {// 向上
            LogUtil.d(TAG, "向上滚动");
        }
    }

    private ScrollListener mScrollListener;

    public void setScrollListener(ScrollListener listener) {
        mScrollListener = listener;
    }

    public interface ScrollListener{
        void onScrollDown();
    }
}
