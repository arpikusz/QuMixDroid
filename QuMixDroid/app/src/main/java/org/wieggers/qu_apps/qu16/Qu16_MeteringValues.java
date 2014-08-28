package org.wieggers.qu_apps.qu16;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Qu16_MeteringValues {

	byte[] mCurrentMeteringValues;

	ConcurrentHashMap<Integer, ConcurrentLinkedQueue<IMeteringValueListener>> mListeners;
	
	public Qu16_MeteringValues() {
		mListeners = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<IMeteringValueListener>>();
		for (int i = 0; i < 2000; ++i) {
			mListeners.put(i, new ConcurrentLinkedQueue<IMeteringValueListener>());
		}
		mCurrentMeteringValues = null;
	}

	public void addListener(int index, IMeteringValueListener listener) {
		mListeners.get(index).add(listener);
	}
	
	public void removeListener(int index, IMeteringValueListener listener) {
		mListeners.get(index).remove(listener);
	}

	public void NewValues(byte[] newMeteringValues) {
		for (int i = 0; i < newMeteringValues.length; ++i) {
			if (mCurrentMeteringValues == null 
					|| i > mCurrentMeteringValues.length
					|| mCurrentMeteringValues[i] != newMeteringValues[i]) {
				for (IMeteringValueListener listener: mListeners.get(i)) {
					listener.ValueChanged(newMeteringValues[i]);
				}
			}
		}
		mCurrentMeteringValues = newMeteringValues;
	}
	
	public interface IMeteringValueListener {
		public void ValueChanged(byte newValue);
	}
}
