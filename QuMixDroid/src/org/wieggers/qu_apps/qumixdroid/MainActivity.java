package org.wieggers.qu_apps.qumixdroid;

import org.wieggers.qu_apps.qumixdroid_qu16.IMixerListener;
import org.wieggers.qu_apps.qumixdroid_qu16.Qu16_Mixer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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

		mMixer = new Qu16_Mixer(this, mRemoteIp, mRemotePort, mDemoMode);
		mMixer.addListener(this);
		mMixer.start();
	}

	@Override
	public void errorOccurred(Exception exception) {

		final String msg = exception.getMessage();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
				toast.show();
				finish();
			}
		});
		
	}
}
