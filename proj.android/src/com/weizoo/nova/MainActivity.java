package com.weizoo.nova;

import com.weizoo.nova.lib.NovaWebActivity;

import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends NovaWebActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
