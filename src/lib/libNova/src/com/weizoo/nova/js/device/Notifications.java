package com.weizoo.nova.js.device;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.weizoo.nova.js.JSApi;
import com.weizoo.nova.js.JSHelper;

interface NotificationsDef{
	@JSApi(apiType = JSApi.TYPE_METHOD)
	public void postNotification(String ticker, String title, String content);
}

public class Notifications extends JSHelper implements NotificationsDef{
	public Notifications(Context context) {
		super(context);
	}

	final Activity activity = (Activity)this.getContext();
	final NotificationManager notifier = 
			(NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
	
	@Override
	@SuppressWarnings("deprecation") 
	@JavascriptInterface
	public void postNotification(String ticker, String title,
			String content) {
		final Notification notification = new Notification();
		notification.icon = android.R.drawable.ic_dialog_alert;
		notification.tickerText = ticker;
		//notification.defaults = Notification.DEFAULT_SOUND;
		Intent notificationIntent = activity.getIntent();
		PendingIntent contentIntent = PendingIntent.getActivity(activity, 0,notificationIntent, 0);
		notification.setLatestEventInfo(activity, title, content, contentIntent);
		notifier.notify(1, notification);				
	}	
}
