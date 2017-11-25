package rk.device.launcher.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import rk.device.launcher.R;
import rk.device.launcher.utils.SizeUtils;

public class BatteryView extends View {

    private int progress = 50;

    // 2 电量实际显示的只有中间部分，两边会多出一部分
    private int powerLeftPadding;

    // 4
    private int powerRightPadding;

    private int powerWidth;

    private Bitmap batteryBg;
    private Bitmap batteryLevel;

    Paint paint;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);

        batteryBg = BitmapFactory.decodeResource(getResources(), R.drawable.battery_empty);
        batteryLevel = BitmapFactory.decodeResource(getResources(), R.drawable.battery_full);
        powerLeftPadding = SizeUtils.dp2px(2);
        powerRightPadding = SizeUtils.dp2px(4);
        powerWidth = batteryBg.getWidth() - powerLeftPadding - powerRightPadding;
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.red));

        Log.i("BatteryView", "powerWidth:" + powerWidth + ",width:" + batteryBg.getWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // draw the background
        canvas.drawBitmap(batteryBg, 0, 0, null);
        // 绘制显示电量部分
        canvas.save();
        canvas.clipRect(0, 0, calculateClipRight(progress), getHeight());
        canvas.drawBitmap(batteryLevel, 0, 0, null);
        if (progress <= 20) {
            canvas.drawRect(8, 8, calculateClipRight(progress), getHeight() - 8, paint);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSize;
        int height = heightSize;
        if (MeasureSpec.AT_MOST == widthMode) {
            width = batteryBg.getWidth();
        }
        if (MeasureSpec.AT_MOST == heightMode) {
            height = batteryBg.getHeight();
        }
        setMeasuredDimension(width, height);

    }

    /**
     * @param progress between 0 and 100
     */
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            return;
        }
        this.progress = progress;
        postInvalidate();
    }

    private int calculateClipRight(int progress) {
        return powerWidth * progress / 100 + powerLeftPadding;
    }
}
