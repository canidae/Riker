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
package net.exent.riker.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data class containing album information.
 */
public class Album {
	/**
	 * Map of all known albums.
	 */
	private static Map<String, Album> albums = Collections.synchronizedMap(new HashMap<String, Album>());
	/**
	 * Artist of the album.
	 */
	private Artist artist;
	/**
	 * Title of the album.
	 */
	private String title;
	/**
	 * Release date of the album.
	 */
	private String released;
	/**
	 * Album type (compilation, single, EP, etc.).
	 */
	private String type;
	/**
	 * MBID of the album.
	 */
	private String mbid;
	/**
	 * List of tracks found on album.
	 */
	private List<Track> tracks;

	/**
	 * Full constructor.
	 * @param artist the artist of the album
	 * @param title the title of the album
	 * @param released when the album was released
	 * @param type the type of the album
	 * @param mbid the MBID of the album
	 * @param tracks the tracks on the album
	 */
	public Album(Artist artist, String title, String released, String type, String mbid, List<Track> tracks) {
		this.artist = artist;
		this.title = title;
		this.released = released;
		this.type = type;
		this.mbid = mbid;
		this.tracks = tracks;
		for (Track track : tracks)
			track.album(this);
		albums.put(mbid, this);
	}

	/**
	 * Partial constructor for track search, won't add album to map of albums as we don't get artist nor release date from track search.
	 * @param title the title of the album
	 * @param type the type of the album
	 * @param mbid the MBID of the album
	 * @param tracks the tracks on the album
	 */
	public Album(String title, String type, String mbid, List<Track> tracks) {
		this.title = title;
		this.type = type;
		this.mbid = mbid;
		this.tracks = tracks;
		for (Track track : tracks)
			track.album(this);
	}

	/**
	 * Get map of all known albums.
	 * @return unmodifiable map of all known albums
	 */
	public static Map<String, Album> albums() {
		return Collections.unmodifiableMap(albums);
	}

	/**
	 * Get the artist of the album.
	 * @return the artist of the album
	 */
	public Artist artist() {
		return artist;
	}

	/**
	 * Get the title of the album.
	 * @return the title of the album
	 */
	public String title() {
		return title;
	}

	/**
	 * Get the release date of the album.
	 * @return the release date of the album
	 */
	public String released() {
		return released;
	}

	/**
	 * Get the album type (compilation, single, EP, etc.).
	 * @return the album type (compilation, single, EP, etc.)
	 */
	public String type() {
		return type;
	}

	/**
	 * Get the album MBID.
	 * @return the album MBID
	 */
	public String mbid() {
		return mbid;
	}

	/**
	 * Get the tracks on the album.
	 * @return the tracks on the album
	 */
	public List<Track> tracks() {
		return tracks;
	}
}
