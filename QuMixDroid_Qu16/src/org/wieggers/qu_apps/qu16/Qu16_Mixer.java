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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.wieggers.qu_apps.communication.Connected_Device;
import org.wieggers.qu_apps.communication.IDeviceListener;
import org.wieggers.qu_apps.qu16.midi.Qu16_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_GEQ_Bands;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;

import android.util.Log;

/**
 * Qu16_Mixer is a "virtual" mixer which holds an active "scene" in memory, corresponding with the Qu-16 it is connected to
 * @author george wieggers
 *
 */
public class Qu16_Mixer implements IDeviceListener, IMixValueListener, IParserListener {

	private static final byte[] requestAllSettings = {(byte) 0xF0, 0x00, 0x00, 0x1A, 0x50, 0x11, 0x01, 0x00, 0x7F, 0x10, 0x01, (byte) 0xF7};
	//private static final byte[] requestMetering = {(byte) 0xF0, 0x00, 0x00, 0x1A, 0x50, 0x11, 0x01, 0x00, 0x00, 0x12, 0x01, (byte) 0xF7};
	
	private static final String mTag = "Qu16_Mixer";
	
	public static final byte Mute = (byte) 0x90;
	public static final byte Channel = (byte) 0xB0;
	
	private Connected_Device mDevice;
	private Qu16_Command_Parser mParser;
	private ConcurrentLinkedQueue<IMixerListener> mListeners;
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
	public Qu16_Mixer(String remoteIp, int port, boolean demoMode) {
		
		mListeners = new ConcurrentLinkedQueue<IMixerListener>();

		mParser = new Qu16_Command_Parser();
		mParser.addListener(this);
		
		mMixValues = new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte,ConcurrentHashMap<Byte,ConcurrentHashMap<Byte,Qu16_MixValue>>>>();
		
		mRemoteIp = remoteIp;
		mRemotePort = port;
		mDemoMode = demoMode;					
	}
	
