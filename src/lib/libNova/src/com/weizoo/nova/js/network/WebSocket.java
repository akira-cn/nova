package com.weizoo.nova.js.network;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.weizoo.nova.js.JSApi;
import com.weizoo.nova.js.JSClass;
import com.weizoo.nova.js.JSHelper;

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
	public void onopen(int callbackId);
	@JSApi(apiType = JSApi.TYPE_SETTER)
	public void onmessage(int callbackId);
	@JSApi(apiType = JSApi.TYPE_SETTER)
	public void onclose(int callbackId);
	@JSApi(apiType = JSApi.TYPE_SETTER)
	public void onerror(int callbackId);	

	@JSApi(apiType = JSApi.TYPE_GETTER, apiRet = JSApi.RET_FUNC)
	public int onopen();
	@JSApi(apiType = JSApi.TYPE_GETTER, apiRet = JSApi.RET_FUNC)
	public int onmessage();
	@JSApi(apiType = JSApi.TYPE_GETTER, apiRet = JSApi.RET_FUNC)
	public int onclose();
	@JSApi(apiType = JSApi.TYPE_GETTER, apiRet = JSApi.RET_FUNC)
	public int onerror();
}

public class WebSocket extends JSHelper implements JSClass,WebSocketClassDef{
	
	public WebSocket(Context context) {
		super(context);
	}

	private static final int CONNECTING = 0;
	private static final int OPEN = 1;
	private static final int CLOSING = 2;
	private static final int CLOSED = 3;
	
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
	public JSHelper constructor(final String url){
		
		class WebSocketImpl extends JSHelper 
		implements WebSocketConnectCallback, WebSocketObjDef{
		
			public WebSocketImpl(Context context) {
				super(context);
			}

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
				
            	if(onopenId >= 0){
            		callbackEvent("open", null, onopenId);
            	}
            	
		        webSocket.setStringCallback(new StringCallback() {
		            @Override
		            public void onStringAvailable(String s) {
		            	if(onmessageId >= 0){
		            		callbackEvent("message", s, onmessageId);
		            	}
		            }
		        });	
		        
		        webSocket.setClosedCallback(new CompletedCallback(){
					@Override
					public void onCompleted(Exception ex) {
						readyState = CLOSED;
						
						if(oncloseId >= 0){
							callbackEvent("close", null, oncloseId);
						}
					}
		        });
		        
		        webSocket.setEndCallback(new CompletedCallback(){
					@Override
					public void onCompleted(Exception ex) {
						readyState = CLOSED;
						
						if(onerrorId >= 0){
							callbackEvent("error", null, onerrorId);
						}
					}				        	
		        });
			}

			private int onopenId = -1;
			private int onmessageId = -1;
			private int oncloseId = -1;
			private int onerrorId = -1;
			
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
			public void onopen(int callbackId) {
				onopenId = callbackId;
			}

			@Override
			@JavascriptInterface
			public void onmessage(int callbackId) {
				onmessageId = callbackId;	
			}

			@Override
			@JavascriptInterface
			public void onclose(int callbackId) {
				oncloseId = callbackId;
			}

			@Override
			@JavascriptInterface
			public void onerror(int callbackId) {
				onerrorId = callbackId;
			}

			@Override
			@JavascriptInterface
			public int readyState() {
				return readyState;
			}

			@Override
			@JavascriptInterface
			public int onopen() {
				return onopenId;
			}

			@Override
			@JavascriptInterface
			public int onmessage() {
				return onmessageId;
			}

			@Override
			@JavascriptInterface
			public int onclose() {
				return oncloseId;
			}

			@Override
			@JavascriptInterface
			public int onerror() {
				return onerrorId;
			}
		};
		
		final WebSocketImpl socketClient = new WebSocketImpl(getContext());
		
		getHandler().postDelayed(new Runnable(){

			@Override
			public void run() {
				AsyncHttpClient.getDefaultInstance().websocket(url, 
						null, socketClient);
			}
			
		}, 100);

		return socketClient;				
	}			
}
