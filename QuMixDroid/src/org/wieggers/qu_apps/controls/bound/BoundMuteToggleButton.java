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

import android.content.Context;
import android.util.AttributeSet;

public class BoundMuteToggleButton extends BoundMixToggleButton {
	public BoundMuteToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BoundMuteToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoundMuteToggleButton(Context context) {
		super(context);
		init();
	}

	private void init() {
		checkedValue = 0x7F;
		uncheckedValue = 0x3F;
	}
}
