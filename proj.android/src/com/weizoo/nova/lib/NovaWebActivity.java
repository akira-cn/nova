package com.weizoo.nova.lib;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ZoomButtonsController;

import com.weizoo.nova.R;
import com.weizoo.nova.js.JSProxy;

public class NovaWebActivity extends Activity {
	
	protected WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novaweb);
		this.init();
	}
	
	protected void init(){
		mWebView = (WebView) findViewById(R.id.web_nova);
		initWebSettings();
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        
        initProxy();
		
        mWebView.loadUrl("file:///android_asset/www/index.html");
	    mWebView.setWebViewClient(mViewClient);
	    mWebView.setWebChromeClient(mChromeClient);
	    
	    //Echo echo = new Echo();
	    //echo.prepareActivity(this, ".nova.echoApi");
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
				        JSProxy proxy = (JSProxy) Class.forName(strClassName).newInstance();
				        proxy.prepareActivity(this, ".nova." + strExportName);
					}
				}
				xmlBirds.next();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    	
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings(){
        final WebSettings settings = mWebView.getSettings();
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
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
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
        } catch (Throwable ignored) {
        	//ignored.printStackTrace();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            settings.setPluginState(PluginState.ON);
        }
    }	
	
	public WebView getWebView(){
		return mWebView;
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
        
	    @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:nova._init();");
        }
        
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	        if (view == null || TextUtils.isEmpty(view.getUrl())) {
	            return;
	        }
	        Log.e("Web Console", description + " at " + failingUrl);
	    };
	};
}
