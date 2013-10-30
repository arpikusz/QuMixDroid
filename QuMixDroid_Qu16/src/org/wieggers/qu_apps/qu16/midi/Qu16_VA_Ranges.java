package org.wieggers.qu_apps.qu16.midi;

import java.util.HashMap;

public class Qu16_VA_Ranges {
	public static final HashMap<Integer, String> Fader_Send;
	static {
		HashMap<Integer, String> initMap = new HashMap<Integer, String>();
		initMap.put(0x7F, "10");
		initMap.put(0x74, "5");
		initMap.put(0x6B, "0");
		initMap.put(0x61, "5");
		initMap.put(0x57, "10");
		initMap.put(0x43, "20");
		initMap.put(0x2F, "30");
		initMap.put(0x1B, "40");
		initMap.put(0, "∞");
		Fader_Send = initMap;
	}
	
	public static final HashMap<Integer, String> Local_Gain;
	static {
		HashMap<Integer, String> initMap = new HashMap<Integer, String>();
		initMap.put(0x7F, "60");
		initMap.put(0x6B, "50");
		initMap.put(0x57, "40");
		initMap.put(0x44, "30");
		initMap.put(0x30, "20");
		initMap.put(0x1D, "10");
		initMap.put(0x13, "5");
		initMap.put(0x0A, "0");
		initMap.put(0x00, "5");
		Local_Gain = initMap;
	}
	
	public static final HashMap<Integer, String> dSnake_Gain;
	static {
		HashMap<Integer, String> initMap = new HashMap<Integer, String>();
		initMap.put(0x7F, "60");
		initMap.put(0x67, "50");
		initMap.put(0x50, "40");
		initMap.put(0x45, "35");
		initMap.put(0x39, "30");
		initMap.put(0x2E, "25");
		initMap.put(0x22, "20");
		initMap.put(0x0B, "10");
		initMap.put(0x00, "5");
		dSnake_Gain = initMap;
	}

	public static final HashMap<Integer, String> Compressor_Type;
	static {
		HashMap<Integer, String> initMap = new HashMap<Integer, String>();
		initMap.put(0x00, "Manual Peak");
		initMap.put(0x01, "Manual RMS");
		initMap.put(0x02, "Auto Slow Opto");
		initMap.put(0x03, "Auto Punchbag");
		Compressor_Type = initMap;
	}

}
