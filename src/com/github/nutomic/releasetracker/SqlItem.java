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

import java.util.Date;

import com.github.nutomic.releasetracker.sql.ItemTable;

/**
 * Data container for row from {@link ItemTable}.
 */
public class SqlItem {

	public final int id;
	public final String title;
	public final String artist;
	public final Date releaseDate;
	public final String apiId;
	public final int apiType;
	public final boolean notified;
	public final boolean released;

	public SqlItem(int pId, String pTitle, String pArtist, Date date,
			String pApiId, int pApiType, boolean pNotified, boolean pReleased) {
		id = pId;
		title = pTitle;
		artist = pArtist;
		releaseDate = date;
		apiId = pApiId;
		apiType = pApiType;
		notified = pNotified;
		released = pReleased;
	}

	/**
	 * Leaves ID empty.
	 */
	public SqlItem(String pTitle, String pArtist, Date date, String pApiId,
			int pApiType, boolean pNotified, boolean pReleased) {
		this(-1, pTitle, pArtist, date, pApiId, pApiType, pNotified, pReleased);
	}
}