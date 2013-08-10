package com.weizoo.nova.js.network;

import android.webkit.JavascriptInterface;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.weizoo.nova.js.JSApi;
import com.weizoo.nova.js.JSClass;
import com.weizoo.nova.js.JSObject;
import com.weizoo.nova.js.JSProxy;
import com.weizoo.nova.lib.NovaWebActivity;

interface WebSocketClassDef{
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int CONNECTING();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int OPEN();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int CLOSING();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int CLOSED();
}

interface WebSocketObjDef{
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int CONNECTING();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int OPEN();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int CLOSING();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int CLOSED();	
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public String URL();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int readyState();
	
	@JSApi(apiType = JSApi.TYPE_METHOD)
	public void send(String message);
	@JSApi(apiType = JSApi.TYPE_METHOD)
	public void close();
	
	@JSApi(apiType = JSApi.TYPE_SETTER)
	public void onopen(String callbackId);
	@JSApi(apiType = JSApi.TYPE_SETTER)
	public void onmessage(String callbackId);
	@JSApi(apiType = JSApi.TYPE_SETTER)
	public void onclose(String callbackId);
	@JSApi(apiType = JSApi.TYPE_SETTER)
	public void onerror(String callbackId);	
}

public class WebSocket extends JSProxy{
	private static final int CONNECTING = 0;
	private static final int OPEN = 1;
	private static final int CLOSING = 2;
	private static final int CLOSED = 3;
	
	@Override
	public Object exportJavaScriptInterface(final NovaWebActivity activity){
		class Exports extends JSClass implements WebSocketClassDef{

			@Override
			@JavascriptInterface
			public int CONNECTING() {
				return CONNECTING;
			}

			@Override
			@JavascriptInterface
			public int OPEN() {
				return OPEN;
			}

			@Override
			@JavascriptInterface
			public int CLOSING() {
				return CLOSING;
			}

			@Override
			@JavascriptInterface
			public int CLOSED() {
				return CLOSED;
			}
			
			@Override 
			@JavascriptInterface
			public JSObject constructor(final String url){
				
				class WebSocketImpl extends JSObject 
				implements WebSocketConnectCallback, WebSocketObjDef{
				
					private com.koushikdutta.async.http.WebSocket webSocket = null;
					private int readyState = CLOSED;
					
					@Override
					@JavascriptInterface
					public void send(String message){
						if(webSocket != null){
							webSocket.send(message);
						}
					}
					
					@Override
					@JavascriptInterface
					public void close(){
						if(webSocket != null){
							webSocket.close();
						}
					}
					
					@Override
					public void onCompleted(Exception ex,
							com.koushikdutta.async.http.WebSocket webSocket) {
						this.webSocket = webSocket;
						this.readyState = OPEN;
						
		            	if(onopenId != null){
		            		callbackEvent(activity, "open", null, onopenId);
		            	}
		            	
				        webSocket.setStringCallback(new StringCallback() {
				            @Override
				            public void onStringAvailable(String s) {
				            	if(onmessageId != null){
				            		callbackEvent(activity, "message", s, onmessageId);
				            	}
				            }
				        });	
				        
				        webSocket.setClosedCallback(new CompletedCallback(){
							@Override
							public void onCompleted(Exception ex) {
								readyState = CLOSED;
								
								if(oncloseId != null){
									callbackEvent(activity, "close", null, oncloseId);
								}
							}
				        });
				        
				        webSocket.setEndCallback(new CompletedCallback(){
							@Override
							public void onCompleted(Exception ex) {
								readyState = CLOSED;
								
								if(onerrorId != null){
									callbackEvent(activity, "error", null, onerrorId);
								}
							}				        	
				        });
					}

					private String onopenId = null;
					private String onmessageId = null;
					private String oncloseId = null;
					private String onerrorId = null;
					
					@Override
					@JavascriptInterface
					public int CONNECTING() {
						return CONNECTING;
					}

					@Override
					@JavascriptInterface
					public int OPEN() {
						return OPEN;
					}

					@Override
					@JavascriptInterface
					public int CLOSING() {
						return CLOSING;
					}

					@Override
					@JavascriptInterface
					public int CLOSED() {
						return CLOSED;
					}

					@Override
					@JavascriptInterface
					public String URL() {
						return url;
					}

					@Override
					@JavascriptInterface
					public void onopen(String callbackId) {
						onopenId = callbackId;
					}

					@Override
					@JavascriptInterface
					public void onmessage(String callbackId) {
						onmessageId = callbackId;	
					}

					@Override
					@JavascriptInterface
					public void onclose(String callbackId) {
						oncloseId = callbackId;
					}

					@Override
					@JavascriptInterface
					public void onerror(String callbackId) {
						onerrorId = callbackId;
					}

					@Override
					@JavascriptInterface
					public int readyState() {
						return readyState;
					}
				};
				
				WebSocketImpl socketClient = new WebSocketImpl();
				
				AsyncHttpClient.getDefaultInstance().websocket(url, 
						null, socketClient);
				
				return socketClient;				
			}			
		}
		
		return new Exports();
	}
}
