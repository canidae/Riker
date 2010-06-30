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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.exent.riker.gui.Riker;
import net.exent.riker.metadata.Metafile;
import net.exent.riker.metadata.Track;

/**
 * Class for matching metadata from a group of files with metadata from MusicBrainz.
 */
public class Matcher implements Runnable {
	/**
	 * List of files to compare with data from MusicBrainz.
	 */
	private List<Metafile> files;
	/**
	 * If set, only match files with this album.
	 */
	private String albumMbid;
	/**
	 * Comparisons between a track and metafiles.
	 */
	private Map<Track, Map<Metafile, Double>> comparison = new HashMap<Track, Map<Metafile, Double>>();

	/**
	 * Default constructor.
	 * @param files the files we wish to compare against data from Musicbrainz
	 */
	public Matcher(List<Metafile> files) {
		this.files = files;
		new Thread(this).run();
	}

	/**
	 * Constructor with specified MBID for album to compare files with.
	 * @param files the files we wish to compare against data from Musicbrainz
	 * @param albumMbid MBID of album to compare files with
	 */
	public Matcher(List<Metafile> files, String albumMbid) {
		this.files = files;
		this.albumMbid = albumMbid;
		new Thread(this).run();
	}

	/**
	 * Compare the files with data from MusicBrainz.
	 */
	@Override
	public void run() {
		if (albumMbid == null) {
			/* search tracks on musicbrainz */
			List<Metafile> queue = files.subList(0, files.size());
			while (!queue.isEmpty()) {
				Metafile file = queue.remove(0);
				/* if we got album mbid, look that up first */
				/* if not, search track */
				/* load album[s] */
				/* compare all files with the album[s] we loaded and remove good matches from queue */
			}
		} else {
			/* only match files with given album */
		}
		Riker.filesMatched(files);
	}

	/**
	 * Compare a metafile with a track.
	 * @param file the Metafile to compare
	 * @param track the Track to compare
	 * @return a value between 0.0 and 1.0 where 0.0 is complete mismatch and 1.0 is perfect match
	 */
	private double compareMetafileWithTrack(Metafile file, Track track) {
		return 0.0;
	}
}
