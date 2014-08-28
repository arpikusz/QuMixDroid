package org.wieggers.qu_apps.controls.bound;

import org.wieggers.qu_apps.qu16.Qu16_MeteringValues.IMeteringValueListener;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class BoundMixMeteringSquare extends View implements IMeteringValueListener {

	private int mIndex;
	
	public BoundMixMeteringSquare(Context context) {
		super(context);
	}

	public BoundMixMeteringSquare(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BoundMixMeteringSquare(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}	
	
	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	@Override
	public void ValueChanged(final byte newValue) {
		this.post(new Runnable() {

			public void run() {
				int rgbValue = newValue * 2;
				if (rgbValue > 255)
					rgbValue = 255;
				
				setBackgroundColor(Color.rgb(rgbValue, rgbValue, rgbValue));
				invalidate();				
			}
		});
	}
}
