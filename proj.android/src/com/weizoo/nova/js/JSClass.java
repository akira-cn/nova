package com.weizoo.nova.js;

import android.webkit.JavascriptInterface;

public class JSClass extends JSObject{
	
	@JavascriptInterface
	public JSObject constructor(String args){
		return new JSObject(args);
	}
}
