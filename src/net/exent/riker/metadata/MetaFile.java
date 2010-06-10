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
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;

/**
 * A MetaFile is an AudioFile with some more data.
 */
public class MetaFile extends AudioFile {
	/**
	 * The group this metafile belongs to.
	 */
	private Group group;
	/**
	 * The track this metafile match.
	 */
	private Track track;

	/**
	 * Default constructor.
	 * @param audioFile the audiofile this metafile will extend
	 */
	public MetaFile(AudioFile audioFile) {
		super(audioFile.getFile(), audioFile.getAudioHeader(), audioFile.getTag());
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
	 * Set the track this metafile match.
	 * @param track the track this metafile match
	 */
	public void track(Track track) {
		this.track = track;
	}

	/**
	 * Create group name from metadata in file.
	 * @return group name of file
	 */
	public String createGroupName() {
		String groupName = getTag().getFirst(FieldKey.MUSICBRAINZ_RELEASEID);
		if (groupName == null || "".equals(groupName))
			groupName = getTag().getFirst(FieldKey.ALBUM);
		if (groupName == null || "".equals(groupName)) {
			String path = getFile().getAbsolutePath();
			groupName = path.substring(0, path.lastIndexOf(File.separatorChar));
		}
		if (groupName == null)
			groupName = "<none>";
		return groupName + " (" + getAudioHeader().getFormat() + ", " + getAudioHeader().getSampleRate() + ", " + getAudioHeader().getChannels() + ")";
	}

	/**
	 * Get the file name of this metafile.
	 * @return file name of metafile
	 */
	public String fileName() {
		return getFile().getAbsolutePath();
	}

	@Override
	public String toString() {
		return fileName();
	}
}
