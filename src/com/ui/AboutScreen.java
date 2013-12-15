/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.ui;

import com.khmelenko.lab.simpleroute.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * About screen
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class AboutScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		TextView versionControl = (TextView) findViewById(R.id.version);
		try {
			String packageName = getPackageName();
			PackageInfo info = getPackageManager().getPackageInfo(packageName,
					0);
			String versionText = getResources().getString(R.string.version,
					info.versionName);
			versionControl.setText(versionText);
		} catch (NameNotFoundException e) {
			e.printStackTrace();

			// hide version control if an exception occurred
			versionControl.setVisibility(View.GONE);
		}

	}
}
