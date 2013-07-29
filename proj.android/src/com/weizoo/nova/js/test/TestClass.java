package com.weizoo.nova.js.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.JavascriptInterface;

import com.weizoo.nova.js.JSClass;
import com.weizoo.nova.js.JSObject;
import com.weizoo.nova.js.JSProxy;
import com.weizoo.nova.lib.NovaWebActivity;

public class TestClass extends JSProxy{
	@Override
	public Object exportJavaScriptInterface(final NovaWebActivity activity){
		return new JSClass(activity){
			@Override
			public JSObject constructor(String args){
				try {
					JSONObject data = new JSONObject(args);
					
					return new JSObject(activity, data){
						@JavascriptInterface
						public String test() throws JSONException{
							return ((JSONObject)mArguments[0]).getString("who");
						}
					};
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}
}
