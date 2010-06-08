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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * A group is a collection of files that we believe come from the same album.
 * This class keeps track of all groups and files connected to the groups.
 */
public class Group {
	/**
	 * Map of all groups.
	 */
	private static Map<String, Group> groups = new HashMap<String, Group>();
	/**
	 * List of files in a group.
	 */
	private List<MetaFile> files = new ArrayList<MetaFile>();

	/**
	 * Private constructor to prevent external instantiation.
	 * @param file first file to be added to the group
	 */
	protected Group(MetaFile file) {
		files.add(file);
	}

	/**
	 * Get all files in this group as an unmodifiable List.
	 * @return an unmodifiable list of files in this group
	 */
	public List<MetaFile> files() {
		return Collections.unmodifiableList(files);
	}

	/**
	 * Add a file to the group it belongs to.
	 * @param groupName the group this file should be placed in
	 * @param file the file to be added
	 */
	public static void addFile(String groupName, MetaFile file) {
		if (groups.containsKey(groupName))
			groups.get(groupName).files.add(file);
		else
			groups.put(groupName, new Group(file));
	}

	/**
	 * Get all groups as an unmodifiable Map.
	 * @return an unmodifiable map of all groups
	 */
	public static Map<String, Group> groups() {
		return Collections.unmodifiableMap(groups);
	}
}
