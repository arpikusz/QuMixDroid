package org.wieggers.qu_apps.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RotaryKnob extends View {

	private static final int mDesiredWidth = 40;
	private static final int mDesiredHeight = 40;

	private float angle = 180f;
	private float resultAngle = 180f;
	private byte mMaxValue = 127;
	private Paint mPaint;
	private RectF mRect;
	private int mSize;
	private int mCenterW;
	private int mCenterH;

	public RotaryKnob(Context context) {
		super(context);
		initialize();
	}

	public RotaryKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public RotaryKnob(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private float getTheta(float x, float y) {
		float sx = x - (getWidth() / 2.0f);
		float sy = y - (getHeight() / 2.0f);

		float length = (float) Math.sqrt(sx * sx + sy * sy);
		float nx = sx / length;
		float ny = sy / length;
		float theta = (float) Math.atan2(ny, nx);

		final float rad2deg = (float) (180.0 / Math.PI);
		float thetaDeg = theta * rad2deg;

		return (thetaDeg < 0) ? thetaDeg + 360.0f : thetaDeg;
	}

	private void initialize() {
		mPaint = new Paint();
		mPaint.setColor(Color.YELLOW);
		mPaint.setStrokeWidth(2.0f);
		
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX(0);
				float y = event.getY(0);
				float theta = getTheta(x, y);

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_DOWN:
					getParent().requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_MOVE:
					invalidate();
					angle = theta;
					resultAngle = angle;
					if (resultAngle >= 90f && resultAngle <= 135f) resultAngle = 135f;
					if (resultAngle <= 90f && resultAngle >= 45f) resultAngle = 45f;
					
					onRotaryValueChanged(getProgress());
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					getParent().requestDisallowInterceptTouchEvent(false);
					break;
				}
				return true;
			}
		});
	}
	
	// dummy function which is overridden in child class
	protected void onRotaryValueChanged(int value)
	{
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		//Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			//Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			//Can't be bigger than...
			width = Math.min(mDesiredWidth, widthSize);
		} else {
			//Be whatever you want
			width = mDesiredWidth;
		}

		//Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			//Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			//Can't be bigger than...
			height = Math.min(mDesiredHeight, heightSize);
		} else {
			//Be whatever you want
			height = mDesiredHeight;
		}

		//MUST CALL THIS
		setMeasuredDimension(width, height);
	}
	
	protected void onDraw(Canvas c) {
		c.rotate(90 + resultAngle, mCenterW, mCenterH);
		mPaint.setStyle(Paint.Style.STROKE);
		c.drawCircle(mCenterW, mCenterH, (mSize / 2) - 2, mPaint);
		
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		c.drawRoundRect(mRect, 2, 2, mPaint);
		super.onDraw(c);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mSize = Math.min(w, h);
		mCenterW = w/2;
		mCenterH = h/2;
		mRect = new RectF(mCenterW - 2, 7, mCenterW + 2, mCenterH);
	}
	
	private float transformAngle(float input) {
		float result = input - 135f;
		if (result < 0) result += 360f;
		return result;
	}
	
	public void setMaxValue(byte value)
	{
		mMaxValue = value;
	}
	
	public byte getMaxValue()
	{
		return mMaxValue;
	}
	
	public void setProgress(byte progress)
	{
		float perc = ((float) progress / (float) mMaxValue);
		if (perc < 0f) perc = 0f;
		if (perc > 1f) perc = 1f;		
		angle = 135f + (perc * 270);
		if (angle > 360f) angle -= 360f;		
		resultAngle = angle;
		invalidate();
	}
	
	public byte getProgress()
	{
		float angle = transformAngle(resultAngle);
		byte result = (byte) (mMaxValue * angle / 270f);
		return result;
	}	
}