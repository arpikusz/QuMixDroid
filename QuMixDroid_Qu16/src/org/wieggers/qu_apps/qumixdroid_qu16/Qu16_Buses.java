package org.wieggers.qu_apps.qumixdroid_qu16;

public enum Qu16_Buses {
	Mix_1(0x00),
	Mix_2(0x01),
	Mix_3(0x02),
	Mix_4(0x03),
	Mix_5_6(0x04),
	Mix_7_8(0x05),
	Mix_9_10(0x06),
	
	LR(0x07),
	
	FX1(0x10),
	FX2(0x11);

	@SuppressWarnings("unused")
	private int mValue;
	Qu16_Buses(int value) {
		mValue = value;
	}
}
