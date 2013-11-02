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
	
	public static Qu16_Input_Channels MasterChannel(Qu16_VX_Buses bus) {
		
		switch (bus) {
		case Mix_1: return Qu16_Input_Channels.Mix_1;
		case Mix_2: return Qu16_Input_Channels.Mix_2;
		case Mix_3: return Qu16_Input_Channels.Mix_3;
		case Mix_4: return Qu16_Input_Channels.Mix_4;
		case Mix_5_6: return Qu16_Input_Channels.Mix_5_6;
		case Mix_7_8: return Qu16_Input_Channels.Mix_7_8;
		case Mix_9_10: return Qu16_Input_Channels.Mix_9_10;
		default: return Qu16_Input_Channels.LR;		
		}
	}
	
	public static Qu16_Id_Parameters Assign_Command(Qu16_VX_Buses bus) {
		switch (bus) {
		case LR: return Qu16_Id_Parameters.Chn_Assign_LR_Sw;
		default: return Qu16_Id_Parameters.Chn_Assign_Mix_Sw;
		}
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
