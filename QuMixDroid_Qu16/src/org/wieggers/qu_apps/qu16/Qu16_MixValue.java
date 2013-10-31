package org.wieggers.qu_apps.qu16;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_GEQ_Bands;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;

public class Qu16_MixValue {
	private Qu16_Id_Parameters mCommand;
	private Qu16_Input_Channels mChannel;
	private Qu16_VX_Buses mBus;
	private Qu16_GEQ_Bands mFreq;
	private byte mValue;	
	private mixValueMode mMode;
	
	private ConcurrentLinkedQueue<IMixValueListener> mListeners;
	private IMixValueMidiListener mMidiListener;

	public Qu16_MixValue(IMixValueMidiListener parent) {
		init(parent);
		mMode = mixValueMode.unknown;
	}
	
	/*
	public Qu16_MixValue(IMixValueMidiListener parent, Object origin, byte[] data) {
		init(parent);
		setCommand(origin, data, true);
	}
	*/
	
	private void init(IMixValueMidiListener parent)
	{
		mMidiListener = parent;
		mListeners = new ConcurrentLinkedQueue<IMixValueListener>();
		mValue = 0;
	}
	
	public static byte[] getKey(byte[] data) {
		switch (data[0]) {
		case (byte) 0xB0: // channel_command
			return new byte[] { data[0], data[2], data[5], data[11] };
		case (byte) 0x90: // mute command
			return new byte[] { data[0], data[1], 0, 0 };
		}
		
		return null;
	}
	
	public void setCommand(Object origin, byte[] data) {
		setCommand(origin, data, false);
	}

	public byte[] getCommand() {
		
		switch (mMode) {
		case channelValue:
			return new byte[] {
					(byte) 0xB0, 0x63, mChannel.getValue(), (byte) 0xB0, 0x62, mCommand.getValue(), (byte) 0xB0, 0x06, mValue, (byte) 0xB0, 0x26, mBus.getValue() 
			};
		case geqFreqValue:
			return new byte[] {
					(byte) 0xB0, 0x63, mChannel.getValue(), (byte) 0xB0, 0x62, mCommand.getValue(), (byte) 0xB0, 0x06, mValue, (byte) 0xB0, 0x26, mFreq.getValue() 
			};
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
				listener.valueChanged(value);
			}
			mMidiListener.valueChanged(this, origin);
		}
	}
	
	public void addListener(IMixValueListener listener) {
		mListeners.add(listener);
	}
	
	public void removeListener(IMixValueListener listener) {
		mListeners.remove(listener);
	}
	
	private void setCommand(Object origin, byte[] data, Boolean fromConstructor) {
		switch (data[0]) {
		case (byte) 0xB0:
			mChannel = Qu16_Input_Channels.fromValue(data[2]);
			mCommand = Qu16_Id_Parameters.fromValue(data[5]);

			if (mCommand == Qu16_Id_Parameters.GEQ) {
				mMode = mixValueMode.geqFreqValue;
				mFreq = Qu16_GEQ_Bands.fromValue(data[11]);
			} else {
				mMode = mixValueMode.channelValue;
				mBus = Qu16_VX_Buses.fromValue(data[11]);
			}
			if (!fromConstructor)
				setValue(origin, data[8]);
			break;
		case (byte) 0x90:
			mChannel = Qu16_Input_Channels.fromValue(data[1]);
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
