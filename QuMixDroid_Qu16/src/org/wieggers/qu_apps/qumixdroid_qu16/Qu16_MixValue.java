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
	
	public void setValue(byte value) {
		if (mValue != value) {
			mValue = value;
			synchronized (mListenerLock) {
				for (IMixValueListener listener : mListeners) {
					listener.valueChanged(value);
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
	
	private enum mixValueMode {
		channelValue,
		geqFreqValue,
		muteValue;
	}
}