	public void start() {
		if (!mDemoMode) {
			mDevice = new Connected_Device(mRemoteIp, mRemotePort);
			mDevice.addListener(this);
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
		mListeners.clear();
	}

	public void addListener(IMixerListener listener) {
		mListeners.add(listener);
	}
	
	public void removeListener(IMixerListener listener) {
		mListeners.remove(listener);
	}
	

	@Override
	public void receivedMessage(byte[] message) {
		if (mParser != null)
			mParser.parse(this, message);
		
	}

	@Override
	public void errorOccurred(Exception exception) {
		for (IMixerListener listener : mListeners) {
			listener.errorOccurred(exception);
		}
	}

	@Override
	public void singleCommand(Object origin, byte[] data) {

		Log.d(mTag, "Received: " + Arrays.toString(data));
		
		byte[] key = Qu16_MixValue.getKey(data);
		if (key != null) {			
			getMixValue(key[0], key[1], key[2], key[3]).setCommand(origin, data);
		}
	}

	public Qu16_MixValue getMixValue(Qu16_Input_Channels channel, Qu16_Parameters command, Qu16_VX_Buses bus) {
		return getMixValue(Qu16_Mixer.Channel , channel.getValue(), command.getValue(), bus.getValue());
	}

	public Qu16_MixValue getMixValue(Qu16_Input_Channels channel, Qu16_Parameters command, Qu16_GEQ_Bands freq) {
		return getMixValue(Qu16_Mixer.Channel, channel.getValue(), command.getValue(), freq.getValue());
	}
	
	public Qu16_MixValue getMixValue(Qu16_Input_Channels mute_channel) {
		return getMixValue(Qu16_Mixer.Mute, mute_channel.getValue(), (byte) 0, (byte) 0);
	}

	public Qu16_MixValue getMixValue(byte mixer, int layer, int fader, Qu16_VX_Buses bus, Qu16_Parameters cmd) {
		
		byte key0 = mixer; // mute or channel command
		byte key1 = Qu16_Input_Channels.Mono_01.getValue();
		byte key2 = cmd.getValue();
		byte key3 = bus.getValue();

		Boolean isInputChannel = true;		
		Boolean supportsPan = false;
		Boolean supportsPrePost = true;
		Boolean supportsAssign = true;
		
		if (fader == 17) { // master fader
			key3 = (byte) Qu16_VX_Buses.LR.getValue();
			switch (bus) {
			case LR:
				key1 = Qu16_Input_Channels.LR.getValue();
				break;
			case FX1:
				key1 = Qu16_Input_Channels.FX_Send_1.getValue();
				break;
			case FX2:
				key1 = Qu16_Input_Channels.FX_Send_2.getValue();
				break;
			case Mix_1:
				key1 = Qu16_Input_Channels.Mix_1.getValue();
				break;
			case Mix_2:
				key1 = Qu16_Input_Channels.Mix_2.getValue();
				break;
			case Mix_3:
				key1 = Qu16_Input_Channels.Mix_3.getValue();
				break;
			case Mix_4:
				key1 = Qu16_Input_Channels.Mix_4.getValue();
				break;
			case Mix_5_6:
				key1 = Qu16_Input_Channels.Mix_5_6.getValue();
				break;
			case Mix_7_8:
				key1 = Qu16_Input_Channels.Mix_7_8.getValue();
				break;
			case Mix_9_10:
				key1 = Qu16_Input_Channels.Mix_9_10.getValue();
				break;
			}
		} else {
			switch (layer) {
			case 1:
				switch (fader) {
				case 1:
					key1 = Qu16_Input_Channels.Mono_01.getValue();
					break;
				case 2:
					key1 = Qu16_Input_Channels.Mono_02.getValue();
					break;
				case 3:
					key1 = Qu16_Input_Channels.Mono_03.getValue();
					break;
				case 4:
					key1 = Qu16_Input_Channels.Mono_04.getValue();
					break;
				case 5:
					key1 = Qu16_Input_Channels.Mono_05.getValue();
					break;
				case 6:
					key1 = Qu16_Input_Channels.Mono_06.getValue();
					break;
				case 7:
					key1 = Qu16_Input_Channels.Mono_07.getValue();
					break;
				case 8:
					key1 = Qu16_Input_Channels.Mono_08.getValue();
					break;
				case 9:
					key1 = Qu16_Input_Channels.Mono_09.getValue();
					break;
				case 10:
					key1 = Qu16_Input_Channels.Mono_10.getValue();
					break;
				case 11:
					key1 = Qu16_Input_Channels.Mono_11.getValue();
					break;
				case 12:
					key1 = Qu16_Input_Channels.Mono_12.getValue();
					break;
				case 13:
					key1 = Qu16_Input_Channels.Mono_13.getValue();
					break;
				case 14:
					key1 = Qu16_Input_Channels.Mono_14.getValue();
					break;
				case 15:
					key1 = Qu16_Input_Channels.Mono_15.getValue();
					break;
				case 16:
					key1 = Qu16_Input_Channels.Mono_16.getValue();
					break;
				}
				break;
			case 2:
				isInputChannel = (fader < 8);
				switch (fader) {
				case 1:
					key1 = Qu16_Input_Channels.Stereo_1.getValue();
					break;
				case 2:
					key1 = Qu16_Input_Channels.Stereo_2.getValue();
					break;
				case 3:
					key1 = Qu16_Input_Channels.Stereo_3.getValue();
					break;
				case 4:
					key1 = Qu16_Input_Channels.FX_Return_1.getValue();
					break;
				case 5:
					key1 = Qu16_Input_Channels.FX_Return_2.getValue();
					break;
				case 6:
					key1 = Qu16_Input_Channels.FX_Return_3.getValue();
					break;
				case 7:
					key1 = Qu16_Input_Channels.FX_Return_4.getValue();
					break;
				case 8:
					key1 = Qu16_Input_Channels.FX_Send_1.getValue();
					break;
				case 9:
					key1 = Qu16_Input_Channels.FX_Send_2.getValue();
					break;
				case 10:
					key1 = Qu16_Input_Channels.Mix_1.getValue();
					break;
				case 11:
					key1 = Qu16_Input_Channels.Mix_2.getValue();
					break;
				case 12:
					key1 = Qu16_Input_Channels.Mix_3.getValue();
					break;
				case 13:
					key1 = Qu16_Input_Channels.Mix_4.getValue();
					break;
				case 14:
					key1 = Qu16_Input_Channels.Mix_5_6.getValue();
					break;
				case 15:
					key1 = Qu16_Input_Channels.Mix_7_8.getValue();
					break;
				case 16:
					key1 = Qu16_Input_Channels.Mix_9_10.getValue();
					break;
				}
				break;
			}
		}
		
		if (mixer == Qu16_Mixer.Mute) {
			return getMixValue(key0, key1, (byte) 0, (byte) 0);
		} else {

			switch (bus) {
			case Mix_5_6:
			case Mix_7_8:
			case Mix_9_10:
				supportsPan = true;
				break;
			case LR:
				supportsPan = true;
				supportsPrePost = false;
				break;
			default:
				supportsPan = false;
				break;
			}
			
			if (! isInputChannel) {
				key3 = Qu16_VX_Buses.LR.getValue();
				supportsPan = false;
				supportsPrePost = false;
				supportsAssign = false;
			}
			
			if ((! supportsPan) && cmd == Qu16_Parameters.Chn_Pan)
				return null;			
			
			if ((! supportsPrePost) && cmd == Qu16_Parameters.Chn_Pre_Post_Sw)
				return null;

			if ((! supportsAssign) && 
					(cmd == Qu16_Parameters.Chn_Assign_LR_Sw) || (cmd == Qu16_Parameters.Chn_Assign_Mix_Sw))
				return null;
			
			
			if ((cmd == Qu16_Parameters.Chn_Assign_LR_Sw) || (cmd == Qu16_Parameters.Chn_Assign_Mix_Sw)) {
				if (bus == Qu16_VX_Buses.LR) {
					key2 = Qu16_Parameters.Chn_Assign_LR_Sw.getValue();
				} else {
					key2 = Qu16_Parameters.Chn_Assign_Mix_Sw.getValue();
				}
			}
			
			// 			  mute/chan, fader,cmd,  bus
			return getMixValue(key0, key1, key2, key3);
		}
	}
	
	private Qu16_MixValue getMixValue(byte key0, byte key1, byte key2, byte key3) {
		
		if (!mMixValues.containsKey(key0)) {
			mMixValues.put(key0, new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>>());
		}

		ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>> mixValues0 = mMixValues.get(key0); 
		
		if (!mixValues0.containsKey(key1)) {
			mixValues0.put(key1, new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>());
		}
		ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>> mixValues1 = mixValues0.get(key1);

		if (!mixValues1.containsKey(key2)) {
			mixValues1.put(key2, new ConcurrentHashMap<Byte, Qu16_MixValue>());
		}
		ConcurrentHashMap<Byte, Qu16_MixValue> mixValues2 = mixValues1.get(key2);
		
		if (!mixValues2.containsKey(key3)) {
			Qu16_MixValue newValue = new Qu16_MixValue();
			newValue.addListener(this);
			mixValues2.put(key3, newValue);
		}

		return mixValues2.get(key3);
	}
	
	@Override
	public void valueChanged(Qu16_MixValue sender, Object origin, byte value) {
		if (origin != this) {
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
			singleCommand(null, bytes);
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
