package org.wieggers.qu_apps.qu16;

import java.util.HashMap;

import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qumixdroid_qu16.R;

public class Qu16_UI {
	public static final HashMap<Integer, HashMap<Integer, Qu16_Input_Channels>> Mixer_Layout;
	public static final HashMap<Byte, Integer> Channel_String_Ids;

	static {
		// Init mixer layout layers		
		HashMap<Integer, HashMap<Integer, Qu16_Input_Channels>> init_Layout = new HashMap<Integer, HashMap<Integer, Qu16_Input_Channels>>();
		init_Layout.put(2, new HashMap<Integer, Qu16_Input_Channels>());
		
		HashMap<Integer, Qu16_Input_Channels> layer1 = new HashMap<Integer, Qu16_Input_Channels>();
		HashMap<Integer, Qu16_Input_Channels> layer2 = new HashMap<Integer, Qu16_Input_Channels>();
		
		// Layer 1, 16 mono channels + L/R
		int idx = 1;
		for (byte i = Qu16_Input_Channels.Mono_01.getValue(); i <= Qu16_Input_Channels.Mono_16.getValue(); ++i) {
			layer1.put((int) idx, Qu16_Input_Channels.fromValue(i));
			++idx;
		}
		layer1.put(idx, Qu16_Input_Channels.LR);
		init_Layout.put(1, layer1);
		
		// Layer 2, 7 input and 9 output + L/R
		idx = 1;
		for (byte i = Qu16_Input_Channels.Stereo_1.getValue(); i <= Qu16_Input_Channels.Stereo_3.getValue(); ++i) {
			layer2.put((int) idx, Qu16_Input_Channels.fromValue(i));
			++idx;
		}
		for (byte i = Qu16_Input_Channels.FX_Return_1.getValue(); i <= Qu16_Input_Channels.FX_Return_4.getValue(); ++i) {
			layer2.put((int) idx, Qu16_Input_Channels.fromValue(i));
			++idx;
		}
		for (byte i = Qu16_Input_Channels.FX_Send_1.getValue(); i <= Qu16_Input_Channels.FX_Send_2.getValue(); ++i) {
			layer2.put((int) idx, Qu16_Input_Channels.fromValue(i));
			++idx;
		}
		for (byte i = Qu16_Input_Channels.Mix_1.getValue(); i <= Qu16_Input_Channels.Mix_9_10.getValue(); ++i) {
			layer2.put((int) idx, Qu16_Input_Channels.fromValue(i));
			++idx;
		}
		layer2.put(idx, Qu16_Input_Channels.LR);
		init_Layout.put(2, layer2);
		
		Mixer_Layout = init_Layout;
		
		HashMap<Byte, Integer> ids = new HashMap<Byte, Integer>(); 
		ids.put(Qu16_Input_Channels.Mono_01.getValue(), R.string.mono_01);
		ids.put(Qu16_Input_Channels.Mono_02.getValue(), R.string.mono_02);
		ids.put(Qu16_Input_Channels.Mono_03.getValue(), R.string.mono_03);
		ids.put(Qu16_Input_Channels.Mono_04.getValue(), R.string.mono_04);
		ids.put(Qu16_Input_Channels.Mono_05.getValue(), R.string.mono_05);
		ids.put(Qu16_Input_Channels.Mono_06.getValue(), R.string.mono_06);
		ids.put(Qu16_Input_Channels.Mono_07.getValue(), R.string.mono_07);
		ids.put(Qu16_Input_Channels.Mono_08.getValue(), R.string.mono_08);
		ids.put(Qu16_Input_Channels.Mono_09.getValue(), R.string.mono_09);
		ids.put(Qu16_Input_Channels.Mono_10.getValue(), R.string.mono_10);
		ids.put(Qu16_Input_Channels.Mono_11.getValue(), R.string.mono_11);
		ids.put(Qu16_Input_Channels.Mono_12.getValue(), R.string.mono_12);
		ids.put(Qu16_Input_Channels.Mono_13.getValue(), R.string.mono_13);
		ids.put(Qu16_Input_Channels.Mono_14.getValue(), R.string.mono_14);
		ids.put(Qu16_Input_Channels.Mono_15.getValue(), R.string.mono_15);
		ids.put(Qu16_Input_Channels.Mono_16.getValue(), R.string.mono_16);
		
		ids.put(Qu16_Input_Channels.Stereo_1.getValue(), R.string.stereo_1);
		ids.put(Qu16_Input_Channels.Stereo_2.getValue(), R.string.stereo_2);
		ids.put(Qu16_Input_Channels.Stereo_3.getValue(), R.string.stereo_3);

		ids.put(Qu16_Input_Channels.FX_Return_1.getValue(), R.string.fx_ret_1);
		ids.put(Qu16_Input_Channels.FX_Return_2.getValue(), R.string.fx_ret_2);
		ids.put(Qu16_Input_Channels.FX_Return_3.getValue(), R.string.fx_ret_3);
		ids.put(Qu16_Input_Channels.FX_Return_4.getValue(), R.string.fx_ret_4);

		ids.put(Qu16_Input_Channels.FX_Send_1.getValue(), R.string.fx_send_1);
		ids.put(Qu16_Input_Channels.FX_Send_2.getValue(), R.string.fx_send_2);

		ids.put(Qu16_Input_Channels.Mix_1.getValue(), R.string.mix_1);
		ids.put(Qu16_Input_Channels.Mix_2.getValue(), R.string.mix_2);
		ids.put(Qu16_Input_Channels.Mix_3.getValue(), R.string.mix_3);
		ids.put(Qu16_Input_Channels.Mix_4.getValue(), R.string.mix_4);
		ids.put(Qu16_Input_Channels.Mix_5_6.getValue(), R.string.mix_5_6);
		ids.put(Qu16_Input_Channels.Mix_7_8.getValue(), R.string.mix_7_8);
		ids.put(Qu16_Input_Channels.Mix_9_10.getValue(), R.string.mix_9_10);

		ids.put(Qu16_Input_Channels.LR.getValue(), R.string.lr);
		
		Channel_String_Ids = ids;
	}
}
