package org.wieggers.qu_apps.qu16;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Qu16_MixValue {
	private Qu16_Commands mCommand;
	private Qu16_Channels mChannel;
	private Qu16_Buses mBus;
	private Qu16_GEQ_Frequenxcies mFreq;
	private byte mValue;	
	private mixValueMode mMode;
	
	private ConcurrentLinkedQueue<IMixValueListener> mListeners;

	public Qu16_MixValue() {
		init();
		mMode = mixValueMode.unknown;
	}
	
	public Qu16_MixValue(Object origin, Qu16_Command_Direction direction, byte[] data) {
		init();
		setCommand(origin, direction, data, true);
	}
	
	private void init()
	{
		mListeners = new ConcurrentLinkedQueue<IMixValueListener>();
		mValue = 0;
	}
	
	public static byte[] getKey(Qu16_Command_Direction direction, byte[] data) {
		switch (data[0]) {
		case (byte) 0xB0: // channel_command
			switch (direction) {
			case from_qu_16:
				return new byte[] { data[0], data[2], data[5], data[11] };
			case to_qu_16:
				return new byte[] { data[0], data[2], data[4], data[8] };
			}
		case (byte) 0x90: // mute command
			return new byte[] { data[0], data[1], 0, 0 };
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
						(byte) 0xB0, 0x63, mChannel.getValue(), 0x62, mCommand.getValueForBus(mBus), 0x06, mValue, 0x26, mBus.getValue() 
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
		case unknown:
			return null;
		}		
		
		return null;
	}
	
	public byte getValue() {
		return mValue;
	}
	
	public void setValue(Object origin, byte value) {
		if (mValue != value) {
			mValue = value;
			for (IMixValueListener listener : mListeners) {
				listener.valueChanged(this, origin, value);
			}
		}
	}
	
	public void addListener(IMixValueListener listener) {
		mListeners.add(listener);
	}
	
	public void removeListener(IMixValueListener listener) {
		mListeners.remove(listener);
	}
	
	private void setCommand(Object origin, Qu16_Command_Direction direction, byte[] data, Boolean fromConstructor) {
		switch (data[0]) {
		case (byte) 0xB0:
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
				if (!fromConstructor)
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
				if (!fromConstructor)
					setValue(origin, data[8]);
				break;
			}
			break;
		case (byte) 0x90:
			mChannel = Qu16_Channels.fromValue(data[1]);
			mMode = mixValueMode.muteValue;
			if (!fromConstructor)
				setValue(origin, data[2]);
		}
	}
	
	private enum mixValueMode {
		unknown,
		channelValue,
		geqFreqValue,
		muteValue;
	}
}
