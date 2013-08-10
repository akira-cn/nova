package com.weizoo.nova.js.device;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.webkit.JavascriptInterface;

import com.weizoo.nova.js.JSApi;
import com.weizoo.nova.js.JSObject;
import com.weizoo.nova.js.JSProxy;
import com.weizoo.nova.lib.NovaWebActivity;

interface PhoneStateDef{
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int PHONE_TYPE_NONE();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int PHONE_TYPE_GSM();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int PHONE_TYPE_CDMA();
	@JSApi(apiType = JSApi.TYPE_GETTER)
	public int PHONE_TYPE_SIP();
	
	@JSApi(apiType = JSApi.TYPE_GETTER, apiRet = JSApi.RET_JSON)
	public String telephony();
	@JSApi(apiType = JSApi.TYPE_GETTER, apiRet = JSApi.RET_JSON)
	public String connectivity();
}

public class PhoneState extends JSProxy {
	@Override
	public Object exportJavaScriptInterface(final NovaWebActivity activity){
		final TelephonyManager tm 
			= (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		
		final ConnectivityManager co 
			= (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);  
		
		final class Exports extends JSObject implements PhoneStateDef{

			@Override
			@JavascriptInterface
			public int PHONE_TYPE_NONE() {
				return 0;
			}

			@Override
			@JavascriptInterface
			public int PHONE_TYPE_GSM() {
				return 1;
			}

			@Override
			@JavascriptInterface
			public int PHONE_TYPE_CDMA() {
				return 2;
			}

			@Override
			@JavascriptInterface
			public int PHONE_TYPE_SIP() {
				return 3;
			}

			@Override
			@JavascriptInterface
			public String telephony() {
				JSONObject json = new JSONObject();
				
				try {
					json.put("deviceId", tm.getDeviceId());
					json.put("softwareVersion", tm.getDeviceSoftwareVersion());
					json.put("line1Number", tm.getLine1Number());
					json.put("networkType", tm.getNetworkType());
					json.put("phoneType", tm.getPhoneType());
					json.put("IMSI", tm.getSubscriberId());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "null";
				}
				
				return json.toString();
			}

			@Override
			@JavascriptInterface
			public String connectivity() {
				try{
		            NetworkInfo[] info = co.getAllNetworkInfo();  
		            if (info != null) {   
		            	JSONObject data = new JSONObject();
		                for (int i = 0; i < info.length; i++) {  
		                	try {
								data.put(info[i].getTypeName(), info[i].getState().toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
		                }
		                return data.toString();
		            }else{
		            	return "null";
		            }
				}catch(Exception ex){
					return "null";
				}
			}
			
		}
		
		return new Exports();
	}
}
