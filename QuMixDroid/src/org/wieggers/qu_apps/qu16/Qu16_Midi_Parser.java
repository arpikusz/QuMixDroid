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
package org.wieggers.qu_apps.qu16;

import java.util.Arrays;

import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;

class Qu16_Midi_Parser {
	
	//private static final String mTag = "Qu16_Midi_Parser";
	private IMidiListener mParent;

	private parse_state_enum mState = parse_state_enum.next_command;
	
	private byte[] current_command = new byte[4000];
	private int current_command_length = 0;

	/**
	 * Construct a parser object, capable of processing individual commands for the Qu-16
	 * @param commandDirection	2 Modes, because commands are different when sent to, or received from the Qu-16
	 */
	public Qu16_Midi_Parser(IMidiListener parent) {
		mParent = parent;
	}
	
	/**
	 * Analyzes a stream of bytes and extracts individual commands 
	 * @param data 		Network data buffer 
	 * @param length	Network data buffer length
	 */
	public void parse (Object origin, byte[] data)
	{
		for (int i = 0; i < data.length; ++i) {
			boolean command_complete = false;
			byte d = data [i];
			current_command [current_command_length] = d;
			switch (mState) {
			case next_command:
				switch (d) {
				case (byte) 0x90: // start mute
					mState = parse_state_enum.in_mute_command;
					break;
				case (byte) 0xB0: // start channel command
					mState = parse_state_enum.start_channel_command;
					break;
				case (byte) 0xF0:
					mState = parse_state_enum.in_sysex_command;
					break;
				case (byte) 0xFE:
					// just ignore "keep alive" commands
					continue;
				default:
					// also ignore everything we don't understand
					//Log.d(mTag, Qu16_Mixer.bytesToHex(new byte[] { d }));
					continue;
				}
				break;
			case start_channel_command:
				switch (d) {
				case (byte) 0x63: // 0xB0 - 0x63 detected, proceed to next state "in channel command"
					mState = parse_state_enum.in_channel_command;
					break;
				default:
					mState = parse_state_enum.next_command; // unknown sequence, ignore and wait for next command
					current_command_length = 0;
					continue;
				}
				break;
			case in_channel_command:
				command_complete = (current_command_length == 11);
				if (current_command_length > 6) {
					if (current_command[3] == (byte) 0xB0) {
						command_complete = (current_command_length == 11);
					} else {
						command_complete = (current_command_length == 8);
					}						
				}
				break;
			case in_mute_command:
				command_complete = (current_command_length == 4);
				break;
			case in_sysex_command:
				if (d == (byte) 0xF7)
					command_complete = true;
				break;
			}
			++current_command_length;
			if (command_complete) {								
				byte[] command; 

				if (mState == parse_state_enum.in_channel_command && current_command_length == 8) { // Qu-pad sends too short NRPN commands					
					// so fix them by inserting proper 0xB0 values
					command = new byte[] {
							current_command[0],
							current_command[1],
							current_command[2],
							(byte) 0xB0,
							current_command[3],
							current_command[4],
							(byte) 0xB0,
							current_command[5],
							current_command[6],
							(byte) 0xB0,
							current_command[7],
							current_command[8]
					};
					
					// Qu-pad doesn't follow the specs for the output_mix command, correct it over here
					if (   current_command[4] >= Qu16_Id_Parameters.Chn_Output_Mix.getValue() 
						&& current_command[4] <= Qu16_Id_Parameters.Chn_Output_Mix9_10.getValue()) {
						current_command[4] = Qu16_Id_Parameters.Chn_Output_Mix.getValue();
					}
				} else {
					command = Arrays.copyOfRange(current_command, 0, current_command_length);
				}
									
				mParent.singleMidiCommand(this, origin, command);

				mState = parse_state_enum.next_command;
				current_command_length = 0;
			}
		}
	}
		
	private enum parse_state_enum
	{
		next_command,
		in_sysex_command,
		in_mute_command,
		start_channel_command,
		in_channel_command
	}
	
	public interface IMidiListener {
		void singleMidiCommand(Object sender, Object origin, byte[] midiCommand);
	}
}
