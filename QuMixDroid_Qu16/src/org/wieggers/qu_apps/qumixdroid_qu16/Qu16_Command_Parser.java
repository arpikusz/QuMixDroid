package org.wieggers.qu_apps.qumixdroid_qu16;

import java.util.Arrays;
import java.util.LinkedList;

class Qu16_Command_Parser {
	
	LinkedList<IParserListener> mListeners;
	Object mListenerLock;

	parse_mode_enum mParseMode;
	parse_state_enum mState = parse_state_enum.next_command;
	
	byte[] current_command = new byte[2048];
	int current_command_length = 0;

	/**
	 * Construct a parser object, capable of processing individual commands for the Qu-16
	 * @param parseMode	2 Modes, because commands are differen when sent to, or received from the Qu-16
	 */
	public Qu16_Command_Parser(parse_mode_enum parseMode) {
		mParseMode = parseMode;
		
		mListenerLock = new Object();
		mListeners = new LinkedList<IParserListener>();
	}
	
	/**
	 * Analyses a stream of bytes and extracts individual commands 
	 * @param data 		Network data buffer 
	 * @param length	Network data buffer length
	 */
	public void Parse (byte[] data, int length)
	{
		for (int i = 0; i < length; ++i) {
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
				switch (mParseMode) {
				case from_qu_16:
					command_complete = (current_command_length == 11);
					break;
				case from_qu_pad:
					command_complete = (current_command_length == 8);
					break;
				}
				break;
			case in_mute_command:
				command_complete = (current_command_length == 4);
				break;
			case in_sysex_command:
				if (d == 0XF7)
					command_complete = true;
				break;
			}
			++current_command_length;
			if (command_complete) {
				
				synchronized (mListenerLock) {
					if (!mListeners.isEmpty()) {
						byte[] command = Arrays.copyOfRange(current_command, 0, current_command_length);
						
						for (IParserListener listener : mListeners) {
							listener.SingleCommand(command);
						}
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
	public void AddListener(IParserListener listener) {
		synchronized (mListenerLock) {
			mListeners.add(listener);
		}
	}
	
	/**
	 * External listeners can remove themselves from the listener list
	 * @param listener
	 */
	public void RemoveListener(IParserListener listener) {
		synchronized (mListenerLock) {
			mListeners.remove(listener);
		}
	}
	
	enum parse_state_enum
	{
		next_command,
		in_sysex_command,
		in_mute_command,
		in_channel_command
	}
}

enum parse_mode_enum
{
	from_qu_16,
	from_qu_pad
}
