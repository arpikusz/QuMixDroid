package org.wieggers.qu_apps.qu16;

public interface IMixValueListener {
	void connect(Qu16_MixValue value);
	void valueChanged(byte value);
}
