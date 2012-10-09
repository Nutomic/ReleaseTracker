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

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.github.nutomic.releasetracker.sql.SqlHelper;

/*
 * Main app activity, shows a list of all release items.
 * 
 * Test: 
 * 		everything on minSdkVersion (7)
 * 		notification service
 * 		update service
 * 		screen rotate in item/person search
 * 		contact does not set to field
 * 
 * general todo:
 * 		select time for notification
 * 		select how many days prior to release notification should be displayed
 * 		show notification (in app?) when new item from person is announced
 * 
 * release:
 * 	http://developer.android.com/guide/publishing/app-signing.html
 * 	http://developer.android.com/guide/developing/tools/proguard.html
 * 	ads/selling: make new bank account, new post address
 * 	https://github.com/sonyericssondev/ApkAnalyser/wiki
 */
public class ReleaseTracker extends Application implements
		Application.ActivityLifecycleCallbacks {

	private static AtomicInteger activityCount_ = new AtomicInteger(0);

	private static Context context_;

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();

		if (Build.VERSION.SDK_INT >= 9) {
			registerActivityLifecycleCallbacks(this);
		}

		context_ = getApplicationContext();
	}

	/**
	 * Returns global application context for objects that are active longer
	 * than the activity that started them or that do not need activity
	 * information.
	 * 
	 * @return Global application context.
	 */
	public static Context getContext() {
		return context_;
	}

	/**
	 * Closes SQL connection if no activities are open.
	 */
	public void onActivityStopped(Activity activity) {
		if (activityCount_.decrementAndGet() < 1) {
			SqlHelper.deleteInstance();
		}
	}

	/**
	 * Close SQL connection on low memory.
	 */
	@Override
	public void onLowMemory() {
		SqlHelper.deleteInstance();
	}

	public void onActivityStarted(Activity activity) {
	}

	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
	}

	public void onActivityDestroyed(Activity activity) {
	}

	public void onActivityPaused(Activity activity) {
	}

	public void onActivityResumed(Activity activity) {
	}

	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
	}
}