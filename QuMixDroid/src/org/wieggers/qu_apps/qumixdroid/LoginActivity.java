/*******************************************************************************
 * Copyright (c) 2013 george wieggers.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     george wieggers - initial API and implementation
 *     
 *     Author unknown  - App icon, taken from DarkGlass_Reworked KDE theme
 *     http://kde-look.org/content/show.php/Dark-Glass+reviewed?content=67902
 ******************************************************************************/
package org.wieggers.qu_apps.qumixdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements Button.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		final Button btnOk = (Button)findViewById (R.id.btnOk);
		final Button btnDemo = (Button)findViewById(R.id.btnDemo);		
		EditText txtIPAddress = (EditText)findViewById(R.id.txtIPAddress);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String defaultIp = prefs.getString("qu16_address", "");		
		txtIPAddress.setText(defaultIp, TextView.BufferType.EDITABLE);
		
		btnOk.setOnClickListener(this);
		btnDemo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		EditText txtIPAddress = (EditText)findViewById(R.id.txtIPAddress);

		
		switch (v.getId()) {
		case R.id.btnOk:
			String strIpAddress = txtIPAddress.getText().toString();
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("qu16_address", strIpAddress); 				
			editor.commit();

			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("address", strIpAddress);
			intent.putExtra("demo", false);
			startActivity(intent);

			break;		
		case R.id.btnDemo:
			intent = new Intent(this, MainActivity.class);
			intent.putExtra("address", "demo");
			intent.putExtra("demo", true);
			startActivity(intent);
			break;
		}
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}	
}
