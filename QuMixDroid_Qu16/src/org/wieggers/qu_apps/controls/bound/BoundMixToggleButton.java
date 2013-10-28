package org.wieggers.qu_apps.controls.bound;

import org.wieggers.qu_apps.qu16.IMixValueListener;
import org.wieggers.qu_apps.qu16.Qu16_MixValue;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class BoundMixToggleButton extends ToggleButton implements IBoundControl, IMixValueListener, android.widget.CompoundButton.OnCheckedChangeListener {

	protected byte checkedValue = 0x01;
	protected byte uncheckedValue = 0x00;
	
	protected Qu16_MixValue mBoundMixValue;
	
	public BoundMixToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BoundMixToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoundMixToggleButton(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setOnCheckedChangeListener(this);
	}

	@Override
	public void Connect(Qu16_MixValue mixValue) {
		if (mBoundMixValue != null) {
			mBoundMixValue.removeListener(this);
		}
		
		mBoundMixValue = mixValue;
		if (mBoundMixValue != null) {
			mBoundMixValue.addListener(this);
			valueChanged(mixValue, null, mixValue.getValue());
			setVisibility(VISIBLE);
		} else {
			setVisibility(INVISIBLE);
		}
	}
	
	@Override
	public void valueChanged(Qu16_MixValue sender, Object origin, final byte value) {
		
		this.post(new Runnable() {
			
			@Override
			public void run() {
				BoundMixToggleButton.this.setChecked(value == checkedValue);
				invalidate();				
			}
		});		
	}
		
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (mBoundMixValue != null) {
			mBoundMixValue.setValue(this, this.isChecked() ? checkedValue : uncheckedValue );
		}
	}

}
