package org.wieggers.qu_apps.controls.bound;

import org.wieggers.qu_apps.controls.RotaryKnob;
import org.wieggers.qu_apps.qu16.IMixValueListener;
import org.wieggers.qu_apps.qu16.Qu16_MixValue;

import android.content.Context;
import android.util.AttributeSet;

public class BoundMixRotaryKnob extends RotaryKnob implements IMixValueListener {

	protected Qu16_MixValue mBoundMixValue;

	public BoundMixRotaryKnob(Context context) {
		super(context);
		initialize();
	}

	public BoundMixRotaryKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public BoundMixRotaryKnob(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize()
	{
		setMaxValue((byte)74);
	}
	
	@Override
	public void connect(Qu16_MixValue mixValue) {
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
	public void valueChanged(final byte value) {
		this.post(new Runnable() {
			
			@Override
			public void run() {
				setProgress(value);				
			}
		});
	}

	@Override
	protected void onRotaryValueChanged(int value) {
		super.onRotaryValueChanged(value);
		if (mBoundMixValue != null)
			mBoundMixValue.setValue(this, (byte) this.getProgress());
	}
}
