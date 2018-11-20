/*
 * Copyright (c) 2018 Martin Denham, Tuomas Airaksinen and the And Bible contributors.
 *
 * This file is part of And Bible (http://github.com/AndBible/and-bible).
 *
 * And Bible is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * And Bible is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with And Bible.
 * If not, see http://www.gnu.org/licenses/.
 *
 */

package net.bible.android.control.mynote;

import androidx.annotation.NonNull;

import net.bible.service.db.mynote.MyNoteDto;

import java.util.Comparator;

/**
 * Sort MyNotes by create date, most recent first
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */

public class MyNoteCreationDateComparator implements Comparator<MyNoteDto> {

	public int compare(@NonNull MyNoteDto myNote1, @NonNull MyNoteDto myNote2) {
		// descending order
		return myNote2.getCreatedOn().compareTo(myNote1.getCreatedOn());
	}

}
