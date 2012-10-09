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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.github.nutomic.releasetracker.R;
import com.github.nutomic.releasetracker.ReleaseTracker;

/**
 * Manages connection between SQL cursor and ListView, disables row highlighting
 * and sets custom fields.
 */
public class SqlCursorAdapter extends ResourceCursorAdapter {

	private static String TAG = "SqlCursorAdapter";

	private boolean enableCheckBox_;

	/**
	 * @inheritDoc
	 * 
	 * @param context  @inheritDoc
	 * @param layout @inheritDoc
	 * @param c @inheritDoc
	 * @param enableCheckBox
	 *            Display a checkbox on items if their release date is in the
	 *            past.
	 */
	public SqlCursorAdapter(Context context, int layout, Cursor cursor,
			boolean enableCheckBox) {
		super(context, layout, cursor, false);
		enableCheckBox_ = enableCheckBox;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Date date = sqlToDate(cursor.getString(ItemTable.FIELD_RELEASE_DATE));
		initializeRow(view, cursor.getString(ItemTable.FIELD_TITLE),
				dateFormat(date), cursor.getString(ItemTable.FIELD_ARTIST));
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
		if (enableCheckBox_ && !date.after(new Date())) {
			checkBox.setTag(cursor.getString(ItemTable.FIELD_ID));
			checkBox.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.layout.selector_item_released);
		} else {
			checkBox.setVisibility(View.INVISIBLE);
			view.setBackgroundResource(0);
		}
	}

	/**
	 * Create a date object from an SQL formatted date string (yyyy-MM-dd).
	 * 
	 * Returns a date object set to the current time on failure.
	 * 
	 * @param sql The SQL formatted date string.
	 * @return A new date object set to the corresponding date.
	 */
	private static Date sqlToDate(String sql) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(sql);
		} catch (ParseException e) {
			Log.w(TAG, "Failed to parse date.");
			e.printStackTrace();
			date = new Date();
			date.setTime(0);
		}
		return date;
	}

	/**
	 * Convert a date object to a formatted string.
	 * 
	 * @param date
	 *            Object containing the date to be displayed.
	 * @return Formatted date, as specified by user preferences.
	 */
	public static String dateFormat(Date date) {
		java.text.DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(ReleaseTracker.getContext());
		return (String) dateFormat.format(date);
	}

	/**
	 * Initialize a ListView item with values.
	 * 
	 * @param v
	 *            The row to be initialized.
	 * @param title
	 *            String for the title field.
	 * @param releaseDate
	 *            String for the release date field.
	 * @param artist
	 *            String for the artist field.
	 */
	public static void initializeRow(View v, String title, String releaseDate,
			String artist) {
		TextView titleView = (TextView) v.findViewById(R.id.title);
		titleView.setText(title);

		TextView releaseDateView = (TextView) v.findViewById(R.id.release_date);
		releaseDateView.setText(releaseDate);

		TextView artistView = (TextView) v.findViewById(R.id.artist);
		artistView.setText(artist);
	}
}