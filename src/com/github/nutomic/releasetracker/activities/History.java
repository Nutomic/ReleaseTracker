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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.nutomic.releasetracker.R;
import com.github.nutomic.releasetracker.sql.ItemTable;
import com.github.nutomic.releasetracker.sql.SqlCursorAdapter;

public class History extends SherlockActivity {

	ResourceCursorAdapter adapter_;

	/**
	 * Initializes views.
	 * 
	 * @inheritDoc
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		adapter_ = new SqlCursorAdapter(this, R.layout.listview_item, null,
				false);

		ListView lv = (ListView) findViewById(R.id.list);
		registerForContextMenu(lv);
		lv.setAdapter(adapter_);
		lv.setEmptyView(findViewById(R.id.empty));
		lv.setStackFromBottom(true);
	}

	/**
	 * Get a new cursor in case the old one was invalidated.
	 */
	@Override
	public void onStart() {
		super.onStart();
		adapter_.changeCursor(ItemTable.getDataCursor(true));
	}

	/**
	 * Create ListView item context menu.
	 * 
	 * @inheritDoc
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, final View v,
			final ContextMenu.ContextMenuInfo menuInfo) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		OnMenuItemClickListener listener = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(android.view.MenuItem item) {
				switch (item.getItemId()) {
				case 0:
					new AlertDialog.Builder(History.this)
							.setMessage(R.string.home_delete_item)
							.setNegativeButton(android.R.string.no, null)
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											ItemTable.deleteRow(info.id);
											adapter_.changeCursor(ItemTable
													.getDataCursor(true));
										}
									}).show();
					return true;
				}
				return false;
			}
		};
		menu.setHeaderTitle(adapter_.getCursor().getString(
				ItemTable.FIELD_TITLE));
		menu.add(getString(R.string.contextmenu_delete))
				.setOnMenuItemClickListener(listener);
		menu.add(getString(R.string.contextmenu_link_open)).setIntent(
				new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.themoviedb.org/movie/"
								+ adapter_.getCursor().getString(
										ItemTable.FIELD_API_ID))));
	}
}