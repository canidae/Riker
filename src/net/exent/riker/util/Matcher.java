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
import net.exent.riker.metadata.Album;
import net.exent.riker.metadata.Metafile;
import net.exent.riker.metadata.Track;
import org.jaudiotagger.tag.FieldKey;

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
	 * Queue of files to do a track search on MusicBrainz.
	 */
	private List<Metafile> queue;

	/**
	 * Default constructor.
	 * @param files the files we wish to compare against data from Musicbrainz
	 */
	public Matcher(List<Metafile> files) {
		this.files = files;
		queue = files.subList(0, files.size());
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
			while (!queue.isEmpty()) {
				Metafile file = queue.remove(0);
				/* if we got album mbid, look that up first */
				String fileAlbumMbid = file.getFirst(FieldKey.MUSICBRAINZ_RELEASEID);
				Album album = null;
				if (fileAlbumMbid != null)
					album = loadAlbum(fileAlbumMbid);
				if (album != null) {
					compareAllMetafilesWithAlbum(album);
				} else {
					/* if not, search track */
					List<Album> albums = MusicBrainz.searchTrack(file);
					/* load best album */
					double bestScore = 0.0;
					Album bestAlbum = null;
					for (Album tmpAlbum : albums) {
						double score = compareMetafileWithTrack(file, tmpAlbum.tracks().get(0));
						if (score > bestScore) {
							bestScore = score;
							bestAlbum = album;
						}
					}
					album = loadAlbum(bestAlbum.mbid());
					/* compare all files with the album we loaded and remove good matches from queue */
					if (album != null)
						compareAllMetafilesWithAlbum(album);
				}
			}
		} else {
			/* only match files with given album */
			Album album = loadAlbum(albumMbid);
			if (album != null)
				compareAllMetafilesWithAlbum(album);
		}
		/* update metafiles with best matched track */
		/* tell Riker that we're done matching these files */
		Riker.filesMatched(files);
	}

	/**
	 * Compare all metafiles with given album.
	 * @param album the album to compare the metafiles with
	 */
	private void compareAllMetafilesWithAlbum(Album album) {
		for (Track track : album.tracks()) {
			for (Metafile file : files) {
				double score = compareMetafileWithTrack(file, track);
				/* if score is bad, don't waste memory keeping the comparison */
				if (score < 0.3)
					continue;
				/* if score is good enough, remove metafile from queue */
				if (queue != null && score > 0.5)
					queue.remove(file);
				/* save comparison */
				Map<Metafile, Double> trackComparison = comparison.get(track);
				if (trackComparison == null) {
					trackComparison = new HashMap<Metafile, Double>();
					comparison.put(track, trackComparison);
				}
				trackComparison.put(file, score);
			}
		}
	}

	/**
	 * Compare a metafile with a track.
	 * @param file the Metafile to compare
	 * @param track the Track to compare
	 * @return a value between 0.0 and 1.0 where 0.0 is complete mismatch and 1.0 is perfect match
	 */
	private double compareMetafileWithTrack(Metafile file, Track track) {
		List<String> values = file.stringValues();
		if (values.size() <= 0)
			return 0.0;
		/* calculate Levenshtein similarity of all file metadata with track metadata */
		double[][] scores = new double[4][values.size()];
		int index = 0;
		for (String value : values) {
			scores[0][index] = Levenshtein.similarity(value, track.album().title());
			scores[1][index] = Levenshtein.similarity(value, track.artist().name());
			scores[2][index] = Levenshtein.similarity(value, track.title());
			scores[3][index] = value.equals("" + track.tracknumber()) ? 1.0 : 0.0;
			++index;
		}
		/* calculate the best possible score from metadata */
		double bestScore = 0.0;
		int[] bestPath = new int[4];
		for (int albumIndex = 0; albumIndex < values.size(); ++albumIndex) {
			for (int artistIndex = 0; artistIndex < values.size(); ++artistIndex) {
				if (artistIndex == albumIndex)
					continue;
				for (int titleIndex = 0; titleIndex < values.size(); ++titleIndex) {
					if (titleIndex == artistIndex || titleIndex == albumIndex)
						continue;
					for (int tracknumIndex = 0; tracknumIndex < values.size(); ++tracknumIndex) {
						if (tracknumIndex == titleIndex || tracknumIndex == artistIndex || tracknumIndex == albumIndex)
							continue;
						double score = scores[0][albumIndex] + scores[1][artistIndex] + scores[2][titleIndex] + scores[3][tracknumIndex];
						if (score > bestScore) {
							bestScore = score;
							bestPath[0] = albumIndex;
							bestPath[1] = artistIndex;
							bestPath[2] = titleIndex;
							bestPath[3] = tracknumIndex;
						}
					}
				}
			}
		}
		/* add score from duration */
		int durationDiff = Math.abs(file.getAudioHeader().getTrackLength() - track.duration());
		if (durationDiff < 15000)
			bestScore += 1.0 - (double) durationDiff / 15000.0;
		return bestScore / 5.0;
	}

	/**
	 * Load album from cache if we already loaded it or from MusicBrainz if not.
	 * @param mbid the MBID of the album to load
	 * @return album for given MBID
	 */
	private Album loadAlbum(String mbid) {
		Album album = Album.albums().get(mbid);
		if (album == null)
			album = MusicBrainz.loadAlbum(mbid);
		return album;
	}
}
