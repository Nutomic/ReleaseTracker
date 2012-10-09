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

package com.github.nutomic.releasetracker;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.nutomic.releasetracker.sql.ItemTable;
import com.github.nutomic.releasetracker.sql.PersonTable;
import com.github.nutomic.releasetracker.sql.SqlHelper;
import com.github.savvasdalkitsis.jtmdb.FilmographyInfo;
import com.github.savvasdalkitsis.jtmdb.GeneralSettings;
import com.github.savvasdalkitsis.jtmdb.Movie;
import com.github.savvasdalkitsis.jtmdb.Person;

/**
 * Checks for newly announced items by artists and for updated item release
 * dates.
 */
public class UpdateCheckService extends Service {

	private final static String TAG = "UpdateCheckService";

	/**
	 * Runs data update if more days than specified in setting have passed. Days
	 * where this service is not launched are not counted.
	 */
	@Override
	public void onCreate() {
		int SECONDS_PER_DAY = 86400;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		int interval = Integer.getInteger(preferences.getString(
				"automatic_data_updates", "2"));
		long elapsedDays = (System.currentTimeMillis() - preferences.getInt(
				"last_data_update_timestamp", 0)) / SECONDS_PER_DAY;
		if ((interval != -1)
				&& (elapsedDays >= interval)
				&& isWifiAvailable()
				|| (Functions.isNetworkAvailable() && !preferences.getBoolean(
						"automatic_data_updates_wifi_only", true))) {
			new Thread(run).run();
			Editor editor = preferences.edit();
			editor.putLong("last_data_update_timestamp",
					System.currentTimeMillis());
			editor.commit();
		}
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public final static Runnable run = new Runnable() {
		public void run() {
			GeneralSettings.setAPILocale(Locale.getDefault());
			GeneralSettings.setApiKey((String) ReleaseTracker.getContext()
					.getText(R.string.api_key_tmdb));

			// Check for updated movies.
			Cursor cursor = ItemTable.getDataCursor(false);
			cursor.moveToFirst();
			while (cursor.isAfterLast()) {
				switch (cursor.getInt(PersonTable.FIELD_API_ID)) {
				case SqlHelper.API_MOVIE:
					Movie movie = null;
					try {
						movie = Movie.getInfo(cursor
								.getInt(ItemTable.FIELD_API_ID));
					} catch (IOException e) {
						Log.w(TAG, "TMDB access failed.", e);
						return;
					} catch (JSONException e) {
						Log.w(TAG, "TMDB json read failed.", e);
						return;
					}
					ItemTable.updateRow(cursor.getInt(ItemTable.FIELD_ID),
							movie.getName(), movie.getReleasedDate());
					break;
				}
				cursor.moveToNext();
			}

			// Check for newly announced items by persons.
			cursor = PersonTable.getDataCursor();
			cursor.moveToFirst();
			Date now = new Date();
			while (cursor.isAfterLast()) {
				switch (cursor.getInt(PersonTable.FIELD_API_ID)) {
				case SqlHelper.API_MOVIE:
					Person person = null;
					try {
						person = Person.getInfo(cursor
								.getInt(PersonTable.FIELD_API_ID));
					} catch (IOException e) {
						Log.w(TAG, "TMDB access failed.", e);
						return;
					} catch (JSONException e) {
						Log.w(TAG, "TMDB json read failed.", e);
						return;
					}
					FilmographyInfo[] films = (FilmographyInfo[]) person
							.getFilmography().toArray();
					for (int i = 0; i < films.length; i++) {
						if (now.before(films[i].getReleasedDate())
								&& ItemTable.itemExists(SqlHelper.API_MOVIE,
										Integer.toString(films[i].getID()))) {
							ItemTable.insertRow(new SqlItem(films[i].getName(),
									person.getName(), films[i]
											.getReleasedDate(), Integer
											.toString(films[i].getID()),
									SqlHelper.API_MOVIE, false, false));
						}
					}
					break;
				}
				cursor.moveToNext();
			}
		}
	};

	public void get() {
	};

	private static boolean isWifiAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) ReleaseTracker
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected()
				&& activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}
}