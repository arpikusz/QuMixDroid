package org.wieggers.qu_apps.qumixdroid_communication;

public interface IDeviceListener {
	public void receivedMessage(byte[] message);
	public void errorOccurred(Exception exception);
}
