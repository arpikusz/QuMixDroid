package org.wieggers.qu_apps.qu16.midi;

public enum Qu16_Input_Channels {
	Mono_01(0x20),
	Mono_02(0x21),
	Mono_03(0x22),
	Mono_04(0x23),
	Mono_05(0x24),
	Mono_06(0x25),
	Mono_07(0x26),
	Mono_08(0x27),
	Mono_09(0x28),
	Mono_10(0x29),
	Mono_11(0x2A),
	Mono_12(0x2B),
	Mono_13(0x2C),
	Mono_14(0x2D),
	Mono_15(0x2E),
	Mono_16(0x2F),

	Stereo_1(0x40),
	Stereo_2(0x41),
	Stereo_3(0x42),

	FX_Return_1(0x08),
	FX_Return_2(0x09),
	FX_Return_3(0x0A),
	FX_Return_4(0x0B),
	
	FX_Send_1(0x00),
	FX_Send_2(0x01),

	Mix_1(0x60),
	Mix_2(0x61),
	Mix_3(0x62),
	Mix_4(0x63),
	Mix_5_6(0x64),
	Mix_7_8(0x65),
	Mix_9_10(0x66),
	LR(0x67),

	Unknown_02(0x02),
	Unknown_03(0x03),
	
	Unknown_10(0x10),
	Unknown_11(0x11),
	Unknown_12(0x12),
	Unknown_13(0x13),

	Unknown_30(0x30),
	Unknown_31(0x31),
	Unknown_32(0x32),
	Unknown_33(0x33),
	Unknown_34(0x34),
	Unknown_35(0x35),
	Unknown_36(0x36),
	Unknown_37(0x37),
	Unknown_38(0x38),
	Unknown_39(0x39),
	Unknown_3A(0x3A),
	Unknown_3B(0x3B),
	Unknown_3C(0x3C),
	Unknown_3D(0x3D),
	Unknown_3E(0x3E),
	Unknown_3F(0x3F),

	Unknown_68(0x68),
	Unknown_69(0x69),
	Unknown_6A(0x6A),
	Unknown_6B(0x6B),
	Unknown_6C(0x6C),
	Unknown_6D(0x6D),
	Unknown_6E(0x6E),
	Unknown_6F(0x6F);
		
	private byte mValue;
	
	public byte getValue() {
		return mValue;
	}
	
	Qu16_Input_Channels(int value) {
		mValue = (byte) value;
	}
	
	public static Qu16_Input_Channels fromValue(byte value) {
		for (Qu16_Input_Channels channel : Qu16_Input_Channels.values()) {
			if (channel.mValue == value) {
				return channel;
			}
		}
		throw new IllegalArgumentException ("Cannot convert " + Byte.toString(value) + " to channel");
	}
}
