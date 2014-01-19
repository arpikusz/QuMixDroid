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

import org.wieggers.qu_apps.qu16.IMixValueListener;
import org.wieggers.qu_apps.qu16.Qu16_MixValue;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class BoundMixSeekbar extends SeekBar implements IMixValueListener, android.widget.SeekBar.OnSeekBarChangeListener { 

	protected Qu16_MixValue mBoundMixValue;

	public BoundMixSeekbar(Context context) {

		super(context);
		init();
	}

	public BoundMixSeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public BoundMixSeekbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setMax(0x4A);
		setOnSeekBarChangeListener(this);
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
	public void connect(Qu16_MixValue mixValue) {
		if (mBoundMixValue != null) {
			mBoundMixValue.removeListener(this);
		}
		
		final int visible;
		mBoundMixValue = mixValue;
		if (mBoundMixValue != null) {
			mBoundMixValue.addListener(this);
			setProgress(mixValue.getValue());
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
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			if (mBoundMixValue != null) {
				mBoundMixValue.setValue(this, (byte) progress );
			}
		}		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
