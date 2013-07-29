package com.weizoo.nova.js;

import android.webkit.JavascriptInterface;

import com.weizoo.nova.lib.NovaWebActivity;

public class JSClass extends JSObject{
	
	public JSClass(NovaWebActivity activity) {
		super(activity);
	}
	
	@JavascriptInterface
	public JSObject constructor(String args){
		return new JSObject(mActivity.get(), args);
	}
}
