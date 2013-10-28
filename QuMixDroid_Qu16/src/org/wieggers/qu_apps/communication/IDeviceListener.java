package org.wieggers.qu_apps.communication;

public interface IDeviceListener {
	void receivedMessage(byte[] message);
	void errorOccurred(Exception exception);
}
