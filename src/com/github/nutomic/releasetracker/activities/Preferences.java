/* 
 * Copyright (c) 2012, Felix Ableitner
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.nutomic.releasetracker.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.github.nutomic.releasetracker.ChangeLog;
import com.github.nutomic.releasetracker.Functions;
import com.github.nutomic.releasetracker.R;
import com.github.nutomic.releasetracker.UpdateCheckService;

public class Preferences extends SherlockPreferenceActivity implements
		OnPreferenceClickListener, OnPreferenceChangeListener {

	private static final String TAG = "Preferences";

	/**
	 * Set listeners for settings clicks.
	 * 
	 * @inheritDoc
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		findPreference("changelog_view").setOnPreferenceClickListener(this);
		findPreference("license_android_change_log")
				.setOnPreferenceClickListener(this);
		findPreference("license_tmdb").setOnPreferenceClickListener(this);
		findPreference("license_jtmdb").setOnPreferenceClickListener(this);

		findPreference("automatic_data_updates").setOnPreferenceChangeListener(
				this);
	}

	/**
	 * User pressed an item with a special action.
	 * 
	 * @inheritDoc
	 */
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals("changelog_view")) {
			new ChangeLog(this).getFullLogDialog().show();
		} else if (preference.getKey().equals("license_android_change_log")) {
			Functions.overlay(this,
					getString(R.string.preferences_license_android_change_log),
					R.raw.license_lgpl);
		} else if (preference.getKey().equals("license_tmdb")) {
			Functions.overlay(this,
					getString(R.string.preferences_license_tmdb),
					R.raw.license_tmdb);
		} else if (preference.getKey().equals("license_jtmdb")) {
			Functions.overlay(this,
					getString(R.string.preferences_license_jtmdb),
					R.raw.license_lgpl);
		} else {
			return false;
		}
		return true;
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey() == "automatic_data_updates") {
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			if (Integer.getInteger((String) newValue) != 0) {
				Log.i(TAG, "enabled update service");
				am.setInexactRepeating(AlarmManager.RTC, 0,
						AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(
								this, 0, new Intent(this,
										UpdateCheckService.class), 0));
			} else {
				Log.i(TAG, "disabled update service");
				am.cancel(PendingIntent.getBroadcast(this, 0, new Intent(this,
						UpdateCheckService.class), 0));
			}
		}
		return true;
	}
}