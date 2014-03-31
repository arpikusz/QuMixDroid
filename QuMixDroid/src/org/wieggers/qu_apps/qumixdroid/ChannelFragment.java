package org.wieggers.qu_apps.qumixdroid;

import java.util.ArrayList;

import org.wieggers.qu_apps.controls.bound.StandardChannel;
import org.wieggers.qu_apps.controls.bound.StandardChannel.StandardChannelListener;
import org.wieggers.qu_apps.qu16.Qu16_Mixer;
import org.wieggers.qu_apps.qu16.Qu16_UI;
import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class ChannelFragment extends Fragment implements StandardChannelListener {

	ArrayList<StandardChannel> mChannels;	
	LinearLayout mLayout;
	
	Qu16_Mixer mMixer;
	Qu16_VX_Buses mCurrentBus;
	int mCurrentLayer = 1;
	
	public ChannelFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
						
		Context context = this.getActivity();
		HorizontalScrollView scroller = new HorizontalScrollView(context);
		scroller.setFillViewport(true);
		scroller.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
		mLayout = new LinearLayout(context);
		mLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		scroller.addView(mLayout);
		
		mChannels = new ArrayList<StandardChannel>();

		showLayer();
		
		return scroller;
	}
	
	@Override
	public void onDestroyView() {
		for (StandardChannel channel:mChannels) {
			channel.disconnect();
		}

		super.onDestroyView();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.channels, menu);
		
		switch (mCurrentLayer) {
			case 1: menu.findItem(R.id.layer1).setChecked(true); break;
			case 2: menu.findItem(R.id.layer2).setChecked(true); break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.layer1: mCurrentLayer = 1; break;
		case R.id.layer2: mCurrentLayer = 2; break;
				
		}
		item.setChecked(true);
		showLayer();
		return true;
	}
	
	@Override
	public void Channel_Selected(StandardChannel caller) {		
		for (StandardChannel channel:mChannels) 
		{
			channel.deselect();
		}
		caller.select();
		//getActivity().setTitle("Chn: " + caller.getName());
	}
	
	public void connect(Qu16_Mixer mixer, Qu16_VX_Buses bus, int layer) {
		mMixer = mixer;
		mCurrentBus = bus;
		mCurrentLayer = layer;
	}
	
	public int getLayer()
	{
		return mCurrentLayer;
	}
	
	private void showLayer() {
		for (StandardChannel channel:mChannels) {
			channel.disconnect();
		}
		mLayout.removeAllViews();
		
		for (int i = 1; i <= 17; ++i) {
			Qu16_Input_Channels inputChannel = Qu16_UI.Mixer_Channel_Layout.get(mCurrentLayer).get(i);
			
			if (i == 17)
				inputChannel = Qu16_VX_Buses.MasterChannel(mCurrentBus);
			
			Qu16_VX_Buses outputBus = Qu16_VX_Buses.OutputBusForChannel(
					inputChannel, mCurrentBus);
		
			String channelName = getString(Qu16_UI.Channel_String_Ids.get(inputChannel.getValue()));			
			
			StandardChannel channel = new StandardChannel(this.getActivity());
			
			mLayout.addView(channel);

			channel.setName(channelName);
			channel.SetParent(this);
			
			if (mMixer != null)
				channel.connect(mMixer, inputChannel, outputBus);
			
			mChannels.add(channel);
		}
	}	
}
