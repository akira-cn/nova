package com.weizoo.nova.js.network;

import android.webkit.JavascriptInterface;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.weizoo.nova.js.JSClass;
import com.weizoo.nova.js.JSObject;
import com.weizoo.nova.js.JSProxy;
import com.weizoo.nova.lib.NovaWebActivity;

public class WebSocket extends JSProxy{
	private static final int CONNECTING = 0;
	private static final int OPEN = 1;
	private static final int CLOSING = 2;
	private static final int CLOSED = 3;
	
	@Override
	public Object exportJavaScriptInterface(final NovaWebActivity activity){
		return new JSClass(activity){
			@JavascriptInterface
			public int __getter__CONNECTING(){
				return CONNECTING;
			}
			
			@JavascriptInterface
			public int __getter__OPEN(){
				return OPEN;
			}
			
			@JavascriptInterface
			public int __getter__CLOSING(){
				return CLOSING;
			}
			
			@JavascriptInterface
			public int __getter__CLOSED(){
				return CLOSED;
			}
			
			@Override
			public JSObject constructor(final String url){
				
				class WebSocketImpl extends JSObject 
					implements WebSocketConnectCallback{
					
					private com.koushikdutta.async.http.WebSocket webSocket = null;
					private int readyState = CLOSED;
					
					public WebSocketImpl(NovaWebActivity activity, Object... args) {
						super(activity, args);
					}
					
					@JavascriptInterface
					public int __getter__CONNECTING(){
						return CONNECTING;
					}
					
					@JavascriptInterface
					public int __getter__OPEN(){
						return OPEN;
					}
					
					@JavascriptInterface
					public int __getter__CLOSING(){
						return CLOSING;
					}
					
					@JavascriptInterface
					public int __getter__CLOSED(){
						return CLOSED;
					}
					
					@JavascriptInterface
					public String __getter__URL(){
						return url;
					}
					
					@JavascriptInterface
					public int __getter__readyState(){
						return readyState;
					}
					
					@JavascriptInterface
					public void send(String message){
						if(webSocket != null){
							webSocket.send(message);
						}
					}
					
					@JavascriptInterface
					public void close(){
						if(webSocket != null){
							webSocket.close();
						}
					}
					
					private String onopen = null;
					private String onmessage = null;
					private String onclose = null;
					private String onerror = null;
					
					@JavascriptInterface
					public void __setter__onopen(String callbackId){
						onopen = callbackId;
					}
					
					@JavascriptInterface
					public void __setter__onmessage(String callbackId){
						onmessage = callbackId;
					}
					
					@JavascriptInterface
					public void __setter__onclose(String callbackId){
						onclose = callbackId;
					}
					
					@JavascriptInterface
					public void __setter__onerror(String callbackId){
						onerror = callbackId;
					}
					
					@Override
					public void onCompleted(Exception ex,
							com.koushikdutta.async.http.WebSocket webSocket) {
						this.webSocket = webSocket;
						this.readyState = OPEN;
						
		            	if(onopen != null){
		            		callbackEvent("open", null, onopen);
		            	}
		            	
				        webSocket.setStringCallback(new StringCallback() {
				            @Override
				            public void onStringAvailable(String s) {
				            	if(onmessage != null){
				            		callbackEvent("message", s, onmessage);
				            	}
				            }
				        });	
				        
				        webSocket.setClosedCallback(new CompletedCallback(){
							@Override
							public void onCompleted(Exception ex) {
								readyState = CLOSED;
								
								if(onclose != null){
									callbackEvent("close", null, onclose);
								}
							}
				        });
				        
				        webSocket.setEndCallback(new CompletedCallback(){
							@Override
							public void onCompleted(Exception ex) {
								readyState = CLOSED;
								
								if(onerror != null){
									callbackEvent("error", null, onerror);
								}
							}				        	
				        });
					}
				};
				
				WebSocketImpl socketClient = new WebSocketImpl(activity, url);
				
				AsyncHttpClient.getDefaultInstance().websocket(url, 
						null, socketClient);
				
				return socketClient;
			}
		};
	}
}
