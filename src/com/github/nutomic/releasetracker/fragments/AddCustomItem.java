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

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.nutomic.releasetracker.R;
import com.github.nutomic.releasetracker.SqlItem;
import com.github.nutomic.releasetracker.activities.AddItem;
import com.github.nutomic.releasetracker.sql.ItemTable;
import com.github.nutomic.releasetracker.sql.SqlHelper;

/**
 * Add an item or person to the SQL database via direct input.
 */
public class AddCustomItem extends SherlockFragment implements OnClickListener {

	private AddItem activity_;

	private EditText title_;
	private EditText artist_;
	private DatePicker date_;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.add_custom_item, container, false);
		activity_ = (AddItem) getActivity();

		title_ = (EditText) v.findViewById(R.id.title);
		artist_ = (EditText) v.findViewById(R.id.artist);
		date_ = (DatePicker) v.findViewById(R.id.date);

		Button b = (Button) v.findViewById(R.id.submit);
		b.setOnClickListener(this);

		return v;
	}

	public void onClick(View v) {
		if (title_.getText().toString().trim().isEmpty()) {
			new AlertDialog.Builder(activity_)
					.setMessage(getString(R.string.addcustom_missing_title))
					.setPositiveButton(android.R.string.ok, null).show();
			return;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(date_.getYear(), date_.getMonth(), date_.getDayOfMonth());
		ItemTable.insertRow(new SqlItem(-1, title_.getText().toString(),
				artist_.getText().toString(), new Date(calendar
						.getTimeInMillis()), "", SqlHelper.API_CUSTOM, false,
				false));

		title_.setText("");
		artist_.setText("");

		new AlertDialog.Builder(activity_)
				.setMessage(getString(R.string.additem_added))
				.setPositiveButton(android.R.string.ok, null).show();
	}
}