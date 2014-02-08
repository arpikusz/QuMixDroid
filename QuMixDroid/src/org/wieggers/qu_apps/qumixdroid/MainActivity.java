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
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements IMixerListener
{

	private final int mRemotePort = 51325;
	private String mRemoteIp;
	private Boolean mDemoMode;

	private Qu16_Mixer mMixer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		mRemoteIp = intent.getStringExtra("address");
		mDemoMode = intent.getBooleanExtra("demo", false);

		getFragmentManager()
		.beginTransaction()
		.add(R.id.main_frame, new ConnectingFragment(), "main")
		.commit();
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
			final AssetManager assetMgr = this.getAssets();
			
			Runnable run = new Runnable() {
				
				@Override
				public void run() {
					try {
						InputStream s = assetMgr.open("qu16_init_scene.txt");
						mMixer.readScene(s);
					} catch (Exception e) {
						errorOccurred(e);
					}					
				}
			};
			
			new Thread(run).start();
		}
	}

	@Override
	public void errorOccurred(Exception exception) {

		final String msg = exception.getMessage();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast toast = Toast.makeText(MainActivity.this, msg,
						Toast.LENGTH_LONG);
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
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				
				MixerFragment fragment = new MixerFragment();
				fragment.SetMixer(mMixer);
				
				getFragmentManager()
					.beginTransaction()
					.replace(R.id.main_frame, fragment, "main")
					.commit();
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}	


}
