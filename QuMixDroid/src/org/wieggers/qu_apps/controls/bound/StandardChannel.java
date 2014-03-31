package org.wieggers.qu_apps.controls.bound;

import org.wieggers.qu_apps.qu16.Qu16_Mixer;
import org.wieggers.qu_apps.qu16.midi.Qu16_Id_Parameters;
import org.wieggers.qu_apps.qu16.midi.Qu16_Input_Channels;
import org.wieggers.qu_apps.qu16.midi.Qu16_VX_Buses;
import org.wieggers.qu_apps.qumixdroid.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class StandardChannel extends LinearLayout {

	StandardChannelListener mParent = null;
	Qu16_VX_Buses mCurrentbus;
	
	public StandardChannel(Context context) {
		super(context);
		init(context);
	}

	public StandardChannel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StandardChannel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void SetParent(StandardChannelListener l)
	{
		mParent = l;
	}
	
	private void init(Context context)
	{
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
		LayoutInflater.from(context).inflate(R.layout.view_standard_channel, this, true);
		Button b = (Button) findViewById(R.id.btnChannel);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mParent != null) {
					mParent.Channel_Selected(StandardChannel.this);
				}
			}
		});
	}
	
	public CharSequence getName()
	{
		Button btnChannelName = (Button) findViewById(R.id.btnChannel);
		return btnChannelName.getText();
	}
	
	public void setName(String channelName) 
	{
		Button btnChannelName = (Button) findViewById(R.id.btnChannel);
		btnChannelName.setText(channelName);
	}
	
	public void select()
	{
		setBackgroundColor(Color.rgb(0, 80, 0));
	}
	
	public void deselect()
	{
		if (mCurrentbus == Qu16_VX_Buses.LR)
			setBackgroundColor(Color.BLACK);
		else if (mCurrentbus == Qu16_VX_Buses.FX1 || mCurrentbus == Qu16_VX_Buses.FX2)
			setBackgroundColor(Color.rgb(50, 0, 0));
		else
			setBackgroundColor(Color.rgb(0, 0, 50));
	}
	
	public void connect(Qu16_Mixer mixer, Qu16_Input_Channels channel, Qu16_VX_Buses bus)
	{
		mCurrentbus = bus;
		
		BoundMuteToggleButton btnMute = (BoundMuteToggleButton) findViewById(R.id.boundMuteToggleButton1);
		BoundMixToggleButton btnAssign = (BoundMixToggleButton) findViewById(R.id.boundMixToggleButton1);
		BoundMixToggleButton btnPre = (BoundMixToggleButton) findViewById(R.id.boundMixToggleButton2);
		BoundMixRotaryKnob btnPan = (BoundMixRotaryKnob) findViewById(R.id.boundMixRotaryKnob1);
		BoundMixFader fader = (BoundMixFader) findViewById(R.id.boundMixFader1);
		
		mixer.connect(btnMute, channel);
		mixer.connect(btnPre, channel, Qu16_Id_Parameters.Chn_Pre_Post_Sw, bus);
		mixer.connect(btnPan, channel, Qu16_Id_Parameters.Chn_Pan, bus);		
		
		if (bus == Qu16_VX_Buses.LR) {		
			mixer.connect(btnAssign, channel, Qu16_Id_Parameters.Chn_Assign_LR_Sw, bus);
			mixer.connect(fader, channel, Qu16_Id_Parameters.Chn_Output_LR, bus);
		} else {
			mixer.connect(btnAssign, channel, Qu16_Id_Parameters.Chn_Assign_Mix_Sw, bus);
			mixer.connect(fader, channel, Qu16_Id_Parameters.Chn_Output_Mix, bus);
		}
		deselect();
	}
	
	public void disconnect()
	{
		BoundMuteToggleButton btnMute = (BoundMuteToggleButton) findViewById(R.id.boundMuteToggleButton1);
		BoundMixToggleButton btnAssign = (BoundMixToggleButton) findViewById(R.id.boundMixToggleButton1);
		BoundMixToggleButton btnPre = (BoundMixToggleButton) findViewById(R.id.boundMixToggleButton2);
		BoundMixRotaryKnob btnPan = (BoundMixRotaryKnob) findViewById(R.id.boundMixRotaryKnob1);
		BoundMixFader fader = (BoundMixFader) findViewById(R.id.boundMixFader1);
		
		btnMute.connect(null);
		btnAssign.connect(null);
		btnPre.connect(null);
		btnPan.connect(null);
		fader.connect(null);
	}
	
	public interface StandardChannelListener
	{
		void Channel_Selected(StandardChannel caller);
	}	
}
