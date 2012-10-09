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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.github.nutomic.releasetracker.sql.ItemTable;

/**
 * Regularly checks for items that have been released, and shows a notification
 * if any are found.
 */
public class NotificationService extends Service {

	@Override
	public void onCreate() {
		ItemTable.checkReleased();
		Cursor cursor = ItemTable.getReleasedTitles();
		if (cursor.getCount() != 0) {
			String titles = "";
			while (!cursor.isLast()) {
				titles += cursor.getString(ItemTable.FIELD_TITLE) + " ";
				cursor.moveToNext();
			}
			titles = titles.substring(0, titles.length() - 1);
			new Notification.Builder(this)
					.setContentTitle(
							(cursor.getCount() == 1) ? getString(R.string.notification_released_single)
									: getString(R.string.notification_released_single))
					.setContentText(titles)
					.setTicker(titles)
					.setNumber(cursor.getCount())
					.setLargeIcon(
							BitmapFactory.decodeResource(getResources(),
									R.drawable.ic_launcher))
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(
							PendingIntent.getActivity(this, 0, new Intent(
									"com.github.nutomic.releasetracker.Home"),
									0)).setOngoing(false).setAutoCancel(true);
		}
		cursor.close();
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}