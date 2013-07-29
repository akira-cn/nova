package com.weizoo.nova.js.test;

import android.webkit.JavascriptInterface;

import com.weizoo.nova.js.JSObject;
import com.weizoo.nova.js.JSProxy;
import com.weizoo.nova.lib.NovaWebActivity;

public class Echo extends JSProxy{
	@Override
	public Object exportJavaScriptInterface(final NovaWebActivity activity){
		return new JSObject(activity){
			
			@JavascriptInterface
			public String message(String message){
				return message;
			}
			
			@JavascriptInterface
			public void message(String message, String onSuccess){
				callback(message, onSuccess);
			}
			
			@JavascriptInterface
			public void message(String message, String onSuccess, String onError){
				try{
					callback(message, onSuccess);
				}catch (Exception ex){
					callback(message, onSuccess);
				}
			}
		};
	}
}
