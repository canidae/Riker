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
import java.util.Map;

/**
 * Data class containing track information.
 */
public class Track {
	/**
	 * Map of all known tracks.
	 */
	private static Map<String, Track> tracks = Collections.synchronizedMap(new HashMap<String, Track>());
	/**
	 * Album track can be found on.
	 */
	private Album album;
	/**
	 * Artist of the track.
	 */
	private Artist artist;
	/**
	 * Title of the track.
	 */
	private String title;
	/**
	 * MBID of the track.
	 */
	private String mbid;
	/**
	 * Tracknumber of track.
	 */
	private int tracknumber;
	/**
	 * Duration of track in milliseconds.
	 */
	private int duration;

	/**
	 * Full constructor.
	 * @param artist the artist of this track
	 * @param title the title of the track
	 * @param mbid the MBID of the track
	 * @param tracknumber the tracknumber of the track
	 * @param duration the duration of the track in milliseconds
	 */
	public Track(Artist artist, String title, String mbid, int tracknumber, int duration) {
		this.artist = artist;
		this.title = title;
		this.mbid = mbid;
		this.tracknumber = tracknumber;
		this.duration = duration;
		tracks.put(mbid, this);
	}

	/**
	 * Get map of all known tracks.
	 * @return unmodifiable map of all known tracks
	 */
	public static Map<String, Track> tracks() {
		return Collections.unmodifiableMap(tracks);
	}

	/**
	 * Set album track belongs to.
	 * This method got default access modifier (package modifier) as only Album should call this method.
	 * @param album album this track belongs to
	 */
	void album(Album album) {
		this.album = album;
	}

	/**
	 * Get the album the track is on.
	 * @return the album this track is on
	 */
	public Album album() {
		return album;
	}

	/**
	 * Get the artist for this track.
	 * @return the artist for this track
	 */
	public Artist artist() {
		return artist;
	}

	/**
	 * Get the title of the track.
	 * @return the title of the track.
	 */
	public String title() {
		return title;
	}

	/**
	 * Get the MBID of the track.
	 * @return the MBID of the track
	 */
	public String mbid() {
		return mbid;
	}

	/**
	 * Get the tracknumber of the track.
	 * @return the tracknumber of the track
	 */
	public int tracknumber() {
		return tracknumber;
	}

	/**
	 * Get the duration of the track.
	 * @return the duration of the track
	 */
	public int duration() {
		return duration;
	}
}
