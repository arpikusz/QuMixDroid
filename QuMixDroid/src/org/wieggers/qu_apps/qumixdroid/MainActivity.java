package org.wieggers.qu_apps.qumixdroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.wieggers.qu_apps.controls.bound.BoundMixFader;
import org.wieggers.qu_apps.controls.bound.BoundMuteToggleButton;
import org.wieggers.qu_apps.qu16.IMixerListener;
import org.wieggers.qu_apps.qu16.Qu16_Mixer;
import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements IMixerListener {

	private final int mRemotePort = 51325;
	private String mRemoteIp;
	private Boolean mDemoMode;

	private Qu16_Mixer mMixer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		mRemoteIp = intent.getStringExtra("address");
		mDemoMode = intent.getBooleanExtra("demo", false);
		
		/*
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mMixer != null) {
					try {
						String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
						String fileName = "scene-init-qu16.txt";

						File f = new File(baseDir + File.separator + fileName);
						FileOutputStream of = new FileOutputStream(f);						
						mMixer.writeScene(of);
						of.flush();
						of.close();						
					} catch (IOException e) {
						errorOccurred(e);
					}
				}
			}
		});
		*/
		
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
		/*
		BoundMuteToggleButton tbMute1 = (BoundMuteToggleButton) findViewById(R.id.tbMute1);		
		mMixer.connect(tbMute1, Qu16_Input_Channels.Mono_01);

		BoundMixFader fader1 = (BoundMixFader) findViewById(R.id.tbFader1);
		mMixer.connect(fader1, Qu16_Input_Channels.Mono_01, Qu16_Id_Parameters.Chn_Output_Mix, Qu16_VX_Buses.Mix_4);
		*/		
	}
}
