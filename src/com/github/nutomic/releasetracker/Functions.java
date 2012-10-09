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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * A random collection of functions.
 */
public class Functions {

	private final static String TAG = "Functions";

	/**
	 * Create an overlay dialog that displays the contents of a resource file.
	 * 
	 * @param activity
	 *            Activity to display the dialog in.
	 * @param title
	 *            The dialog title.
	 * @param resource
	 *            d of a raw resource file to be displayed.
	 */
	public static void overlay(Activity activity, String title, int resource) {
		Log.i(TAG, "Displaying overlay.");

		String text = "";

		try {
			BufferedReader buffreader = new BufferedReader(
					new InputStreamReader(activity.getResources()
							.openRawResource(resource)));
			String line;
			while ((line = buffreader.readLine()) != null) {
				text += line + "\n";
			}
		} catch (IOException e) {
			Log.w(TAG, "Failed to open raw resource file.", e);
			return;
		}

		TextView tv = new TextView(activity);
		tv.setText(text.toString());

		ScrollView sv = new ScrollView(activity);
		sv.addView(tv);

		new AlertDialog.Builder(activity).setTitle(title)
				.setPositiveButton(android.R.string.ok, null).setView(sv)
				.show();
	}

	/**
	 * Check for an active internet connection.
	 * 
	 * @param context
	 *            Application context.
	 * @return true if a network connection is available.
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) ReleaseTracker
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}