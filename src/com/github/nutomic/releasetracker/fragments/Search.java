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

package com.github.nutomic.releasetracker.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.nutomic.releasetracker.ApiSearch;
import com.github.nutomic.releasetracker.Functions;
import com.github.nutomic.releasetracker.R;
import com.github.nutomic.releasetracker.SqlItem;
import com.github.nutomic.releasetracker.sql.ItemTable;
import com.github.nutomic.releasetracker.sql.SqlHelper;

/**
 * Search for an item or person and add it to the database.
 */
public class Search extends SherlockFragment implements OnItemClickListener,
		OnKeyListener, OnClickListener {

	private Activity activity_;

	private EditText et_;
	private ListView lv_;

	private ApiSearch api_;

	private boolean searching_ = false;

	/**
	 * Sets this class as OnClickListener for the search button and the results,
	 * handles actions in EditText.
	 * 
	 * @inheritDoc
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.search, container, false);
		activity_ = getActivity();

		lv_ = (ListView) v.findViewById(R.id.results);
		lv_.setOnItemClickListener(this);
		lv_.setEmptyView(v.findViewById(R.id.empty));

		et_ = (EditText) v.findViewById(R.id.text);

		activity_.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		Button b = (Button) v.findViewById(R.id.button);
		b.setOnClickListener(this);

		container.setOnKeyListener(this);
		et_.setOnKeyListener(this);

		setRetainInstance(true);

		return v;
	}

	/**
	 * Enter key pressed in search text.
	 * 
	 * @inheritDoc
	 */
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (v.equals(et_)
				&& ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
				&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
			onClick(null);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			activity_.findViewById(R.id.text).requestFocus();
			return false;
		}
		return false;
	}

	/**
	 * Searches api with user text.
	 */
	public void onClick(View v) {
		if (!searching_ && (et_.getText().length() > 0)) {
			if (!Functions.isNetworkAvailable()) {
				new AlertDialog.Builder(activity_)
						.setMessage(R.string.error_require_internet)
						.setPositiveButton(android.R.string.ok, null)
						.show();
			} else {
				searching_ = true;
				api_ = new ApiSearch(activity_);
				api_.execute(et_.getText().toString(),
						Integer.toString(SqlHelper.API_MOVIE));
				searching_ = false;
			}
		}
	}

	/**
	 * Adds the item that was selected in the ListView to the database.
	 * 
	 * @inheritDoc
	 */
	public void onItemClick(AdapterView<?> lv, final View v, int position,
			final long id) {
		DialogInterface.OnClickListener onMessagePositive = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ItemTable.insertRow((SqlItem) api_.getData((int) id));
				new AlertDialog.Builder(activity_)
						.setMessage(getString(R.string.additem_added))
						.setPositiveButton(android.R.string.ok, null).show();
			}
		};

		new AlertDialog.Builder(activity_).setMessage(R.string.search_add_item)
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, onMessagePositive)
				.show();
	}
}