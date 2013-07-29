package com.weizoo.nova.js;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import com.weizoo.nova.lib.NovaWebActivity;

import android.os.Handler;

public class JSObject {
	
	protected final WeakReference<NovaWebActivity> mActivity;
	
	protected final Handler mMainHandler = new Handler();
	
	protected Object[] mArguments;
	
	public JSObject(NovaWebActivity activity, Object...args){
		mActivity = new WeakReference<NovaWebActivity>(activity);
		mArguments = args;
	}
	
	public String keys(){
		Method[] methods = getClass().getDeclaredMethods();
		Field[] fields = getClass().getDeclaredFields();
		String keys = "";
		for(int i = 0; i < methods.length; i++){
			if(methods[i].getName().equals("keys")){
				continue;
			}
			keys += methods[i].getName() + ",";
		}
		for(int j = 0; j < fields.length; j++){
			if(fields[j].getName().startsWith("this$")){
				continue;
			}
			keys += fields[j].getName() + ",";
		}
		return keys.substring(0, keys.length() - 1);
	}
	
	protected JSONObject pick(String param){
		try {
			return new JSONObject(param);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void callback(final String result, final String onSuccess){
		JSONObject data = new JSONObject();
		try {
			data.put("id", onSuccess);
			data.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		dispatchEvent("novaCallback", data.toString());
	}
	
	protected void callbackEvent(final String type, 
			final String result, final String onSuccess){
		JSONObject data = new JSONObject();
		
		try {
			data.put("id", onSuccess);
			data.put("data", result);
			data.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		dispatchEvent("novaCallbackEvent", data.toString());		
	}
	
	public void postMessage(final String message){
		this.dispatchEvent("message", message);
	}
	
	public void dispatchEvent(final String eventID, final String data){
		mMainHandler.post(new Runnable() {    
            public void run() {    
            	NovaWebActivity activity = mActivity.get();
            	if(activity != null){
            		activity.getWebView().loadUrl("javascript:var ev = document.createEvent('Events');ev.initEvent('"+eventID+"',true,true);ev.data='"+data+"';window.dispatchEvent(ev);");
            	}
            }    
        });		
	}
	
	public void destory(){
		
	}
}
