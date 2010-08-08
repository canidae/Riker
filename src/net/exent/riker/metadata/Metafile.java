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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.exent.riker.util.Levenshtein;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;

/**
 * A MetaFile is an AudioFile with some more data.
 */
public class Metafile extends AudioFile {

	/**
	 * The group this metafile belongs to.
	 */
	private Group group;
	/**
	 * The track this metafile match.
	 */
	private Track track;
	/**
	 * How well the metafile match the track.
	 */
	private double matchScore;
	/**
	 * A list of semi-unique strings found in metadata and filename.
	 */
	private List<String> stringValues = new ArrayList<String>();

	/**
	 * Default constructor.
	 * @param audioFile the audiofile this metafile will extend
	 */
	public Metafile(AudioFile audioFile) {
		super(audioFile.getFile(), audioFile.getAudioHeader(), audioFile.getTag());
	}

	/**
	 * Create group name from metadata in file.
	 * @return group name of file
	 */
	public String createGroupName() {
		String groupName = getFirst(FieldKey.MUSICBRAINZ_RELEASEID);
		if (groupName == null) {
			groupName = getFirst(FieldKey.ALBUM);
		}
		if (groupName == null) {
			String path = getFile().getAbsolutePath();
			groupName = path.substring(0, path.lastIndexOf(File.separatorChar));
		}
		if (groupName == null) {
			groupName = "<none>";
		}
		return groupName + " - " + getAudioHeader().getFormat() + " - " + getAudioHeader().getSampleRate() + " - " + getAudioHeader().getChannels();
	}

	/**
	 * Update the list of string values.
	 */
	public void updateStringValues() {
		stringValues.clear();
		/* add interesting metadata to list of string values */
		String tmp = getFirst(FieldKey.ALBUM);
		if (tmp != null) {
			stringValues.add(tmp);
		}
		tmp = getFirst(FieldKey.ALBUM_ARTIST);
		if (tmp != null) {
			stringValues.add(tmp);
		}
		tmp = getFirst(FieldKey.ARTIST);
		if (tmp != null) {
			stringValues.add(tmp);
		}
		tmp = getFirst(FieldKey.TITLE);
		if (tmp != null) {
			stringValues.add(tmp);
		}
		tmp = getFirst(FieldKey.TRACK);
		if (tmp != null) {
			stringValues.add(tmp);
		}
		/* add interesting strings from last directory name to list of string values unless a similar value already exist in list */
		int lastSlash = filename().lastIndexOf(File.separatorChar);
		String directory = filename().substring(filename().lastIndexOf(File.separatorChar, lastSlash - 1) + 1, lastSlash).replace('_', ' ');
		for (String value : directory.split("-")) {
			value = value.trim();
			boolean valueExists = false;
			for (String listValue : stringValues) {
				if (Levenshtein.similarity(value, listValue) >= 0.8) {
					valueExists = true;
					break;
				}
			}
			if (!valueExists) {
				stringValues.add(value);
			}
		}
		/* add interesting strings from base filename to list of string values unless a similar value already exist in list */
		String basename = filename().substring(lastSlash + 1, filename().lastIndexOf(".")).replace('_', ' ');
		for (String valueTmp : basename.split("-")) {
			for (String value : valueTmp.split("\\.")) {
				value = value.trim();
				boolean valueExists = false;
				for (String listValue : stringValues) {
					if (Levenshtein.similarity(value, listValue) >= 0.8) {
						valueExists = true;
						break;
					}
				}
				if (!valueExists) {
					stringValues.add(value);
				}
			}
		}
	}

	/**
	 * Get a list of semi-unique strings found in metadata and filename.
	 * @return a list of semi-unique strings found in metadata and filename
	 */
	public List<String> stringValues() {
		if (stringValues.size() <= 0) {
			updateStringValues();
		}
		return stringValues;
	}

	/**
	 * Get the group this metafile belongs to.
	 * @return the group this metafile belongs to
	 */
	public Group group() {
		return group;
	}

	/**
	 * Set the group this metafile belongs to.
	 * @param group the group this metafile belongs to
	 */
	public void group(Group group) {
		this.group = group;
	}

	/**
	 * Get the track this metafile match.
	 * @return the track this metafile match
	 */
	public Track track() {
		return track;
	}

	/**
	 * Get how well the metafile matched the track.
	 * @return how well the metafile matched the track
	 */
	public double matchScore() {
		return matchScore;
	}

	/**
	 * Set the track this metafile match.
	 * @param track the track this metafile match
	 * @param matchScore how well the metafile matched the track
	 */
	public void track(Track track, double matchScore) {
		this.track = track;
		this.matchScore = matchScore;
	}

	/**
	 * Get the filename of this metafile.
	 * @return filename of metafile
	 */
	public String filename() {
		return getFile().getAbsolutePath();
	}

	@Override
	public String toString() {
		return filename();
	}

	/**
	 * Get first value for given field.
	 * This is a wrapper for getFirst() in Tag as that seems to return "" when field is not set, instead of null.
	 * Method will also trim() the string to remove leading and trailing whitespaces.
	 * @param key the field to get value from
	 * @return the value of the given field
	 */
	public String getFirst(FieldKey key) {
		String value = getTag().getFirst(key);
		if (value == null) {
			return value;
		}
		value = value.trim();
		if ("".equals(value)) {
			return null;
		}
		return value;
	}
}
