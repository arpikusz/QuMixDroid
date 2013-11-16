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
package org.wieggers.qu_apps.qu16.midi;

import java.util.HashMap;

import android.annotation.SuppressLint;

@SuppressLint("UseSparseArrays")
public class Qu16_VA_Ranges {
	
	public static final HashMap<Integer, String> Fader_Send;
	public static final HashMap<Integer, String> Local_Gain;
	public static final HashMap<Integer, String> dSnake_Gain;
	public static final HashMap<Integer, String> Compressor_Type;

	static {
		HashMap<Integer, String> init_Fader = new HashMap<Integer, String>();
		init_Fader.put(0x7F, "10");
		init_Fader.put(0x74, "5");
		init_Fader.put(0x6B, "0");
		init_Fader.put(0x61, "5");
		init_Fader.put(0x57, "10");
		init_Fader.put(0x43, "20");
		init_Fader.put(0x2F, "30");
		init_Fader.put(0x1B, "40");
		init_Fader.put(0, "∞");
		Fader_Send = init_Fader;

		HashMap<Integer, String> init_Gain = new HashMap<Integer, String>();
		init_Gain.put(0x7F, "60");
		init_Gain.put(0x6B, "50");
		init_Gain.put(0x57, "40");
		init_Gain.put(0x44, "30");
		init_Gain.put(0x30, "20");
		init_Gain.put(0x1D, "10");
		init_Gain.put(0x13, "5");
		init_Gain.put(0x0A, "0");
		init_Gain.put(0x00, "5");
		Local_Gain = init_Gain;

		HashMap<Integer, String> init_Snake_Gain = new HashMap<Integer, String>();
		init_Snake_Gain.put(0x7F, "60");
		init_Snake_Gain.put(0x67, "50");
		init_Snake_Gain.put(0x50, "40");
		init_Snake_Gain.put(0x45, "35");
		init_Snake_Gain.put(0x39, "30");
		init_Snake_Gain.put(0x2E, "25");
		init_Snake_Gain.put(0x22, "20");
		init_Snake_Gain.put(0x0B, "10");
		init_Snake_Gain.put(0x00, "5");
		dSnake_Gain = init_Snake_Gain;

		HashMap<Integer, String> init_Compressor = new HashMap<Integer, String>();
		init_Compressor.put(0x00, "Manual Peak");
		init_Compressor.put(0x01, "Manual RMS");
		init_Compressor.put(0x02, "Auto Slow Opto");
		init_Compressor.put(0x03, "Auto Punchbag");
		Compressor_Type = init_Compressor;
	}
}
