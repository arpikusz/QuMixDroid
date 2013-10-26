package org.wieggers.qu_apps.qumixdroid_qu16;

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

import org.wieggers.qu_apps.qumixdroid_communication.Connected_Device;
import org.wieggers.qu_apps.qumixdroid_communication.IDeviceListener;

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

		mParser = new Qu16_Command_Parser(Qu16_Command_Direction.from_qu_16);
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
		
		byte[] key = Qu16_MixValue.getKey(Qu16_Command_Direction.from_qu_16, data);
		if (key != null) {
			
			if (!mMixValues.containsKey(key[0])) {
				mMixValues.put(key[0], new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>>());
			}

			ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>> mixValues0 = mMixValues.get(key[0]); 
			
			if (!mixValues0.containsKey(key[1])) {
				mixValues0.put(key[1], new ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>>());
			}
			ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Qu16_MixValue>> mixValues1 = mixValues0.get(key[1]);

			if (!mixValues1.containsKey(key[2])) {
				mixValues1.put(key[2], new ConcurrentHashMap<Byte, Qu16_MixValue>());
			}
			ConcurrentHashMap<Byte, Qu16_MixValue> mixValues2 = mixValues1.get(key[2]);
			
			if (!mixValues2.containsKey(key[3])) {
				Qu16_MixValue newValue = new Qu16_MixValue(origin, Qu16_Command_Direction.from_qu_16, data);
				newValue.addListener(this);
				mixValues2.put(key[3], newValue);
			}
			mixValues2.get(key[3]).setCommand(origin, Qu16_Command_Direction.from_qu_16, data);
		}
	}

	@Override
	public void valueChanged(Qu16_MixValue sender, Object origin, byte value) {
		if (origin != this) {
			if (mDevice != null) {
				mDevice.send(sender.getCommand(Qu16_Command_Direction.to_qu_16));
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
						
						byte[] cmd = mixValue4.getValue().getCommand(Qu16_Command_Direction.from_qu_16);

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
