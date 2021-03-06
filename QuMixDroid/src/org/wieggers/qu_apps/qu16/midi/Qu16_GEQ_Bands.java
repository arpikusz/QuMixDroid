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

public enum Qu16_GEQ_Bands {
	f00031(0x00),
	f00040(0x01),
	f00050(0x02),
	f00063(0x03),
	f00080(0x04),
	f00100(0x05),
	f00125(0x06),
	f00160(0x07),
	f00200(0x08),
	f00250(0x09),
	f00315(0x0A),
	f00400(0x0B),
	f00500(0x0C),
	f00630(0x0D),
	f00800(0x0E),
	f01000(0x0F),
	f01250(0x10),
	f01600(0x11),
	f02000(0x12),
	f02500(0x13),
	f03150(0x14),
	f04000(0x15),
	f05000(0x16),
	f06300(0x17),
	f08000(0x18),
	f10000(0x19),
	f12500(0x1A),
	f16000(0x1B);
	
	private byte mValue;
	
	public byte getValue() {
		return mValue;
	}
	
	Qu16_GEQ_Bands(int value) {
		mValue = (byte) value;
	}	

	public static Qu16_GEQ_Bands fromValue(byte value) {
		for (Qu16_GEQ_Bands freq : Qu16_GEQ_Bands.values()) {
			if (freq.mValue == value) {
				return freq;
			}
		}
		throw new IllegalArgumentException ("Cannot convert " + Byte.toString(value) + " to GEQ frequency");
	}
}
