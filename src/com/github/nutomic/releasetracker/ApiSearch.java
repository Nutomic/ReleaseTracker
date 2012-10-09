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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.nutomic.releasetracker.sql.SqlCursorAdapter;
import com.github.nutomic.releasetracker.sql.SqlHelper;
import com.github.savvasdalkitsis.jtmdb.GeneralSettings;
import com.github.savvasdalkitsis.jtmdb.Movie;

/**
 * This class searches on all APIs (one at a time -> pass object that contains
 * type and query).
 */
public class ApiSearch extends
		AsyncTask<String, Integer, ArrayAdapter<SqlItem>> {

	private static final String TAG = "ApiSearch";

	private final Activity activity_;

	private ProgressDialog pg_;

	private ArrayAdapter<SqlItem> adapter_;

	/**
	 * Constructor, get activity to display ProgressDialog.
	 * 
	 * @param activity
	 *            Activity this class is constructed from.
	 */
	public ApiSearch(Activity activity) {
		activity_ = activity;
	}

	/**
	 * Creates ProgressDialog before starting web request.
	 */
	@Override
	protected void onPreExecute() {
		pg_ = ProgressDialog.show(activity_, "", "Loading. Please wait...",
				true, true, new OnCancelListener() {
					public void onCancel(DialogInterface di) {
						ApiSearch.this.cancel(true);
					}
				});
		pg_.show();
	}

	/**
	 * Searches remote API for query, depending on selected type. Must have
	 * exactly two parameters. May return null (valid adapter value for
	 * ListView).
	 * 
	 * @param params
	 *            [0] Search query.
	 * @param params
	 *            [1] Search type.
	 */
	@Override
	protected ArrayAdapter<SqlItem> doInBackground(String... params) {
		List<SqlItem> ri = new ArrayList<SqlItem>();
		switch (Integer.parseInt(params[1])) {
		case SqlHelper.API_ALL:
			break;
		case SqlHelper.API_MOVIE:
			GeneralSettings.setAPILocale(Locale.getDefault());
			GeneralSettings.setApiKey((String) activity_
					.getText(R.string.api_key_tmdb));
			List<Movie> movies;
			try {
				movies = Movie.search(params[0]);
			} catch (IOException e) {
				Log.w(TAG, "TMDB search failed.", e);
				return null;
			} catch (JSONException e) {
				Log.w(TAG, "TMDB json read failed.", e);
				return null;
			}
			Date now = new Date();
			for (int i = 0; i < movies.size(); i++) {
				if (isCancelled())
					return null;
				Movie cur = movies.get(i);
				// ReleaseDate can be null, we don't want these items.
				if ((cur.getReleasedDate() != null)
						&& cur.getReleasedDate().after(now)) {
					// try to get regisseur at this point?
					ri.add(new SqlItem(cur.getName(), "",
							cur.getReleasedDate(),
							Integer.toString(cur.getID()), SqlHelper.API_MOVIE,
							false, false));
				}
			}
			break;
		}
		Collections.sort(ri, new Comparator<SqlItem>() {
			/**
			 * Compare two release items by date. Does not check for null!
			 * 
			 * @inheritDoc
			 */
			public int compare(SqlItem lhs, SqlItem rhs) {
				SqlItem l = (SqlItem) lhs;
				SqlItem r = (SqlItem) rhs;
				if (l.releaseDate.before(r.releaseDate))
					return -1;
				if (l.releaseDate.after(r.releaseDate))
					return 1;
				return 0;

			}
		});
		return new ArrayAdapter<SqlItem>(activity_, 0, ri) {
			@Override
			/**
			 * Create or initialize view with custom texts
			 * @inheritDoc
			 */
			public View getView(int position, View row,
					android.view.ViewGroup parent) {
				if (row == null) {
					row = activity_.getLayoutInflater().inflate(
							R.layout.listview_item, null);
				}
				SqlItem item = (SqlItem) getItem(position);
				SqlCursorAdapter.initializeRow(row, item.title,
						(String) SqlCursorAdapter.dateFormat(item.releaseDate),
						item.artist);
				return row;
			};
		};
	}

	/**
	 * Closes ProgressDialog and add all items into ListView.
	 * 
	 * @param adapter
	 *            Contains search results.
	 */
	@Override
	protected void onPostExecute(ArrayAdapter<SqlItem> adapter) {
		adapter_ = adapter;

		ListView lv_ = (ListView) activity_.findViewById(R.id.results);
		lv_.setAdapter(adapter_);
		pg_.dismiss();
	}

	/**
	 * Search cancelled, hide progress dialog and clear listview from possible
	 * previous search.
	 * 
	 * @inheritDoc
	 */
	@Override
	protected void onCancelled(ArrayAdapter<SqlItem> adapter) {
		ListView lv_ = (ListView) activity_.findViewById(R.id.results);
		lv_.setAdapter(null);
		pg_.dismiss();
	}

	/**
	 * Returns information about a specific item.
	 * 
	 * @param id
	 *            Data identifier.
	 * @return Item information.
	 */
	public SqlItem getData(int id) {
		return adapter_.getItem(id);
	}

}