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

package com.github.nutomic.releasetracker.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.nutomic.releasetracker.ReleaseTracker;

/**
 * Singleton that manages SQL database connection.
 * 
 * for threading (put this in background thread):
 * http://developer.android.com/reference/android/content/CursorLoader.html
 * http://developer.android.com/reference/android/app/LoaderManager.html
 * Handler?
 */
public class SqlHelper extends SQLiteOpenHelper {

	private static final String TAG = "DB";

	/**
	 * Enum of API types.
	 */
	public static final int API_ALL = 0;
	public static final int API_MOVIE = 1;
	public static final int API_TV = 2;
	public static final int API_MUSIC = 3;
	public static final int API_CUSTOM = 4;

	private static SqlHelper instance_ = null;

	/**
	 * Various SQL constants.
	 */
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "database.db";

	/**
	 * Opens database with SQL constants.
	 * 
	 * @inheritDoc
	 */
	private SqlHelper() {
		super(ReleaseTracker.getContext(), DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	/**
	 * Returns the instance of this class. DO not hold onto this object as it
	 * may be closed on low memory.
	 * 
	 * @return Object of this class.
	 */
	public static SqlHelper getInstance() {
		if (instance_ == null) {
			instance_ = new SqlHelper();
		}
		return instance_;
	}

	/**
	 * Deletes the current instance to free memory. This function is called by
	 * {@link ReleaseTracker#onActivityPaused} or
	 * {@link ReleaseTracker#onLowMemory()} and should not be called elsewhere.
	 */
	public static void deleteInstance() {
		if (instance_ != null) {
			instance_.close();
			instance_ = null;
		}
	}

	/**
	 * Create tables.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Initializing Database.");
		db.execSQL(ItemTable.getCreateTableString());
		db.execSQL(PersonTable.getCreateTableString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// pass
	}
}