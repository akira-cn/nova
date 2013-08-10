package com.weizoo.nova.js;

import com.weizoo.nova.lib.NovaWebActivity;

import android.webkit.WebView;

public abstract class JSProxy {
	
	protected JSObject mJSObject;
	
	protected Object exportJavaScriptInterface(final NovaWebActivity activity){
		return new JSObject();
	}
	
	public void prepareActivity(final NovaWebActivity activity, final String strExportName){
		mJSObject = (JSObject)exportJavaScriptInterface(activity);
		WebView webView = activity.getWebView();
		webView.addJavascriptInterface(mJSObject, strExportName);
	}
	
	protected void finalize(){
		mJSObject.destory();
	}
}