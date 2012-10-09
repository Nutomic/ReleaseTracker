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

import android.content.ContentValues;
import android.database.Cursor;

import com.github.nutomic.releasetracker.SqlPerson;

/**
 * A collection of functions to access the "persons" SQL table.
 * 
 * The meaning of api_id depends on type (movie -> TMDB etc).
 */
public class PersonTable {

	/**
	 * Indexes for each column
	 */
	public static final int FIELD_ID = 0;
	public static final int FIELD_NAME = 1;
	public static final int FIELD_API_ID = 2;
	public static final int FIELD_TYPE = 3;

	private static final String TABLE_NAME = "persons";

	private PersonTable() {
	}

	/**
	 * Returns the string that creates this table.
	 * 
	 * @return String used to create this table.
	 */
	public static String getCreateTableString() {
		return "CREATE TABLE " + TABLE_NAME + " (" + "_id INTEGER PRIMARY KEY,"
				+ "name TEXT, " + "api_id TEXT" + "type INTEGER);";
	}

	/**
	 * Delete a single row from the database.
	 * 
	 * @param id
	 *            Id of the entry to be deleted.
	 */
	public static void deleteRow(long id) {
		SqlHelper
				.getInstance()
				.getWritableDatabase()
				.delete(TABLE_NAME, "_id = ?",
						new String[] { Long.toString(id) });
	}

	/**
	 * Select all artists, sorted A to Z.
	 * 
	 * @return Cursor to results.
	 */
	public static Cursor getDataCursor() {
		Cursor result = SqlHelper
				.getInstance()
				.getReadableDatabase()
				.query(TABLE_NAME, new String[] { "_id", "name" }, null, null,
						null, null, "NAME ASC", null);
		return result;
	}

	/**
	 * Add a row to the database.
	 * 
	 * @param data
	 *            SingleItem object containing values.
	 */
	public static void insertRow(SqlPerson data) {
		ContentValues values = new ContentValues();
		values.put("name", data.name.trim());
		values.put("api_id", data.apiId);
		values.put("type", data.apiType);

		SqlHelper.getInstance().getWritableDatabase()
				.insert(TABLE_NAME, null, values);
	}

	public static void updateRow(int id, String name) {
		ContentValues values = new ContentValues();
		values.put("name", name.trim());

		SqlHelper
				.getInstance()
				.getReadableDatabase()
				.update(TABLE_NAME, values, "_id = ?",
						new String[] { Integer.toString(id) });
	}
}