package org.wieggers.qu_apps.qu16;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.wieggers.qu_apps.communication.Connected_Device;
import org.wieggers.qu_apps.communication.IDeviceListener;
import org.wieggers.qu_apps.qu16.midi.Qu16_GEQ_Bands;
import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;

import android.util.Log;

/**
 * Qu16_Mixer is a "virtual" mixer which holds an active "scene" in memory, corresponding with the Qu-16 it is connected to
 * @author george wieggers
 *
 */
public class Qu16_Mixer implements IDeviceListener, IMixValueMidiListener, IMidiParserListener {

	private static final byte[] requestAllSettings = {(byte) 0xF0, 0x00, 0x00, 0x1A, 0x50, 0x11, 0x01, 0x00, 0x7F, 0x10, 0x01, (byte) 0xF7};
	//private static final byte[] requestMetering = {(byte) 0xF0, 0x00, 0x00, 0x1A, 0x50, 0x11, 0x01, 0x00, 0x00, 0x12, 0x01, (byte) 0xF7};
	
	private static final String mTag = "Qu16_Mixer";
	
	private static final byte Mute = (byte) 0x90;
	private static final byte Channel = (byte) 0xB0;
	
	private Connected_Device mDevice;
	private Qu16_Midi_Parser mParser;
	private IMixerListener mListener;
	private Boolean mDemoMode;
	private String mRemoteIp;
	private int mRemotePort;

	// access is as follows:
	// mMixValues[0x90][fader][0][0] for mute command
	// mMixValues[0xB0][fader][command][bus/freq] for channel command
	private ConcurrentHashMap<Byte,ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>>> mMixValues;

		
	/**
	 * Construct a "virtual" mixer
	 * @param remoteIp	IP address of Qu-16
	 * @param port		Port of Qu-16
	 * @param demoMode	The virtual mixer can be initialized with some fake data, so the app can be used in demo mode
	 * @throws IOException 
	 */
	public Qu16_Mixer(String remoteIp, int port, boolean demoMode, IMixerListener parent) {
		
		mListener = parent;

		mParser = new Qu16_Midi_Parser(this);		
		mMixValues = new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte,ConcurrentHashMap<Byte,ConcurrentHashMap<Byte,Qu16_MixValue>>>>();
		
		mRemoteIp = remoteIp;
		mRemotePort = port;
		mDemoMode = demoMode;					
	}
	
	public void start() {
		if (!mDemoMode) {
			mDevice = new Connected_Device(mRemoteIp, mRemotePort, this);
			mDevice.start();
			mDevice.send(requestAllSettings); // request all current mixer settings on startup
		} else {
			mDevice = null;
		}		
	}	
	
	public void stop() {
		if (mDevice != null) {
			mDevice.stop();
		}
	}

	@Override
	public void receivedMessage(byte[] message) {
		if (mParser != null)
			mParser.parse(mDevice, message);		
	}

	@Override
	public void errorOccurred(Exception exception) {
		mListener.errorOccurred(exception);
	}

	@Override
	public void singleMidiCommand(Object origin, byte[] data) {

		Log.d(mTag, "Received: " + Arrays.toString(data));
		
		byte[] key = Qu16_MixValue.getKey(data);
		if (key != null) {			
			getMixValue(key[0], key[1], key[2], key[3], true).setCommand(origin, data);
		}
	}

	// Several connect overloads, to connect safely to several mix values
	
	// when only channel is given as a parameter, connect to a mute value
	public void connect(IMixValueListener listener, Qu16_Input_Channels mute_channel) { 
		Qu16_MixValue muteValue = getMixValue(Qu16_Mixer.Mute, mute_channel.getValue(), (byte) 0, (byte) 0, false);
		muteValue.addListener(listener);
	}
	
	// if channel and bus is given, it's a channel NRPN command (but not GEQ)
	public void connect(IMixValueListener listener, Qu16_Input_Channels channel, Qu16_Id_Parameters command, Qu16_VX_Buses bus) {
		if (command == Qu16_Id_Parameters.GEQ)
			throw new IllegalArgumentException("Cannot connect GEQ command in combination with a Qu16_VX_Buses type");
		
		Qu16_MixValue channelValue = getMixValue(Qu16_Mixer.Channel , channel.getValue(), command.getValue(), bus.getValue(), false);
		channelValue.addListener(listener);
	}

	public void connect(IMixValueListener listener, Qu16_Input_Channels channel, Qu16_Id_Parameters command, Qu16_GEQ_Bands band) {
		if (command != Qu16_Id_Parameters.GEQ)
			throw new IllegalArgumentException("Cannot connect this command in combination with a Qu16_GEQ_Bands type");
		
		Qu16_MixValue channelValue = getMixValue(Qu16_Mixer.Channel, channel.getValue(), command.getValue(), band.getValue(), false);
		channelValue.addListener(listener);
	}

	private Qu16_MixValue getMixValue(byte key0, byte key1, byte key2, byte key3, boolean create) {
		
		if (!mMixValues.containsKey(key0)) {
			if (! create)
				return null;
			
			mMixValues.put(key0, new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>>());
		}

		ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>> mixValues0 = mMixValues.get(key0); 
		
		if (!mixValues0.containsKey(key1)) {
			if (! create)
				return null;
			mixValues0.put(key1, new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>());
		}
		ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>> mixValues1 = mixValues0.get(key1);

		if (!mixValues1.containsKey(key2)) {
			if (! create)
				return null;
			mixValues1.put(key2, new ConcurrentHashMap<Byte, Qu16_MixValue>());
		}
		ConcurrentHashMap<Byte, Qu16_MixValue> mixValues2 = mixValues1.get(key2);
		
		if (!mixValues2.containsKey(key3)) {
			if (! create)
				return null;

			Qu16_MixValue newValue = new Qu16_MixValue(this);
			mixValues2.put(key3, newValue);
		}

		return mixValues2.get(key3);
	}
	
	@Override
	public void valueChanged(Qu16_MixValue sender, Object origin) {
		if (origin != mDevice) {
			if (mDevice != null) {
				mDevice.send(sender.getCommand());
			}
		}		
	}
	
	public void readScene(InputStream is) throws NumberFormatException, IOException 
	{	
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		
		String line;
		while ((line = r.readLine()) != null)
		{
			String[] parts = line.split(",");	
			 
			byte[] bytes = new byte[parts.length];
			int count = 0;
			for(String str : parts)
			{
			    bytes[count++] = Byte.parseByte(str);
			}			
			singleMidiCommand(null, bytes);
		}
	}
	
	public void writeScene(OutputStream os) throws IOException
	{
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter w = new BufferedWriter(osw);
		
		int i = 0;
		for (Entry<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>>> mixValues1 : mMixValues.entrySet()) {
			for (Entry<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>> mixValues2 : mixValues1.getValue().entrySet()) {
				for (Entry<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>> mixValues3 : mixValues2.getValue().entrySet()) {
					for (Entry<Byte, Qu16_MixValue> mixValue4 : mixValues3.getValue().entrySet()) {
						++i;
												
						byte[] cmd = mixValue4.getValue().getCommand();
						if (cmd == null)
							continue;

						String strLine = Arrays.toString(cmd).replaceAll("[\\[|\\]| ]", "");
						Log.d(mTag, "Scene line " + i + ": " + strLine);
						
						if (w != null) {
							w.write(strLine);
							w.write("\n");
						}
					}
				}
			}			
		}
		w.close();
		osw.close();
	}
}
