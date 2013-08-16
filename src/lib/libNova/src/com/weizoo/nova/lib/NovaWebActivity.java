package com.weizoo.nova.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ZoomButtonsController;

import com.weizoo.nova.R;
import com.weizoo.nova.js.JSHelper;

public class NovaWebActivity extends Activity {
	
	protected NovaWebView mWebView;
	protected FrameLayout mLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.init();
	}
	
	protected void init(){
    	// FrameLayout
        ViewGroup.LayoutParams framelayout_params =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                       ViewGroup.LayoutParams.MATCH_PARENT);	
        mLayout = new FrameLayout(this);
        mLayout.setLayoutParams(framelayout_params);
        
		mWebView = new NovaWebView(this);
		initWebSettings();
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mLayout.addView(mWebView);     
        
        initProxy();
		
	    mWebView.setWebViewClient(mViewClient);
	    mWebView.setWebChromeClient(mChromeClient);

        // Set framelayout as the content view
		setContentView(mLayout);
	}
	
	public void loadUrl(String url){
		mWebView.loadUrl(url);
	}
	
    private void initProxy(){
    	XmlResourceParser xmlBirds = getResources().getXml(R.xml.nova);
    	try {
			while(xmlBirds.getEventType() != XmlResourceParser.END_DOCUMENT){
				if(xmlBirds.getEventType() == XmlResourceParser.START_TAG){
					String strTagName = xmlBirds.getName();
					if(strTagName.equals("object")){
						String strClassName = xmlBirds.getAttributeValue(null, "name");
						String strExportName = xmlBirds.getAttributeValue(null, "exports");
						Class<?> cls = Class.forName(strClassName);
						Constructor<?> ctor[]=cls.getDeclaredConstructors(); 
						for(int i = 0; i < ctor.length; i++){
							Class<?> cx[]=ctor[i].getParameterTypes();
							if(cx.length == 1 && cx[0] == Context.class){
						        JSHelper helper = (JSHelper) cls.getConstructor(cx)
						        		.newInstance(this);
						        getWebView().addJavascriptInterface(helper, ".nova." + strExportName);								
							}
						}
					}
				}
				xmlBirds.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    	
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings(){
        final WebSettings settings = mWebView.getSettings();
        //settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setUserAgentString(null);
        settings.setJavaScriptCanOpenWindowsAutomatically(true); 
        settings.setSupportMultipleWindows(true);
        settings.setBuiltInZoomControls(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(false);
        settings.setSupportZoom(true);
        settings.setRenderPriority(RenderPriority.HIGH);
        settings.setNeedInitialFocus(false);

        try {
            if (Build.VERSION.SDK_INT >= (Build.VERSION_CODES.FROYO + 3)) {
                try {
                    Method m = android.webkit.WebSettings.class.getMethod("setDisplayZoomControls",
                                    new Class[] { Boolean.TYPE });
                    m.invoke(settings, false);
                } catch (Throwable ignored) {
                }
            } else {
                Method m = WebView.class.getMethod("getZoomButtonsController");
                ZoomButtonsController zoom = (ZoomButtonsController) m.invoke(mWebView);
                zoom.getZoomControls().setLayoutParams(new FrameLayout.LayoutParams(0, 0));
            }
            if (Build.VERSION.SDK_INT >= 16) {  
                Class<?> clazz = mWebView.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(mWebView.getSettings(), true);
                }
                mWebView.enableCrossDomainNew();
            }else{
            	mWebView.enableCrossDomain();
            }
        } catch (Throwable ignored) {
        	//ignored.printStackTrace();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            settings.setPluginState(PluginState.ON);
        }
    }	
	
	public NovaWebView getWebView(){
		return mWebView;
	}

	public FrameLayout getLayout(){
		return mLayout;
	}
	
	private WebChromeClient mChromeClient = new WebChromeClient() {
	    @Override
	    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
	        resultMsg.sendToTarget();
	    	return true;
	    }
	};
	
	private WebViewClient mViewClient = new WebViewClient() {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {  	
	        view.loadUrl(url);
	        return true;
	    }
	    /*@Override
	    public void onLoadResource (WebView view, String url){
	    	Log.d("Task", url);
	    }*/
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	        if (view == null || TextUtils.isEmpty(view.getUrl())) {
	            return;
	        }
	        Log.e("Web Console", description + " at " + failingUrl);
	    };
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {   
		   mWebView.goBack();   
	       return true;   
	   }else if(keyCode == KeyEvent.KEYCODE_BACK){
		   mWebView.loadUrl("about:blank");
	       finish();
	       return true; 
	   }   
	   return super.onKeyDown(keyCode, event);   
	}
}
