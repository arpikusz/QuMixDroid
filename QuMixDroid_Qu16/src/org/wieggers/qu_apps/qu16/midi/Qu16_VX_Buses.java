package org.wieggers.qu_apps.qu16.midi;

public enum Qu16_VX_Buses {
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

	private byte mValue;
	
	public byte getValue() {
		return mValue;
	}
	
	Qu16_VX_Buses(int value) {
		mValue = (byte) value;
	}
	
	public static Qu16_VX_Buses fromValue(byte value) {
		for (Qu16_VX_Buses bus : Qu16_VX_Buses.values()) {
			if (bus.mValue == value) {
				return bus;
			}
		}
		throw new IllegalArgumentException ("Cannot convert " + Byte.toString(value) + " to bus");
	}
}
