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
package org.wieggers.qu_apps.controls.bound;

import org.wieggers.qu_apps.qu16.Qu16_MixValue;
import org.wieggers.qu_apps.qu16.Qu16_MixValue.IMixValueListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class BoundMixToggleButton extends ToggleButton implements IMixValueListener, android.widget.CompoundButton.OnCheckedChangeListener {

	protected byte checkedValue;
	protected byte uncheckedValue;
	
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
		checkedValue = 0x01;
		uncheckedValue = 0x00;
		setOnCheckedChangeListener(this);
	}
	
	@Override
	public void connect(Qu16_MixValue mixValue) {
		if (mBoundMixValue != null) {
			mBoundMixValue.removeListener(this);
		}

		final int visible;
		mBoundMixValue = mixValue;
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
