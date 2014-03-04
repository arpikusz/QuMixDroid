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

import org.wieggers.qu_apps.qu16.Qu16_Mixer;
import org.wieggers.qu_apps.qu16.Qu16_Mixer.IMixerListener;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements IMixerListener, OnNavigationListener
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
		.add(R.id.main_frame, new ChannelFragment(), "main")
		.commit();
	
		final ActionBar actionBar = getActionBar();
		//actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.mixes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(adapter, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
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
				
				/*
				MixerFragment fragment = new MixerFragment();
				fragment.SetMixer(mMixer);
				
				getFragmentManager()
					.beginTransaction()
					.replace(R.id.main_frame, fragment, "main")
					.commit();
					*/
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}	


}
