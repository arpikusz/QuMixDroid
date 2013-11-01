package org.wieggers.qu_apps.qu16;

public interface IMidiListener {
	void singleMidiCommand(Object sender, Object origin, byte[] midiCommand);
}
