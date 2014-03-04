package org.wieggers.qu_apps.qumixdroid;

import org.wieggers.qu_apps.qu16.Qu16_Mixer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MeteringFragment extends Fragment {

	private Qu16_Mixer mMixer;
	
	public MeteringFragment() {
		// TODO Auto-generated constructor stub
	}
	
	public void SetMixer(Qu16_Mixer mixer) {
		mMixer = mixer;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View meteringView = inflater.inflate(R.layout.fragment_meteringtest, container, false);

		for (int i = 0; i < 2000; ++i) {
			
		}
		
		return meteringView;
	}
}
