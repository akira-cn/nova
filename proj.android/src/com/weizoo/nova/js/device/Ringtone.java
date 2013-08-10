package com.weizoo.nova.js.device;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.webkit.JavascriptInterface;

import com.weizoo.nova.js.JSApi;
import com.weizoo.nova.js.JSObject;
import com.weizoo.nova.js.JSProxy;
import com.weizoo.nova.lib.NovaWebActivity;

interface RingtoneDef{
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int TYPE_RINGTONE();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int TYPE_ALARM();	
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int TYPE_NOTIFICATION();
	
	@JSApi(apiType = JSApi.TYPE_METHOD)
	public void play(int type);
}

public class Ringtone extends JSProxy {
	@Override
	public Object exportJavaScriptInterface(final NovaWebActivity activity){
		
		class Exports extends JSObject implements RingtoneDef{

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
				final MediaPlayer mp = new MediaPlayer();
				mp.reset();                    
				try {
					mp.setDataSource(activity.getApplicationContext(),
							RingtoneManager.getDefaultUri(type));
					mp.prepare();
					mp.start();	
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}
		}
		
		return new Exports();
	}
}
