package org.wieggers.qu_apps.controls.bound;

import org.wieggers.qu_apps.controls.Fader;
import org.wieggers.qu_apps.qu16.IMixValueListener;
import org.wieggers.qu_apps.qu16.Qu16_MixValue;

import android.content.Context;
import android.util.AttributeSet;

public class BoundMixFader extends Fader implements IMixValueListener {

	protected Qu16_MixValue mBoundMixValue;
	
	public BoundMixFader(Context context) {
		super(context);
	}

	public BoundMixFader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BoundMixFader(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void valueChanged(byte value) {
		setProgress(value, false);
	}
	
	@Override
	public void connect(Qu16_MixValue mixValue) {
		if (mBoundMixValue != null) {
			mBoundMixValue.removeListener(this);
		}
		
		mBoundMixValue = mixValue;
		if (mBoundMixValue != null) {
			mBoundMixValue.addListener(this);
			valueChanged(mixValue.getValue());
			setVisibility(VISIBLE);
		} else {
			setVisibility(INVISIBLE);
		}
	}
	
	@Override
	protected void onProgressChanged() {
		super.onProgressChanged();
		mBoundMixValue.setValue(this, (byte) this.getProgress());
	}
}
