package com.weizoo.nova.js;

import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import com.weizoo.nova.lib.NovaWebActivity;
import com.weizoo.nova.lib.NovaWebView;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

public class JSHelper {
	private static final Handler mHandler = new Handler();
	
	private Context mContext = null;
	
	public JSHelper(Context context){
		mContext = context;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	public Handler getHandler(){
		return mHandler;
	}
	
	@JavascriptInterface
	public String __exports__(){
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
						json.put("RET_FUNC", JSApi.RET_FUNC);
						
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
	
	public void callback(final Object result, final int onSuccess){
		
		JSONObject data = new JSONObject();
		try {
			data.put("id", onSuccess);
			data.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		dispatchEvent("novaCallback", data.toString());
	}
	
	
	public void callbackEvent(final String type, 
			final Object result, final int onSuccess){
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
	
	public void postMessage(final NovaWebView view, final String message){
		dispatchEvent("message", message);
	}
	
	public void dispatchEvent(final String eventID, final String data){
		final NovaWebView view = ((NovaWebActivity)getContext()).getWebView();
		
				mHandler.post(new Runnable() {    
            public void run() {    
            	view.loadUrl("javascript:var ev = document.createEvent('Events');ev.initEvent('"+eventID+"',true,true);ev.data='"+data+"';window.dispatchEvent(ev);");
            }    
        });		
	}
	
	public void destory(){
		
	}
}
