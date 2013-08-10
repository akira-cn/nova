package com.weizoo.nova.js;

import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import com.weizoo.nova.lib.NovaWebActivity;

import android.os.Handler;
import android.webkit.JavascriptInterface;

public class JSObject {
	protected final Handler mMainHandler = new Handler();
	
	protected Object[] mArguments;
	
	public JSObject(Object...args){
		mArguments = args;
	}
	
	@JavascriptInterface
	public String __reflects__(){
		Class<?>[] its = getClass().getInterfaces();
		StringBuilder reflects = new StringBuilder();
		
		for(int i = 0; i < its.length; i++){
			Class<?> it = its[i];
			Method[] methods = it.getDeclaredMethods();
			
			try {
				for(int j = 0; j < methods.length; j++){
					Method method = methods[j];
					JSApi annotation = method.getAnnotation(JSApi.class);
					if(annotation != null){
						JSONObject json = new JSONObject();
		
						json.put("apiName", method.getName());
						json.put("apiType", annotation.apiType());
						json.put("apiRet", annotation.apiRet());
						json.put("TYPE_METHOD", JSApi.TYPE_METHOD);
						json.put("TYPE_GETTER", JSApi.TYPE_GETTER);
						json.put("TYPE_SETTER", JSApi.TYPE_SETTER);
						json.put("RET_VALUE", JSApi.RET_VALUE);
						json.put("RET_JSON", JSApi.RET_JSON);
						
						reflects.append(json.toString());
						if(j < methods.length - 1){
							reflects.append(",");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}

		return "[" + reflects.toString() + "]";
	}
	
	protected void callback(NovaWebActivity activity, final String result, final String onSuccess){
		JSONObject data = new JSONObject();
		try {
			data.put("id", onSuccess);
			data.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		dispatchEvent(activity, "novaCallback", data.toString());
	}
	
	protected void callbackEvent(NovaWebActivity activity, final String type, 
			final String result, final String onSuccess){
		JSONObject data = new JSONObject();
		
		try {
			data.put("id", onSuccess);
			data.put("data", result);
			data.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		dispatchEvent(activity, "novaCallbackEvent", data.toString());		
	}
	
	protected void postMessage(NovaWebActivity activity, final String message){
		this.dispatchEvent(activity, "message", message);
	}
	
	protected void dispatchEvent(final NovaWebActivity activity, final String eventID, final String data){
		mMainHandler.post(new Runnable() {    
            public void run() {    
            	if(activity != null){
            		activity.getWebView().loadUrl("javascript:var ev = document.createEvent('Events');ev.initEvent('"+eventID+"',true,true);ev.data='"+data+"';window.dispatchEvent(ev);");
            	}
            }    
        });		
	}
	
	public void destory(){
		
	}
}
