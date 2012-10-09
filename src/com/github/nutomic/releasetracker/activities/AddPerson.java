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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.nutomic.releasetracker.ApiSearchPerson;
import com.github.nutomic.releasetracker.Functions;
import com.github.nutomic.releasetracker.R;
import com.github.nutomic.releasetracker.sql.PersonTable;
import com.github.nutomic.releasetracker.sql.SqlHelper;

/**
 * Search APIs for a person and add to SQL database.
 */
public class AddPerson extends SherlockActivity implements OnItemClickListener,
		OnKeyListener, OnClickListener {

	private EditText et_;
	private ListView lv_;

	private ApiSearchPerson api_;

	private boolean searching_ = false;

	class Container {
		public ApiSearchPerson api;
		public ListView lv;
		public boolean searching;
	}

	/**
	 * Sets this class as OnClickListener for the search button and the results,
	 * handles actions in EditText.
	 * 
	 * @inheritDoc
	 */
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		Container c = (Container) getLastNonConfigurationInstance();
		if (c != null) {
			api_ = c.api;
			lv_ = c.lv;
			searching_ = c.searching;
		} else {
			lv_ = (ListView) findViewById(R.id.results);
			lv_.setOnItemClickListener(this);
			lv_.setEmptyView(findViewById(R.id.empty));
		}

		et_ = (EditText) findViewById(R.id.text);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		et_.setOnKeyListener(this);

		Button b = (Button) findViewById(R.id.button);
		b.setOnClickListener(this);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Container c = new Container();
		c.api = api_;
		c.lv = lv_;
		c.searching = searching_;
		return c;
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
		return false;
	}

	/**
	 * Searches api with user text.
	 */
	public void onClick(View v) {
		if (!searching_ && (et_.getText().length() > 0)) {
			if (!Functions.isNetworkAvailable()) {
				new AlertDialog.Builder(this)
						.setMessage(R.string.error_require_internet)
						.setPositiveButton(android.R.string.ok, null)
						.show();
			} else {
				searching_ = true;
				api_ = new ApiSearchPerson(this);
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
		DialogInterface.OnClickListener onPositiveClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				PersonTable.insertRow(api_.getData((int) id));
			}
		};

		new AlertDialog.Builder(this).setMessage(R.string.search_add_item)
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, onPositiveClick)
				.show();

		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.additem_added))
				.setPositiveButton(android.R.string.ok, null).show();
	}

	/**
	 * Hardware search button pressed, focus search text.
	 * 
	 * @inheritDoc
	 */
	@Override
	public boolean onSearchRequested() {
		et_.requestFocus();
		return false;
	}
}
