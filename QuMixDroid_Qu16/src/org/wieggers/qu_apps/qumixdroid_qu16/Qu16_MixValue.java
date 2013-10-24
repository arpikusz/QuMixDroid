package org.wieggers.qu_apps.qumixdroid_qu16;

import java.util.LinkedList;

public class Qu16_MixValue {
	private Qu16_Commands mCommand;
	private Qu16_Channels mChannel;
	private Qu16_Buses mBus;
	private Qu16_GEQ_Frequenxcies mFreq;
	private byte mValue;	
	private mixValueMode mMode;
	
	private LinkedList<IMixValueListener> mListeners;
	private Object mListenerLock;

	
	public Qu16_MixValue(Qu16_Commands command, Qu16_Channels channel, Qu16_Buses bus, byte value) {
		mCommand = command;
		mChannel = channel;
		mBus = bus;
		mValue = value;
		mMode = mixValueMode.channelValue;
		
		mListeners = new LinkedList<IMixValueListener>();
		mListenerLock = new Object();
	}
	
	public Qu16_MixValue(Qu16_Commands command, Qu16_Channels channel, Qu16_GEQ_Frequenxcies freq, byte value) {
		mCommand = command;
		mChannel = channel;
		mFreq = freq;
		mValue = value;
		mMode = mixValueMode.geqFreqValue;	
	}
	
	public Qu16_MixValue(Qu16_Channels channel, byte value) {
		mChannel = channel;
		mValue = value;
		mMode = mixValueMode.muteValue;
	}
	
	public Qu16_MixValue(Object origin, Qu16_Command_Direction direction, byte[] data) {
		setCommand(origin, direction, data, true);		
	}
	
	public static String getKey(byte[] data) {
		switch ((int) data[0]) {
		case 0xB0: // channel_command
			return "chn_" + Byte.toString(data[2]) + "_" + Byte.toString(data[5]) + "_" + Byte.toString(data[11]);
		case 0x90: // mute command
			return "mute_" + Byte.toString(data[1]);			
		}
		
		return null;
	}
	
	public void setCommand(Object origin, Qu16_Command_Direction direction, byte[] data) {
		setCommand(origin, direction, data, false);
	}

	public byte[] getCommand(Qu16_Command_Direction direction) {
		
		switch (mMode) {
		case channelValue:
			switch (direction) {
			case to_qu_16:
				return new byte[] {
						(byte) 0xB0, 0x63, mChannel.getValue(), 0x62, mCommand.getValue(), 0x06, mValue, 0x26, mBus.getValue() 
				};
			case from_qu_16:
				return new byte[] {
						(byte) 0xB0, 0x63, mChannel.getValue(), (byte) 0xB0, 0x62, mCommand.getValue(), (byte) 0xB0, 0x06, mValue, (byte) 0xB0, 0x26, mBus.getValue() 
				};
			}
			break;
		case geqFreqValue:
			switch (direction) {
			case to_qu_16:
				return new byte[] {
						(byte) 0xB0, 0x63, mChannel.getValue(), 0x62, mCommand.getValue(), 0x06, mValue, 0x26, mFreq.getValue() 
				};
			case from_qu_16:
				return new byte[] {
						(byte) 0xB0, 0x63, mChannel.getValue(), (byte) 0xB0, 0x62, mCommand.getValue(), (byte) 0xB0, 0x06, mValue, (byte) 0xB0, 0x26, mFreq.getValue() 
				};
			}
			break;
		case muteValue:
			return new byte[] {
					(byte) 0x90, mChannel.getValue(), mValue, mChannel.getValue(), (byte) 0x00
			};			
		}
		
		
		return null;
	}
	
	public byte getValue() {
		return mValue;
	}
	
	public void setValue(Object origin, byte value) {
		if (mValue != value) {
			mValue = value;
			synchronized (mListenerLock) {
				for (IMixValueListener listener : mListeners) {
					listener.valueChanged(this, origin, value);
				}
			}
		}
	}
	
	public void addListener(IMixValueListener listener) {
		synchronized (mListenerLock) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(IMixValueListener listener) {
		synchronized (mListenerLock) {
			mListeners.remove(listener);
		}
	}
	
	private void setCommand(Object origin, Qu16_Command_Direction direction, byte[] data, Boolean fromConstructor) {
		switch ((int) data[0]) {
		case 0xB0:
			switch (direction) {
			case to_qu_16:
				mChannel = Qu16_Channels.fromValue(data[2]);
				mCommand = Qu16_Commands.fromValue(data[4]);

				if (mCommand == Qu16_Commands.GEQ) {
					mMode = mixValueMode.geqFreqValue;
					mFreq = Qu16_GEQ_Frequenxcies.fromValue(data[8]);
				} else {
					mMode = mixValueMode.channelValue;
					mBus = Qu16_Buses.fromValue(data[8]);
				}
				setValue(origin, data[6]);
				break;
			case from_qu_16:
				mChannel = Qu16_Channels.fromValue(data[2]);
				mCommand = Qu16_Commands.fromValue(data[5]);

				if (mCommand == Qu16_Commands.GEQ) {
					mMode = mixValueMode.geqFreqValue;
					mFreq = Qu16_GEQ_Frequenxcies.fromValue(data[11]);
				} else {
					mMode = mixValueMode.channelValue;
					mBus = Qu16_Buses.fromValue(data[11]);
				}
				setValue(origin, data[8]);
				break;
			}
			break;
		case 0x90:
			mChannel = Qu16_Channels.fromValue(data[2]);
			mMode = mixValueMode.muteValue;
			setValue(origin, data[2]);
		}
	}
	
	private enum mixValueMode {
		channelValue,
		geqFreqValue,
		muteValue;
	}
}
