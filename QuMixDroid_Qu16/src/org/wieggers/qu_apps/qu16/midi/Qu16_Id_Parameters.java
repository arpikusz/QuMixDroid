package org.wieggers.qu_apps.qu16.midi;

public enum Qu16_Id_Parameters {
	Chn_Output_LR(0x17),
	Chn_Output_Mix(0x20),
	
	Chn_Output_Mix2(0x21),
	Chn_Output_Mix3(0x22),
	Chn_Output_Mix4(0x23),
	Chn_Output_Mix5_6(0x24),
	Chn_Output_Mix7_8(0x25),
	Chn_Output_Mix9_10(0x26),
	Chn_Output_FX1(0x30),
	Chn_Output_FX2(0x31),

	Chn_Pan(0x16),
	Chn_Assign_LR_Sw(0x18),
	Chn_Assign_Mix_Sw(0x55),
	Chn_Pre_Post_Sw(0x50),
	Chn_PAFL_Sw(0x51),
	
	Input_USB_Source_Sw(0x12),
	Input_Polarity_Sw(0x6A),
	Input_Phantom_48V_Sw(0x69),
	Input_Gain_Mono(0x19),
	Input_Gain_Stereo(0x54),
	Input_USB_Trim(0x52),
	
	Delay_Sw(0x6D),
	Delay(0x6C),

	HPF_Sw(0x14),
	HPF(0x13),

	Gate_Sw(0x46),
	Gate_Attack(0x41),
	Gate_Release(0x42),
	Gate_Hold(0x43),
	Gate_Threshold(0x44),
	Gate_Depth(0x45),

	PEQ_Sw(0x11),
	PEQ_1_Gain(0x01),
	PEQ_1_Freq(0x02),
	PEQ_1_Q(0x03),
	PEQ_1_Unknown(0x04),
	PEQ_2_Gain(0x05),
	PEQ_2_Freq(0x06),
	PEQ_2_Q(0x07),
	PEQ_2_Unknown(0x08),
	PEQ_3_Gain(0x09),
	PEQ_3_Freq(0x0A),
	PEQ_3_Q(0x0B),
	PEQ_3_Unknown(0x0C),
	PEQ_4_Gain(0x0D),
	PEQ_4_Freq(0x0E),
	PEQ_4_Q(0x0F),
	PEQ_4_Unknown(0x10),

	Comp_Sw(0x68),
	Comp_Type(0x61),
	Comp_Attack(0x62),
	Comp_Release(0x63),
	Comp_Soft_knee(0x64),
	Comp_Ratio(0x65),
	Comp_Threshold(0x66),
	Comp_Gain(0x67),


	GEQ(0x70),
	GEQ_Sw(0x71),

	Unknown_40(0x40),
	Unknown_56(0x56),
	Unknown_57(0x57),
	Unknown_58(0x58),
	Unknown_59(0x59),
	Unknown_5A(0x5A),
	Unknown_6B(0x6B);
	
	private byte mValue;
	
	public byte getValue() {
		return mValue;
	}
	
	Qu16_Id_Parameters(int value) {
		mValue = (byte) value;
	}	
	
	public static Qu16_Id_Parameters fromValue(byte value) {
		for (Qu16_Id_Parameters command : Qu16_Id_Parameters.values()) {
			if (command.mValue == value) {
				return command;
			}
		}
		throw new IllegalArgumentException ("Cannot convert " + Byte.toString(value) + " to command");
	}
}
