package org.wieggers.qu_apps.controls.bound;

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
		setBackgroundColor(Color.BLACK);
	}
	
	public interface StandardChannelListener
	{
		void Channel_Selected(StandardChannel caller);
	}
}
