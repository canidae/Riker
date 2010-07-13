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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import net.exent.riker.metadata.Album;
import net.exent.riker.metadata.Artist;
import net.exent.riker.metadata.Metafile;
import net.exent.riker.metadata.Track;
import org.jaudiotagger.tag.FieldKey;

/**
 * Class for searching MusicBrainz.
 */
public final class MusicBrainz {
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = new Logger(MusicBrainz.class);
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
		LOG.info("Loading album with MBID \"", mbid, "\"");
		Artist artist = null;
		List<Track> tracks = new ArrayList<Track>();
		try {
			delay();
			URL url = new URL("http://musicbrainz.org/ws/1/release/" + mbid + "?type=xml&inc=tracks+artist+release-events+labels+artist-rels+url-rels");
			LOG.info("Connecting to MusicBrainz: ", url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			/* fast xml parsing with stax, very fragile */
			XMLStreamReader xml = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(connection.getInputStream()));
			int depth = 0;
			String lastElement = null;
			Map<String, String> values = new HashMap<String, String>();
			while (xml.hasNext()) {
				switch (xml.next()) {
					case XMLStreamConstants.START_ELEMENT:
						++depth;
						lastElement = xml.getLocalName();
						if (depth == 2 && "release".equals(lastElement)) {
							/* album id & type */
							values.put("album_mbid", xml.getAttributeValue(null, "id"));
							values.put("album_type", xml.getAttributeValue(null, "type"));
						} else if (depth == 3 && "artist".equals(lastElement)) {
							/* artist id */
							values.put("artist_mbid", xml.getAttributeValue(null, "id"));
						} else if (depth == 4 && "event".equals(lastElement)) {
							String date = xml.getAttributeValue(null, "date");
							if (date != null && (values.get("album_released") == null || date.compareTo(values.get("album_released")) < 0)) {
								values.put("album_released", date);
							}
						} else if (depth == 4 && "track".equals(lastElement)) {
							/* track id */
							values.put("track_mbid", xml.getAttributeValue(null, "id"));
						}
						break;

					case XMLStreamConstants.CHARACTERS:
						if (depth == 3 && "title".equals(lastElement)) {
							/* album title */
							values.put("album_title", xml.getText());
						} else if (depth == 4 && "name".equals(lastElement)) {
							/* artist name */
							values.put("artist_name", xml.getText());
						} else if (depth == 4 && "sort-name".equals(lastElement)) {
							/* artist sortname */
							values.put("artist_sortname", xml.getText());
						} else if (depth == 5 && "duration".equals(lastElement)) {
							/* track duration */
							values.put("track_duration", xml.getText());
						} else if (depth == 5 && "title".equals(lastElement)) {
							/* track title */
							values.put("track_title", xml.getText());
						}
						break;

					case XMLStreamConstants.END_ELEMENT:
						if (depth == 3 && "artist".equals(xml.getLocalName())) {
							artist = new Artist(values.get("artist_name"), values.get("artist_sortname"), values.get("artist_mbid"));
						} else if (depth == 4 && "track".equals(xml.getLocalName())) {
							try {
								int trackduration = Integer.parseInt(values.get("track_duration"));
								Track tr = new Track(artist, values.get("track_title"), values.get("track_mbid"), tracks.size() + 1, trackduration);
								tracks.add(tr);
							} catch (NumberFormatException e) {
								LOG.warning(e);
							}
						} else if (depth == 2 && "release".equals(xml.getLocalName())) {
							Album album = new Album(artist, values.get("album_title"), values.get("album_released"), values.get("album_type"), values.get("album_mbid"), tracks);
							LOG.info("Album loaded: ", album);
							return album;
						}
						--depth;
						break;

					default:
						break;
				}
			}
			xml.close();
		} catch (FactoryConfigurationError e) {
			LOG.warning(e);
		} catch (IOException e) {
			LOG.warning(e);
		} catch (XMLStreamException e) {
			LOG.warning(e);
		}
		LOG.notice("Unable to load album with MBID \"", mbid, "\"");
		return null;
	}

	/**
	 * Search MusicBrainz for tracks matching the given file.
	 * Note that the albums returned are not complete albums, they only contain 1 track as well as limited data!
	 * @param metafile the file we'll create a search query from
	 * @return a list of albums containing matching tracks
	 */
	public static synchronized List<Album> searchTrack(Metafile metafile) {
		LOG.info("Searching MusicBrainz for track matching file: ", metafile);
		/* create search query */
		int lastSlash = metafile.filename().lastIndexOf(File.separatorChar);
		String lastDirectory = escape(metafile.filename().substring(metafile.filename().lastIndexOf(File.separatorChar, lastSlash - 1) + 1, lastSlash));
		String basename = escape(metafile.filename().substring(lastSlash + 1, metafile.filename().lastIndexOf('.')));

		StringBuilder query = new StringBuilder();
		/* track number */
		String tracknum = escape(metafile.getFirst(FieldKey.TRACK));
		if (tracknum == null) {
			/* TODO: attempt to get tracknum from basename */
		} else {
			query.append("tnum:").append(tracknum).append(' ');
		}
		/* duration */
		int duration = metafile.getAudioHeader().getTrackLength();
		if (duration > 0) {
			int lower = Math.max(0, duration / 1000 - 10);
			int upper = duration / 1000 + 10;
			query.append("qdur:[").append(lower).append(" TO ").append(upper).append("] ");
		}
		/* artist */
		String artist = escape(metafile.getFirst(FieldKey.ARTIST));
		query.append("artist:(");
		if (artist != null)
			query.append(artist).append(' ');
		query.append(lastDirectory).append(' ').append(basename).append(") ");
		/* title */
		String title = escape(metafile.getFirst(FieldKey.TITLE));
		query.append("track:(");
		if (title != null)
			query.append(title).append(' ');
		query.append(basename).append(") ");
		/* release */
		String album = escape(metafile.getFirst(FieldKey.ALBUM));
		query.append("release:(");
		if (artist != null)
			query.append(album).append(' ');
		query.append(lastDirectory).append(' ').append(basename).append(") ");

		/* fetch result */
		List<Album> trackAlbums = new ArrayList<Album>();
		try {
			delay();
			URL url = new URL("http://musicbrainz.org/ws/1/track/?type=xml&limit=25&query=" + URLEncoder.encode(query.toString(), "UTF-8"));
			LOG.info("Connecting to MusicBrainz: ", url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			/* fast xml parsing with stax, very fragile */
			XMLStreamReader xml = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(connection.getInputStream()));
			int depth = 0;
			String lastElement = null;
			Map<String, String> values = new HashMap<String, String>();
			while (xml.hasNext()) {
				switch (xml.next()) {
					case XMLStreamConstants.START_ELEMENT:
						++depth;
						lastElement = xml.getLocalName();
						if (depth == 3 && "track".equals(lastElement)) {
							/* track id */
							values.put("track_mbid", xml.getAttributeValue(null, "id"));
						} else if (depth == 4 && "artist".equals(lastElement)) {
							/* artist id */
							values.put("artist_mbid", xml.getAttributeValue(null, "id"));
						} else if (depth == 5 && "release".equals(lastElement)) {
							/* album id & type */
							values.put("album_mbid", xml.getAttributeValue(null, "id"));
							values.put("album_type", xml.getAttributeValue(null, "type"));
						} else if (depth == 6 && "track-list".equals(lastElement)) {
							/* track offset */
							values.put("track_offset", xml.getAttributeValue(null, "offset"));
						}
						break;

					case XMLStreamConstants.CHARACTERS:
						if (depth == 4 && "title".equals(lastElement)) {
							/* track title */
							values.put("track_title", xml.getText());
						} else if (depth == 4 && "duration".equals(lastElement)) {
							/* track duration */
							values.put("track_duration", xml.getText());
						} else if (depth == 5 && "name".equals(lastElement)) {
							/* artist name */
							values.put("artist_name", xml.getText());
						} else if (depth == 6 && "title".equals(lastElement)) {
							/* album title */
							values.put("album_title", xml.getText());
						}
						break;

					case XMLStreamConstants.END_ELEMENT:
						if (depth == 3 && "track".equals(xml.getLocalName())) {
							try {
								int tracknumber = Integer.parseInt(values.get("track_offset")) + 1;
								int trackduration = Integer.parseInt(values.get("track_duration"));
								Artist ar = new Artist(values.get("artist_name"), values.get("artist_mbid"));
								Track tr = new Track(ar, values.get("track_title"), values.get("track_mbid"), tracknumber, trackduration);
								List<Track> tracks = new ArrayList<Track>();
								tracks.add(tr);
								Album al = new Album(values.get("album_title"), values.get("album_type"), values.get("album_mbid"), tracks);
								trackAlbums.add(al);
							} catch (NumberFormatException e) {
								LOG.warning(e);
							}
							values.clear();
						}
						--depth;
						break;

					default:
						break;
				}
			}
			xml.close();
		} catch (FactoryConfigurationError e) {
			LOG.warning(e);
		} catch (IOException e) {
			LOG.warning(e);
		} catch (XMLStreamException e) {
			LOG.warning(e);
		}
		LOG.info("Returning list of matching albums: ", trackAlbums);
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
		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < text.length(); ++a) {
			switch (text.charAt(a)) {
				case ':':
				case '+':
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
	 * May only send a request to MusicBrainz every second, delay a bit if we recently sent a request.
	 */
	private static synchronized void delay() {
		long delayTime = lastRequestTime - System.currentTimeMillis() + 1000;
		if (delayTime > 0) {
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				/* this never really happens */
				LOG.warning(e, "Unable to delay query to MusicBrains");
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}
}
