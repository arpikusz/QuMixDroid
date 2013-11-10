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
	public void valueChanged(final byte value) {
		this.post(new Runnable() {
			
			@Override
			public void run() {
				setProgress(value, false);				
			}
		});
	}
	
	@Override
	public void connect(final Qu16_MixValue mixValue) {
		if (mBoundMixValue != null) {
			mBoundMixValue.removeListener(this);
		}
		
		mBoundMixValue = mixValue;
		
		final int visible;
		if (mBoundMixValue != null) {
			mBoundMixValue.addListener(this);
			valueChanged(mixValue.getValue());
			visible = VISIBLE;
		} else {
			visible = INVISIBLE;
		}				

		this.post(new Runnable() {
			
			@Override
			public void run() {
				setVisibility(visible);
			}
		});

	}
	
	@Override
	protected void onProgressChanged() {
		super.onProgressChanged();
		if (mBoundMixValue != null)
			mBoundMixValue.setValue(this, (byte) this.getProgress());
	}
}
