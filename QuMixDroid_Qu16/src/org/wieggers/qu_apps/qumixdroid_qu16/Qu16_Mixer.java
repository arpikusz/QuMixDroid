package org.wieggers.qu_apps.qumixdroid_qu16;

import java.util.LinkedList;

import org.wieggers.qu_apps.qumixdroid_communication.Connected_Device;
import org.wieggers.qu_apps.qumixdroid_communication.IDeviceListener;

public class Qu16_Mixer implements IDeviceListener {
	
	private Connected_Device mDevice;
	private LinkedList<IMixerListener> mListeners;
	private Object mListenerLock;

	public Qu16_Mixer(String remoteIp, int port, boolean demoMode) {
		
		mListenerLock = new Object();
		
		if (!demoMode) {
			mDevice = new Connected_Device(remoteIp, port);
		} else {
			mDevice = null;
		}
	}
	
	public void Stop() {
		if (mDevice != null) {
			mDevice.Stop();
		}
	}

	public void AddListener(IMixerListener listener) {
		synchronized (mListenerLock) {
			mListeners.add(listener);
		}
	}
	
	public void RemoveListener(IMixerListener listener) {
		synchronized (mListenerLock) {
			mListeners.remove(listener);
		}
	}
	

	@Override
	public void receivedMessage(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorOccurred(Exception exception) {
		synchronized (mListenerLock) {
			for (IMixerListener listener : mListeners) {
				listener.equals(exception);
			}
		}

	}
}
