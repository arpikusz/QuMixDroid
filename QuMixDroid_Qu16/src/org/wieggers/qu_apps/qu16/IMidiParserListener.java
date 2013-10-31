package org.wieggers.qu_apps.qu16;

interface IMidiParserListener {
	void singleMidiCommand(Object origin, byte[] data);
}
