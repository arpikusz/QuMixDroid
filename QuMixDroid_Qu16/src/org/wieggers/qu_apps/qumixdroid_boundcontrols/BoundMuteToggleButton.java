package org.wieggers.qu_apps.qumixdroid_boundcontrols;

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
