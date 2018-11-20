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

package net.bible.android.control.link;

import net.bible.service.common.Constants;

import org.apache.commons.lang3.StringUtils;

/** Analyse typical standard Sword uri: sword://module/key
 *  e.g. sword://StrongsRealGreek/01909
 *  Job.3.3
 *  
 *  Also And Bible specific links:
 *  
 *  see http://www.crosswire.org/wiki/Frontends:URI_Standard
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 */
public class UriAnalyzer {
	public enum DocType {BIBLE, GREEK_DIC, HEBREW_DIC, ROBINSON, ALL_GREEK, ALL_HEBREW, SPECIFIC_DOC} 
	
	private DocType docType = DocType.BIBLE;
	private String book;
	private String key = "";
	
	public boolean analyze(String uri) {
		// check for urls like gdef:01234
		
        String protocol;
        String ref;

        // split the prefix from the book
		if (!uri.contains(":")) {
			protocol = Constants.BIBLE_PROTOCOL;
			ref = uri;
		} else {
			String[] uriTokens = uri.split(":");
	        protocol = uriTokens[0];
	        ref = uriTokens[1];
		}

        // Doc type
        if (Constants.SWORD_PROTOCOL.equals(protocol)) {
        	docType = DocType.SPECIFIC_DOC;
        } else if (Constants.BIBLE_PROTOCOL.equals(protocol)) {
        	docType = DocType.BIBLE;
        } else if (Constants.GREEK_DEF_PROTOCOL.equals(protocol)) {
        	docType = DocType.GREEK_DIC;
        } else if (Constants.HEBREW_DEF_PROTOCOL.equals(protocol)) {
        	docType = DocType.HEBREW_DIC;
        } else if (Constants.ROBINSON_GREEK_MORPH_PROTOCOL.equals(protocol)) {
        	docType = DocType.ROBINSON;
        } else if (Constants.ALL_GREEK_OCCURRENCES_PROTOCOL.equals(protocol)) {
        	docType = DocType.ALL_GREEK;
        } else if (Constants.ALL_HEBREW_OCCURRENCES_PROTOCOL.equals(protocol)) {
        	docType = DocType.ALL_HEBREW;
        } else {
        	// not a valid Strongs Uri
        	return false;
        }
        
        // Document
        if (StringUtils.isEmpty(ref)) {
        	return false;
        }
        
        // remove the first 2 slashes from the url e.g. //module/key
        ref = StringUtils.strip(ref, "/");
		if (!ref.contains("/")) {
			key = ref;
		} else {
			int firstSlash = ref.indexOf("/");
	        book = ref.substring(0, firstSlash);
	        
	        // handle uri like sword://Bible/John.17.11 found in Calvin's commentary avoiding any attempt to find a book named Bible that will fail
	        if (Constants.BIBLE_PROTOCOL.equalsIgnoreCase(book)) {
	        	docType = DocType.BIBLE;
	        }
	        
	        // safe to grab after slash because slash can't be on end due to above strip("/") 
	        key = ref.substring(firstSlash+1);
		}
        
        // handled this url (or at least attempted to)
        return true;
	}

	public DocType getDocType() {
		return docType;
	}
	public String getBook() {
		return book;
	}
	public String getKey() {
		return key;
	}
}
