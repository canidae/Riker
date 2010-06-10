/*
 *  The MIT License
 * 
 *  Copyright 2010 Vidar Wahlberg <canidae@exent.net>.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package net.exent.riker.util;

import java.util.ArrayList;
import java.util.List;
import net.exent.riker.metadata.Album;

/**
 * Class for searching MusicBrainz.
 */
public final class MusicBrainz {
	/**
	 * The last time we sent a request to MusicBrainz.
	 * We may only send a request once per second.
	 */
	private static long lastRequestTime;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private MusicBrainz() {
	}

	/**
	 * Loan an album from MusicBrainz with the given MBID.
	 * @param mbid the MBID of the album.
	 * @return the album if found.
	 */
	public static synchronized Album loadAlbum(String mbid) {
		delay();
		return null;
	}

	/**
	 * Search MusicBrainz for albums containing a track matching the given query.
	 * Note that the albums returned are not complete albums, they only contain 1 track as well as limited data!
	 * @param query the query used to search for tracks
	 * @return a list of albums containing matching tracks
	 */
	public static synchronized List<Album> searchTrack(String query) {
		delay();
		List<Album> trackAlbums = new ArrayList<Album>();
		return trackAlbums;
	}

	/**
	 * May only send a request to MusicBrainz every second, delay a bit if we recently sent a request.
	 */
	private static synchronized void delay() {
		long delayTime = lastRequestTime - System.currentTimeMillis() + 1000;
		if (delayTime > 0) {
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				/* this never really happens */
				e.printStackTrace();
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}
}
