package org.wieggers.qu_apps.qu16;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

class Qu16_Command_Parser {
	
	private ConcurrentLinkedQueue<IParserListener> mListeners;

	private parse_state_enum mState = parse_state_enum.next_command;
	
	private byte[] current_command = new byte[4000];
	private int current_command_length = 0;

	/**
	 * Construct a parser object, capable of processing individual commands for the Qu-16
	 * @param commandDirection	2 Modes, because commands are different when sent to, or received from the Qu-16
	 */
	public Qu16_Command_Parser() {
		mListeners = new ConcurrentLinkedQueue<IParserListener>();
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
					mState = parse_state_enum.in_channel_command;
					break;
				case (byte) 0xF0:
					mState = parse_state_enum.in_sysex_command;
					break;
				case (byte) 0xFE:
					// just ignore "keep alive" commands
					continue;
				default:
					// also ignore everything we don't understand
					continue;
				}
				break;
			case in_channel_command:
				command_complete = (current_command_length == 11);
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
				
				if (!mListeners.isEmpty()) {
					byte[] command = Arrays.copyOfRange(current_command, 0, current_command_length);
					
					for (IParserListener listener : mListeners) {
						listener.singleCommand(origin, command);
					}
				}

				mState = parse_state_enum.next_command;
				current_command_length = 0;
			}
		}
	}
	
	/**
	 * External listeners can subscribe to events using this method
	 * @param listener
	 */
	public void addListener(IParserListener listener) {
		mListeners.add(listener);
	}
	
	/**
	 * External listeners can remove themselves from the listener list
	 * @param listener
	 */
	public void removeListener(IParserListener listener) {
		mListeners.remove(listener);
	}
	
	private enum parse_state_enum
	{
		next_command,
		in_sysex_command,
		in_mute_command,
		in_channel_command
	}
}