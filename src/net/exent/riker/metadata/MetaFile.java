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
	 * Get default group name of this file.
	 * @return group name of file
	 */
	public String groupName() {
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
}
