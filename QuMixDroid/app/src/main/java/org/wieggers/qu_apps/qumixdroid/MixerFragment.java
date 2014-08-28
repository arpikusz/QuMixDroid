/*******************************************************************************
 * Copyright (c) 2013 George Wieggers.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     George Wieggers - initial API and implementation
 ******************************************************************************/

package org.wieggers.qu_apps.qumixdroid;

import org.wieggers.qu_apps.controls.bound.BoundMixFader;
import org.wieggers.qu_apps.qu16.Qu16_MixValue.IMixValueListener;
import org.wieggers.qu_apps.qu16.Qu16_Mixer;
import org.wieggers.qu_apps.qu16.Qu16_Mixer.IMixerConnector;
import org.wieggers.qu_apps.qu16.Qu16_UI;
import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class MixerFragment extends Fragment implements OnCheckedChangeListener, OnItemSelectedListener, IMixerConnector {

	private int mCurrentLayer;
	private Qu16_Mixer mMixer;

	public MixerFragment() {
		// TODO Auto-generated constructor stub
	}

	public void SetMixer(Qu16_Mixer mixer) {
		mMixer = mixer;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View mixView = inflater.inflate(R.layout.fragment_main, container, false);
		
		Spinner spMix = (Spinner) mixView.findViewById(R.id.spMix);
		ToggleButton tbLayer1 = (ToggleButton) mixView.findViewById(R.id.tbLayer1);
		ToggleButton tbLayer2 = (ToggleButton) mixView.findViewById(R.id.tbLayer2);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.mixes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMix.setAdapter(adapter);
		spMix.setOnItemSelectedListener(this);

		tbLayer1.setChecked(true);
		mCurrentLayer = 1;

		tbLayer1.setOnCheckedChangeListener(this);
		tbLayer2.setOnCheckedChangeListener(this);
		
		bindUserInterface(mixView);
		
		return mixView;
	}
	

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		ToggleButton tbLayer1 = (ToggleButton) getView().findViewById(R.id.tbLayer1);
		ToggleButton tbLayer2 = (ToggleButton) getView().findViewById(R.id.tbLayer2);

		ToggleButton otherButton = buttonView.getId() == R.id.tbLayer1 ? tbLayer2
				: tbLayer1;
		otherButton.setChecked(!isChecked);

		if (isChecked) {
			mCurrentLayer = (buttonView.getId() == R.id.tbLayer1) ? 1 : 2;
		} else {
			mCurrentLayer = (buttonView.getId() == R.id.tbLayer1) ? 2 : 1;
		}

		bindUserInterface(getView());
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		bindUserInterface(getView());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private void bindUserInterface(View parent) {
		Spinner spMix = (Spinner) parent.findViewById(R.id.spMix);
		int currentMixNumber = spMix.getSelectedItemPosition();

		Qu16_VX_Buses currentBus = Qu16_UI.Qu16_Bus_Layout
				.get(currentMixNumber);
		Qu16_Id_Parameters assignCommand = Qu16_VX_Buses
				.Assign_Command(currentBus);

		Resources res = getResources();
		String packageName = getActivity().getPackageName();

		for (int i = 1; i <= 17; ++i) {
			Qu16_Input_Channels channel = Qu16_UI.Qu16_Channel_Layout.get(
					mCurrentLayer).get(i);
			if (i == 17) { // master channel?
				channel = Qu16_VX_Buses.MasterChannel(currentBus);
			}

			Qu16_VX_Buses outputBus = Qu16_VX_Buses.OutputBusForChannel(
					channel, currentBus);
			Qu16_Id_Parameters outputCommand = Qu16_VX_Buses
					.Output_Command(outputBus);

			IMixValueListener muteControl = (IMixValueListener) parent.findViewById(res
					.getIdentifier("mute" + i, "id", packageName));
			mMixer.connect(muteControl, channel);

			IMixValueListener assignControl = (IMixValueListener) parent.findViewById(res
					.getIdentifier("assign" + i, "id", packageName));
			if (assignControl != null) {
				mMixer.connect(assignControl, channel, assignCommand, outputBus);
			}

			IMixValueListener preControl = (IMixValueListener) parent.findViewById(res
					.getIdentifier("pre" + i, "id", packageName));
			if (preControl != null) {
				mMixer.connect(preControl, channel,
						Qu16_Id_Parameters.Chn_Pre_Post_Sw, outputBus);
			}

			IMixValueListener panControl = (IMixValueListener) parent.findViewById(res
					.getIdentifier("pan" + i, "id", packageName));
			if (panControl != null) {
				mMixer.connect(panControl, channel, Qu16_Id_Parameters.Chn_Pan,
						outputBus);
			}

			BoundMixFader faderControl = (BoundMixFader) parent.findViewById(res
					.getIdentifier("fader" + i, "id", packageName));
			if (faderControl != null) {
				faderControl
						.setChannelName(getString(Qu16_UI.Channel_String_Ids
								.get(channel.getValue())));
				mMixer.connect(faderControl, channel, outputCommand, outputBus);
			}

			IMixValueListener paflControl = (IMixValueListener) parent.findViewById(res
					.getIdentifier("pafl" + i, "id", packageName));
			if (paflControl != null) {
				mMixer.connect(paflControl, channel,
						Qu16_Id_Parameters.Chn_PAFL_Sw, Qu16_VX_Buses.LR);
			}
		}
	}

	@Override
	public void connectToMixer(Qu16_Mixer mixer, Qu16_VX_Buses bus,
			Qu16_Input_Channels channel) {
		mMixer = mixer;
		
	}

	@Override
	public void disconnectFromMixer() {
		// TODO Auto-generated method stub
		
	}
}
