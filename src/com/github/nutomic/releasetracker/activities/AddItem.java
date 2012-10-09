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

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.nutomic.releasetracker.R;
import com.github.nutomic.releasetracker.TabsAdapter;
import com.github.nutomic.releasetracker.fragments.AddCustomItem;
import com.github.nutomic.releasetracker.fragments.Search;

/**
 * Tabbed Activity to add an item or person to the SQL database, either through
 * API or by direct input.
 */
public class AddItem extends SherlockFragmentActivity {

	TabHost tabHost_;
	ViewPager viewPager_;
	TabsAdapter tabsAdapter_;

	/**
	 * Creates tabs.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_item);
		tabHost_ = (TabHost) findViewById(android.R.id.tabhost);
		tabHost_.setup();

		viewPager_ = (ViewPager) findViewById(R.id.pager);

		tabsAdapter_ = new TabsAdapter(this, tabHost_, viewPager_);

		tabsAdapter_
				.addTab(tabHost_.newTabSpec("search").setIndicator(
						getString(R.string.additem_search)), Search.class, null);
		tabsAdapter_.addTab(
				tabHost_.newTabSpec("custom").setIndicator(
						getString(R.string.additem_custom)),
				AddCustomItem.class, null);
	}

	/**
	 * Restores tab that was last opened in this activity, no matter if that was
	 * for items or persons.
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		tabHost_.setCurrentTabByTag(savedInstanceState.getString("tab"));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", tabHost_.getCurrentTabTag());
		super.onSaveInstanceState(outState);
	}
}