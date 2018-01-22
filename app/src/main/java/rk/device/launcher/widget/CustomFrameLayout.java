package rk.device.launcher.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by mundane on 2018/1/22 下午4:32
 */

public class CustomFrameLayout extends FrameLayout {
    public CustomFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnFrameLayoutTouchListener{
        void onFrameLayoutTouched();
    }

    private OnFrameLayoutTouchListener mListener;

    public void setOnFrameLayoutTouchListener(OnFrameLayoutTouchListener onFrameLayoutTouchListener) {
        this.mListener = onFrameLayoutTouchListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mListener != null) {
            mListener.onFrameLayoutTouched();
        }
        return super.dispatchTouchEvent(ev);
    }
}
