/*******************************************************************************
 * Copyright (c) 2013 george wieggers.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     george wieggers - initial API and implementation
 ******************************************************************************/
package org.wieggers.qu_apps.controls;

import java.util.HashMap;
import java.util.Map;

import org.wieggers.qu_apps.qumixdroid.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Fader extends View {

	private int mMaxValue, mProgress;
	private String mChannelName;
	
	private static final int mMargin = 50;
	private static final int mDesiredWidth = 30;
	private static final int mDesiredHeight = 300;

	private static Bitmap mKnobBmp = null;
	private static int mKnobWidth, mKnobHeight;

	private int mCenterX, mFaderKnobRangeY;
	private int mFaderKnobX1, mFaderKnobX2, mFaderKnobY1, mFaderKnobY2, mFaderKnobMinY1, mFaderKnobMaxY1; // fader knob coordinates
	private int mLevelX1, mLevelX2; // db scale X coordinates

	private double mProgressPercentage;
	private Paint mPaint;
	private Boolean mIsActive;

	private static final HashMap<Integer, String> faderLevels;
	static {
		HashMap<Integer, String> initMap = new HashMap<Integer, String>();
		initMap.put(0x7F, "10");
		initMap.put(0x72, "5");
		initMap.put(0x60, "0");
		initMap.put(0x4D, "-5");
		initMap.put(0x3D, "-10");
		initMap.put(0x2E, "-20");
		initMap.put(0x1F, "-30");
		initMap.put(0x0F, "-40");
		initMap.put(0, "-∞");
		faderLevels = initMap;
	}
	
	public Fader(Context context) {
		super(context);
		init(null, 0);
	}

	public Fader(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public Fader(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.Fader, defStyle, 0);

		mChannelName = a.getString(R.styleable.Fader_channel_name);
		setMaxValue(a.getInt(R.styleable.Fader_max, 127));
		setProgress(a.getInt(R.styleable.Fader_progress, 0), false);
		a.recycle();

		mIsActive = false;

		mFaderKnobRangeY = 1;		

		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize((float)12.0);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mKnobBmp == null) {
			mKnobBmp = BitmapFactory.decodeResource (getResources(), R.drawable.fader_knob);
			mKnobWidth = mKnobBmp.getWidth();
			mKnobHeight = mKnobBmp.getHeight();
		}

		mCenterX = (getWidth() / 2); // + 8; 
		mFaderKnobX1 = mCenterX - mKnobWidth / 2;			// left value of fader knob
		mFaderKnobX2 = mCenterX + mKnobWidth / 2;																
		
		mFaderKnobMinY1 = mMargin - (mKnobHeight / 2);
		mFaderKnobMaxY1 = getHeight() - mMargin - (mKnobHeight / 2);		// max value for top position of fader knob
		mFaderKnobRangeY = mFaderKnobMaxY1 - mFaderKnobMinY1;	// max possible sliding range for fader
		
		mLevelX1 = mCenterX - (mKnobWidth / 2) - 2;			// left value of DB scale lines
		mLevelX2 = mCenterX + (mKnobWidth / 2);				// right value of DB scale lines

		// draw the channel name
		mPaint.setColor(Color.WHITE);
		mPaint.setAlpha(255);
		
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize((float)16.0);
		
		if (mIsActive) {
			int value = mProgress + 8;
			while (value >= 0) {
				if (faderLevels.containsKey(value)) {
					String level = faderLevels.get(value);
					canvas.drawText(level, mCenterX, 20, mPaint);
					canvas.drawText(level, mCenterX, getHeight() - 20, mPaint);
					break;
				}
				--value;
			}
		} else if (mChannelName != null) {			
			canvas.drawText (mChannelName, mCenterX, 20, mPaint);
		}

		mPaint.setTextSize((float)12.0);

		// draw the DB scale with corresponding text labels
		mPaint.setTextAlign(Paint.Align.RIGHT);
		for(Map.Entry<Integer, String> entry : faderLevels.entrySet()) {
			
			double factorLevel = (double)entry.getKey() / (double)mMaxValue;
			int levelY = mFaderKnobMaxY1 - (int)(factorLevel * (double)mFaderKnobRangeY) + (mKnobHeight / 2);

			canvas.drawRect (mLevelX1, levelY - 1, mLevelX2, levelY, mPaint);
			//canvas.drawText (entry.getValue(), mLevelX1 - 4, levelY + 3, mPaint);
		}
		

		// draw the long vertical rectangle (in which our virtual fader can slide)
		canvas.drawRect(mCenterX - 3, mMargin, mCenterX + 3, getHeight() - mMargin, mPaint);

		if (!mIsActive) { // when not presssed, fill the vertical rectangle black 
			mPaint.setColor(Color.BLACK); 		
			canvas.drawRect(mCenterX - 2, mMargin + 1, mCenterX + 2, getHeight() - mMargin - 1, mPaint);
		}
		// so when pressed, the vertical rectangle lights up in white

		// determine position of the fader knob
		mFaderKnobY1 = mFaderKnobMaxY1 - (int)(mProgressPercentage * (double)mFaderKnobRangeY);
		mFaderKnobY2 = mFaderKnobY1 + mKnobHeight;
		
		if (!mIsActive)
			mPaint.setAlpha(190);
		// and draw it
		canvas.drawBitmap (mKnobBmp, mFaderKnobX1, mFaderKnobY1, mPaint);
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
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
	}	
	
	int startX, startY;
	int mixerValueStart;
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		
		if (!isEnabled())
			return false;

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int)e.getX ();
			startY = (int)e.getY ();
			mIsActive = (startX >= mFaderKnobX1
				&& startX <= mFaderKnobX2
				&& startY >= mFaderKnobY1
				&& startY <= mFaderKnobY2);
			
			if (mIsActive)
				getParent().requestDisallowInterceptTouchEvent(true);
			
			mixerValueStart = mProgress;
			safeInvalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsActive) {
				int newY = (int)e.getY ();
				int diffY = startY - newY;

				float factor = (float)diffY / (float)mFaderKnobRangeY;
				int mixerValueDelta = (int) (factor * (float) mMaxValue);
				int newMixerValue = mixerValueStart + mixerValueDelta;
				if (newMixerValue < 0)
					newMixerValue = 0;
				if (newMixerValue > mMaxValue)
					newMixerValue = mMaxValue;

				setProgress(newMixerValue, true);
				safeInvalidate ();
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsActive = false;
			getParent().requestDisallowInterceptTouchEvent(false);
			safeInvalidate ();
			break;
		}		
		
		return true;
	}
	
	public String getChannelName() {
		return mChannelName;
	}

	public void setChannelName(String mChannelName) {
		this.mChannelName = mChannelName;
		safeInvalidate();
	}

	public int getMaxValue() {
		return mMaxValue;
	}

	public void setMaxValue(int mMaxValue) {
		if (mMaxValue <= 0)	
			throw new IllegalArgumentException("MaxValue should be greater than zero");
		
		this.mMaxValue = mMaxValue;
		safeInvalidate();
	}
	
	public int getProgress() {
		return mProgress;
	}

	public void setProgress(int mProgress, Boolean triggerEvent) {
		this.mProgress = mProgress;
		mProgressPercentage = (double)mProgress / (double)getMaxValue();		
		safeInvalidate();
		if (triggerEvent)
			onProgressChanged();
	}
	
	private void safeInvalidate() {
		this.post(new Runnable() {
			
			@Override
			public void run() {
				Fader.this.invalidate();				
			}
		});
	}
	
	protected void onProgressChanged() {
	
	}
}
