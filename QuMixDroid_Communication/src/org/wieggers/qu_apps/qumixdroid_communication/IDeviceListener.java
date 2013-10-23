package org.wieggers.qu_apps.qumixdroid_communication;

public interface IDeviceListener {
	void receivedMessage(byte[] message);
	void errorOccurred(Exception exception);
}
