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

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.github.nutomic.releasetracker.SqlItem;

/**
 * A collection of functions to access the "items" SQL table.
 * 
 * Date is saved as yyyy-mm-dd. The meaning of api_id depends on type (movie ->
 * TMDB etc).
 */
public class ItemTable {

	private static final String TAG = "ItemTable";

	/**
	 * Indexes for each column
	 */
	public static final int FIELD_ID = 0;
	public static final int FIELD_TITLE = 1;
	public static final int FIELD_ARTIST = 2;
	public static final int FIELD_RELEASE_DATE = 3;
	public static final int FIELD_API_ID = 4;
	public static final int FIELD_TYPE = 5;
	public static final int FIELD_NOTIFIED = 6;
	public static final int FIELD_RELEASED = 7;

	private static final String TABLE_NAME = "items";

	private ItemTable() {
	}

	/**
	 * Returns the string that creates this table.
	 * 
	 * @return String used to create this table.
	 */
	public static String getCreateTableString() {
		return "CREATE TABLE " + TABLE_NAME + " ("
				+ "_id INTEGER PRIMARY KEY, " + "title TEXT, "
				+ "artist TEXT, " + "release_date TEXT, " + "api_id TEXT, "
				+ "type INTEGER, " + "notified INTEGER, "
				+ "released INTEGER);";
	}

	/**
	 * Returns a cursor to the titles of all items marked as released by
	 * {@link #checkReleased()}, but not marked as notified by
	 * {@link #setNotified(long)}.
	 * 
	 * @return Result cursor.
	 */
	public static Cursor getReleasedTitles() {
		return SqlHelper
				.getInstance()
				.getReadableDatabase()
				.query(TABLE_NAME, new String[] { "title" },
						"released = 1 AND notified = 0", null, null, null, null);
	}

	/**
	 * Delete a single row from the database.
	 * 
	 * @param Id
	 *            Id of the entry to be deleted.
	 */
	public static void deleteRow(long id) {
		Log.i(TAG, "Deleting item with id " + Long.toString(id));
		SqlHelper
				.getInstance()
				.getWritableDatabase()
				.delete(TABLE_NAME, "_id = ?",
						new String[] { Long.toString(id) });
	}

	/**
	 * Saves to database that the user has been informed about the release of a
	 * specific item.
	 * 
	 * @param id
	 *            SQL id of the item to be updated.
	 */
	public static void setNotified(long id) {
		Log.d(TAG, "Deleting item with id " + Long.toString(id));
		ContentValues values = new ContentValues();
		values.put("notified", "1");
		SqlHelper
				.getInstance()
				.getWritableDatabase()
				.update(TABLE_NAME, values, "_id = ?",
						new String[] { Long.toString(id) });
	}

	/**
	 * Checks if any item in the database has been released since the last
	 * check. Use {@link #itemsReleased()} to get the number of released items.
	 */
	public static void checkReleased() {
		ContentValues values = new ContentValues();
		values.put("released", "1");
		SqlHelper
				.getInstance()
				.getWritableDatabase()
				.update(TABLE_NAME, values,
						"released = 0 AND release_date <= date('now')", null);
	}

	/**
	 * Select all unreleased items from database, ordered by date (soonest
	 * first).
	 * 
	 * @param history
	 *            If true select items for history (notified is true)
	 * @return Cursor to results.
	 */
	public static Cursor getDataCursor(boolean history) {
		Cursor result = SqlHelper
				.getInstance()
				.getReadableDatabase()
				.query(TABLE_NAME,
						new String[] { "_id", "title", "artist",
								"release_date", "api_id", "type", "notified",
								"released" }, "notified = ?",
						new String[] { (history) ? "1" : "0" }, null, null,
						"RELEASE_DATE ASC", null);
		return result;
	}

	/**
	 * Add a row to the database.
	 * 
	 * @param data
	 *            SingleItem object containing values.
	 */
	public static void insertRow(SqlItem data) {
		ContentValues values = new ContentValues();
		values.put("title", data.title.trim());
		values.put("artist", data.artist.trim());
		values.put("release_date", (String) dateToSql(data.releaseDate));
		values.put("api_id", data.apiId);
		values.put("type", data.apiType);
		values.put("notified", 0);
		values.put("released", (data.releaseDate.after(new Date())) ? 0 : 1);

		SqlHelper.getInstance().getWritableDatabase()
				.insert(TABLE_NAME, null, values);
	}

	public static void updateRow(int id, String title, Date date) {
		ContentValues values = new ContentValues();
		values.put("title", title.trim());
		values.put("release_date", dateToSql(date));
		values.put("released", (date.after(new Date())) ? 0 : 1);

		SqlHelper
				.getInstance()
				.getReadableDatabase()
				.update(TABLE_NAME, values, "_id = ?",
						new String[] { Integer.toString(id) });
	}

	public static boolean itemExists(int apiType, String apiId) {
		return SqlHelper
				.getInstance()
				.getReadableDatabase()
				.query(TABLE_NAME, new String[] { "_id" },
						"type = ? AND api_id = ?",
						new String[] { Integer.toString(apiType), apiId },
						null, null, null, null).getCount() > 0;
	}

	private static String dateToSql(Date date) {
		return (String) android.text.format.DateFormat.format("yyyy-MM-dd",
				date);
	}
}