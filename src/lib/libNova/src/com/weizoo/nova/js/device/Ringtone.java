package com.weizoo.nova.js.device;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.webkit.JavascriptInterface;

import com.weizoo.nova.js.JSApi;
import com.weizoo.nova.js.JSHelper;

interface RingtoneDef{
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int TYPE_RINGTONE();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int TYPE_ALARM();	
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int TYPE_NOTIFICATION();
	
	@JSApi(apiType = JSApi.TYPE_METHOD)
	public void play(int type);
	@JSApi(apiType = JSApi.TYPE_METHOD)
	public void stop();
}

public class Ringtone extends JSHelper implements RingtoneDef{

	public Ringtone(Context context) {
		super(context);
	}
	
	final MediaPlayer mp = new MediaPlayer();
	
	@Override
	@JavascriptInterface
	public int TYPE_RINGTONE() {
		return RingtoneManager.TYPE_RINGTONE;
	}

	@Override
	@JavascriptInterface
	public int TYPE_ALARM() {
		return RingtoneManager.TYPE_ALARM;
	}

	@Override
	@JavascriptInterface
	public int TYPE_NOTIFICATION() {
		return RingtoneManager.TYPE_NOTIFICATION;
	}

	@Override
	@JavascriptInterface
	public void play(int type) {
		mp.reset();                    
		try {
			mp.setDataSource(getContext().getApplicationContext(),
					RingtoneManager.getDefaultUri(type));
			mp.prepare();
			mp.start();	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	@JavascriptInterface
	public void stop(){
		mp.stop();
	}
}

