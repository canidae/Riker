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

/**
 * Data class containing artist information.
 */
public class Artist {

	/**
	 * Artist name.
	 */
	private String name;
	/**
	 * Artist sortname.
	 */
	private String sortname;
	/**
	 * Artist MBID.
	 */
	private String mbid;

	/**
	 * Full constructor.
	 * @param name artist name
	 * @param sortname artist sortname
	 * @param mbid MBID for artist
	 */
	public Artist(String name, String sortname, String mbid) {
		this.name = name;
		this.sortname = sortname;
		this.mbid = mbid;
	}

	/**
	 * Partial constructor for track search.
	 * @param name artist name
	 * @param mbid MBID for artist
	 */
	public Artist(String name, String mbid) {
		this.name = name;
		this.mbid = mbid;
	}

	/**
	 * Get the artist name.
	 * @return the artist name
	 */
	public String name() {
		return name;
	}

	/**
	 * Get the artist sortname.
	 * @return the artist sortname
	 */
	public String sortname() {
		return sortname;
	}

	/**
	 * Get the artist MBID.
	 * @return the artist MBID
	 */
	public String mbid() {
		return mbid;
	}
}
