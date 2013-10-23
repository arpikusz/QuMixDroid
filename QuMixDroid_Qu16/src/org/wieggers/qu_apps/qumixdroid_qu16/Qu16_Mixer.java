package org.wieggers.qu_apps.qumixdroid_qu16;

import java.util.LinkedList;

import org.wieggers.qu_apps.qumixdroid_communication.Connected_Device;
import org.wieggers.qu_apps.qumixdroid_communication.IDeviceListener;

/**
 * Qu16_Mixer is a "virtual" mixer which holds an active "scene" in memory, corresponding with the Qu-16 it is connected to
 * @author george wieggers
 *
 */
public class Qu16_Mixer implements IDeviceListener, IParserListener {
	
	private Connected_Device mDevice;
	private Qu16_Command_Parser mParser;
	private LinkedList<IMixerListener> mListeners;
	private Object mListenerLock;
		
	/**
	 * Construct a "virtual" mixer
	 * @param remoteIp	IP address of Qu-16
	 * @param port		Port of Qu-16
	 * @param demoMode	The virtual mixer can be initialized with some fake data, so the app can be used in demo mode
	 */
	public Qu16_Mixer(String remoteIp, int port, boolean demoMode) {
		
		mListenerLock = new Object();
		mListeners = new LinkedList<IMixerListener>();
		mParser = new Qu16_Command_Parser(Qu16_Command_Direction.from_qu_16);
				
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

		
	}
}
