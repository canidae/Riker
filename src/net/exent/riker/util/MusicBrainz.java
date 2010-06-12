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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.exent.riker.metadata.Album;
import net.exent.riker.metadata.Metafile;
import org.jaudiotagger.tag.FieldKey;

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
		return null;
	}

	/**
	 * Search MusicBrainz for tracks matching the given file.
	 * Note that the albums returned are not complete albums, they only contain 1 track as well as limited data!
	 * @param file the file we'll create a search query from
	 * @return a list of albums containing matching tracks
	 */
	public static synchronized List<Album> searchTrack(Metafile file) {
		/* create search query */
		int lastSlash = file.fileName().lastIndexOf(File.separatorChar);
		String lastDirectory = escape(file.fileName().substring(file.fileName().lastIndexOf(File.separatorChar, lastSlash - 1) + 1, lastSlash));
		String basename = escape(file.fileName().substring(lastSlash + 1, file.fileName().lastIndexOf('.')));

		StringBuffer query = new StringBuffer("query=");
		/* track number */
		String tracknum = escape(file.getTag().getFirst(FieldKey.TRACK));
		if (tracknum == null) {
			/* TODO: attempt to get tracknum from basename */
		} else {
			query.append("tnum:").append(tracknum).append(' ');
		}
		/* duration */
		int duration = file.getAudioHeader().getTrackLength();
		if (duration > 0) {
			int lower = Math.max(0, duration / 1000 - 10);
			int upper = duration / 1000 + 10;
			query.append("qdur:[" + lower).append(" TO " + upper).append("] ");
		}
		/* artist */
		String artist = escape(file.getTag().getFirst(FieldKey.ARTIST));
		query.append("artist:(");
		if (artist != null)
			query.append(artist).append(' ');
		query.append(lastDirectory).append(' ').append(basename).append(") ");
		/* title */
		String title = escape(file.getTag().getFirst(FieldKey.TITLE));
		query.append("track:(");
		if (title != null)
			query.append(title).append(' ');
		query.append(basename).append(") ");
		/* release */
		String album = escape(file.getTag().getFirst(FieldKey.ALBUM));
		query.append("release:(");
		if (artist != null)
			query.append(album).append(' ');
		query.append(lastDirectory).append(' ').append(basename).append(") ");

		List<Album> trackAlbums = new ArrayList<Album>();
		return trackAlbums;
	}

	/**
	 * Escape special characters that mess up Lucene query.
	 * @param text the text to be escaped
	 * @return the escaped version of the text
	 */
	private static String escape(String text) {
		if (text == null)
			return null;
		text = text.trim();
		if ("".equals(text))
			return null;
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < text.length(); ++a) {
			switch (text.charAt(a)) {
				case '$':
					sb.append("%24");
					break;

				case '+':
					sb.append("\\%2b");
					break;

				case ',':
					sb.append("%2c");
					break;

				case '/':
					sb.append("%2f");
					break;

				case ':':
					sb.append("\\%3a");
					break;

				case '=':
					sb.append("%3d");
					break;

				case '@':
					sb.append("%40");
					break;


				case '-':
				case '!':
				case '(':
				case ')':
				case '{':
				case '}':
				case '[':
				case ']':
				case '^':
				case '"':
				case '~':
				case '*':
				case '\\':
					sb.append('\\');
					sb.append(text.charAt(a));
					break;

				case '|':
				case '&':
					if (a + 1 < text.length() && text.charAt(a + 1) == text.charAt(a))
						sb.append('\\');
					sb.append(text.charAt(a));
					break;

				case '_':
				case '?':
				case ';':
				case '#':
					sb.append(' ');
					break;

				default:
					sb.append(text.charAt(a));
					break;
			}
		}
		return sb.toString().trim();
	}

	/**
	 * Fetch response from remote server.
	 * @param url URL to remote server
	 */
	private static void fetch(URL url) {
		/* make sure we don't hassle the remote server too much */
		delay();
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
