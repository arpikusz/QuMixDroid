package org.wieggers.qu_apps.qumixdroid;

import java.io.InputStream;

import org.wieggers.qu_apps.controls.bound.BoundMixFader;
import org.wieggers.qu_apps.qu16.IMixValueListener;
import org.wieggers.qu_apps.qu16.IMixerListener;
import org.wieggers.qu_apps.qu16.Qu16_Mixer;
import org.wieggers.qu_apps.qu16.Qu16_UI;
import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements IMixerListener, OnCheckedChangeListener, OnItemSelectedListener {

	private final int mRemotePort = 51325;
	private String mRemoteIp;
	private Boolean mDemoMode;

	private Qu16_Mixer mMixer;
	private int mCurrentLayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		mRemoteIp = intent.getStringExtra("address");
		mDemoMode = intent.getBooleanExtra("demo", false);
		
		Spinner spMix = (Spinner) findViewById(R.id.spMix);
		ToggleButton tbLayer1 = (ToggleButton) findViewById(R.id.tbLayer1);
		ToggleButton tbLayer2 = (ToggleButton) findViewById(R.id.tbLayer2);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.mixes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spMix.setAdapter(adapter);
		spMix.setOnItemSelectedListener(this);

		
		tbLayer1.setChecked(true);
		mCurrentLayer = 1;
		
		tbLayer1.setOnCheckedChangeListener(this);
		tbLayer2.setOnCheckedChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if (mMixer != null) {
			mMixer.stop();
			mMixer = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mMixer = new Qu16_Mixer(mRemoteIp, mRemotePort, mDemoMode, this);
					
		mMixer.start();
		if (mDemoMode) {
			AssetManager assetMgr = this.getAssets();
			try {
				InputStream s = assetMgr.open("qu16_init_scene.txt");
				mMixer.readScene(s);
			} catch (Exception e) {
				errorOccurred(e);
			}
		}		
	}

	@Override
	public void errorOccurred(Exception exception) {

		final String msg = exception.getMessage();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG);
				toast.show();
				
				if (mMixer != null) {
					mMixer.stop();
					mMixer = null;
				}
				
				finish();
			}
		});		
	}

	@Override
	public void initialSyncComplete() {
		bindUserInterface();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		ToggleButton tbLayer1 = (ToggleButton) findViewById(R.id.tbLayer1);
		ToggleButton tbLayer2 = (ToggleButton) findViewById(R.id.tbLayer2);

		ToggleButton otherButton = buttonView.getId() == R.id.tbLayer1 ? tbLayer2 : tbLayer1;
		otherButton.setChecked(! isChecked);
		
		if (isChecked) {
			mCurrentLayer = (buttonView.getId() == R.id.tbLayer1) ? 1 : 2;
		} else {
			mCurrentLayer = (buttonView.getId() == R.id.tbLayer1) ? 2 : 1;			
		}
		
		bindUserInterface();		
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		bindUserInterface();		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private void bindUserInterface() {
		Spinner spMix = (Spinner) findViewById(R.id.spMix);
		int currentMixNumber = spMix.getSelectedItemPosition();
				
		Qu16_VX_Buses currentBus = Qu16_UI.Mixer_Bus_Layout.get(currentMixNumber);
		Qu16_Id_Parameters assignCommand = Qu16_VX_Buses.Assign_Command(currentBus);
		
		Resources res = getResources();
		String packageName = getPackageName();
		
		for (int i = 1; i <= 17; ++i)
		{
			Qu16_Input_Channels channel = Qu16_UI.Mixer_Channel_Layout.get(mCurrentLayer).get(i);
			if (i == 17) { // master channel?
				channel = Qu16_VX_Buses.MasterChannel(currentBus);
			}
			
			Qu16_VX_Buses outputBus = Qu16_VX_Buses.OutputBusForChannel(channel, currentBus);
			Qu16_Id_Parameters outputCommand = Qu16_VX_Buses.Output_Command(outputBus);

			
			IMixValueListener muteControl = (IMixValueListener) findViewById(res.getIdentifier("mute" + i, "id", packageName));
			mMixer.connect(muteControl, channel);
			
			IMixValueListener assignControl = (IMixValueListener) findViewById(res.getIdentifier("assign" + i, "id", packageName));
			if (assignControl != null) {
				mMixer.connect(assignControl, channel, assignCommand, outputBus);
			}
			
			IMixValueListener preControl = (IMixValueListener) findViewById(res.getIdentifier("pre" + i, "id", packageName));
			if (preControl != null) {
				mMixer.connect(preControl, channel, Qu16_Id_Parameters.Chn_Pre_Post_Sw, outputBus);
			}
			
			IMixValueListener panControl = (IMixValueListener) findViewById(res.getIdentifier("pan" + i, "id", packageName));
			if (panControl != null) {
				mMixer.connect(panControl, channel, Qu16_Id_Parameters.Chn_Pan, outputBus);
			}
			
			BoundMixFader faderControl = (BoundMixFader) findViewById(res.getIdentifier("fader" + i, "id", packageName));
			if (faderControl != null) {
				faderControl.setChannelName(getString(Qu16_UI.Channel_String_Ids.get(channel.getValue())));
				mMixer.connect(faderControl, channel, outputCommand, outputBus);
			}
			
			IMixValueListener paflControl = (IMixValueListener) findViewById(res.getIdentifier("pafl" + i, "id", packageName));
			if (paflControl != null) {
				mMixer.connect(paflControl, channel, Qu16_Id_Parameters.Chn_PAFL_Sw, Qu16_VX_Buses.LR);
			}
		}			
	}
}
