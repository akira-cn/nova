package com.qihoo.net.qalarm;

import com.weizoo.nova.lib.NovaWebActivity;

import android.os.Bundle;

public class MainActivity extends NovaWebActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.loadUrl("file:///android_asset/www/index.html");
	}
}
