package org.wieggers.qu_apps.qumixdroid_qu16;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.wieggers.qu_apps.qumixdroid_communication.Connected_Device;
import org.wieggers.qu_apps.qumixdroid_communication.IDeviceListener;

import android.content.Context;

/**
 * Qu16_Mixer is a "virtual" mixer which holds an active "scene" in memory, corresponding with the Qu-16 it is connected to
 * @author george wieggers
 *
 */
public class Qu16_Mixer implements IDeviceListener, IMixValueListener, IParserListener {
	
	private Connected_Device mDevice;
	private Qu16_Command_Parser mParser;
	private LinkedList<IMixerListener> mListeners;
	private Object mListenerLock;
	private HashMap<String, Qu16_MixValue> mMixValues;
		
	/**
	 * Construct a "virtual" mixer
	 * @param remoteIp	IP address of Qu-16
	 * @param port		Port of Qu-16
	 * @param demoMode	The virtual mixer can be initialized with some fake data, so the app can be used in demo mode
	 * @throws IOException 
	 */
	public Qu16_Mixer(Context context, String remoteIp, int port, boolean demoMode) throws IOException {
		
		mListenerLock = new Object();
		mListeners = new LinkedList<IMixerListener>();
		mParser = new Qu16_Command_Parser(Qu16_Command_Direction.from_qu_16);
		mMixValues = new HashMap<String, Qu16_MixValue>();
		
		//AssetManager assetManager = context.getAssets();
		//readScene(assetManager.open("qu16_init_scene.txt"));
		
		if (!demoMode) {
			mDevice = new Connected_Device(remoteIp, port);
		} else {
			mDevice = null;
		}
	}
	
	public void stop() {
		if (mDevice != null) {
			mDevice.stop();
		}
		synchronized (mListenerLock) {
			mListeners.clear();
		}
	}

	public void addListener(IMixerListener listener) {
		synchronized (mListenerLock) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(IMixerListener listener) {
		synchronized (mListenerLock) {
			mListeners.remove(listener);
		}
	}
	

	@Override
	public void receivedMessage(byte[] message) {
		if (mParser != null)
			mParser.parse(message);
		
	}

	@Override
	public void errorOccurred(Exception exception) {
		synchronized (mListenerLock) {
			for (IMixerListener listener : mListeners) {
				listener.equals(exception);
			}
		}

	}

	@Override
	public void singleCommand(byte[] data) {

		String key = Qu16_MixValue.getKey(data);
		if (!mMixValues.containsKey(key)) {
			Qu16_MixValue newValue = new Qu16_MixValue(this, Qu16_Command_Direction.from_qu_16, data);
			mMixValues.put(key, newValue);
		} else {
			mMixValues.get(key).setCommand(this, Qu16_Command_Direction.from_qu_16, data);
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
		}
	}
	
	public void writeScene(OutputStream os) throws IOException
	{
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(os));
		
		for (HashMap.Entry<String, Qu16_MixValue> mixValue : mMixValues.entrySet()) {
			byte[] singleCmd = mixValue.getValue().getCommand(Qu16_Command_Direction.from_qu_16);
			
			String line = Arrays.toString(singleCmd);
			line = line.replaceAll("[\\[|\\]| ]", ""); // [1, 2, 3, 4] ==> 1,2,3,4
			
			w.write(line);
			w.write("\n");
		}		
	}
}
