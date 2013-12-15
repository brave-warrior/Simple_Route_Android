/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.store;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.khmelenko.lab.simpleroute.R;

/**
 * Provides access to the application settings
 * 
 * @author Dmytro Khmelenko
 * 
 */
public final class AppSettings {

	/** Key to the preference value */
	public static final String KEY_ACCESS_TOKEN = "access_token";

	/**
	 * Denied constructor
	 */
	private AppSettings() {
	}

	/**
	 * Sets new API key
	 * 
	 * @param aContext
	 *            Context
	 * @param aApiKey
	 *            API key
	 */
	public static void setApiKey(Context aContext, String aApiKey) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(aContext);
		Editor edit = pref.edit();
		edit.putString(KEY_ACCESS_TOKEN, aApiKey);
		edit.commit();
	}

	/**
	 * Gets access token
	 * 
	 * @param aContext
	 *            Context
	 * @return API key
	 */
	public static String getApiKey(Context aContext) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(aContext);
		String accessToken = pref.getString(KEY_ACCESS_TOKEN, "");

		// if API key is not found in preferences,
		// load it from the raw store
		if (accessToken.isEmpty()) {
			accessToken = readApiKey(aContext);
			setApiKey(aContext, accessToken);
		}

		return accessToken;
	}

	/**
	 * Reads API key from the raw storage
	 * 
	 * @param aContext
	 *            Context
	 * @return API key
	 */
	private static String readApiKey(Context aContext) {
		String apiKey = "";
		try {
			Resources res = aContext.getResources();
			InputStream is = res.openRawResource(R.raw.api_access);

			byte[] byteArray = new byte[is.available()];
			is.read(byteArray);
			apiKey = new String(byteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return apiKey;
	}
}
