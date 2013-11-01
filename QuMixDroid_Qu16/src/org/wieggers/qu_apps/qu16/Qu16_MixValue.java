package org.wieggers.qu_apps.qu16;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Qu16_MixValue {

	private byte[] mCommand;
	private byte mValue;
	private ConcurrentLinkedQueue<IMixValueListener> mListeners;
	private IMidiListener mParent;

	public Qu16_MixValue(IMidiListener parent) {
		mParent = parent;
		mListeners = new ConcurrentLinkedQueue<IMixValueListener>();
		mCommand = null;
		mValue = 0;
	}	
	
	public byte getValue() {
		return mValue;
	}
	
	public void setValue(Object origin, byte value) {
		if (mValue != value) {
			mValue = value;
			
			if (mCommand != null) {
				switch (mCommand[0]) {
				case (byte) 0x90: // mute command:
					mCommand[1] = value;
					mCommand[4] = value;
					break;
				case (byte) 0xB0: // channel command:
					mCommand[8] = value;
					break;
				}

				mParent.singleMidiCommand(this, origin, mCommand);
			}
			
			for (IMixValueListener listener : mListeners) {
				listener.valueChanged(value);
			}
			
		}
	}
	
	public void addListener(IMixValueListener listener) {
		mListeners.add(listener);
	}
	
	public void removeListener(IMixValueListener listener) {
		mListeners.remove(listener);
	}
		
	static byte[] getKey(byte[] data) {
		switch (data[0]) {
		case (byte) 0xB0: // channel_command
			return new byte[] { data[0], data[2], data[5], data[11] };
		case (byte) 0x90: // mute command
			return new byte[] { data[0], data[1], 0, 0 };
		}
		
		return null;
	}

	byte[] getCommand() {
		return mCommand;
	}

	void setCommand(Object origin, byte[] data) {
		mCommand = data;
		
		if (mCommand != null) {
			switch (mCommand[0]) {
			case (byte) 0x90: // mute command:
				setValue(origin, data[1]);
				break;
			case (byte) 0xB0: // channel command:
				setValue(origin, data[8]);
				break;
			}
		}
	}	
}
