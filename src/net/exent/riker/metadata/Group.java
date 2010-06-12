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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A group is a collection of files that we believe come from the same album.
 */
public class Group {
	/**
	 * Map of all groups.
	 */
	private static Map<String, Group> groups = Collections.synchronizedMap(new HashMap<String, Group>());
	/**
	 * Name of current group.
	 */
	private String name;
	/**
	 * List of files in group.
	 */
	private List<Metafile> files = new ArrayList<Metafile>();

	/**
	 * Default constructor.
	 * @param name the name of the group
	 */
	public Group(String name) {
		this.name = name;
		groups.put(name, this);
	}

	/**
	 * Get map of all groups.
	 * @return map of all groups
	 */
	public static Map<String, Group> groups() {
		return Collections.unmodifiableMap(groups);
	}

	/**
	 * Get the name of the group.
	 * @return the name of the group
	 */
	public String name() {
		return name;
	}

	/**
	 * Set the name of the group.
	 * @param name the name of the group.
	 */
	public void name(String name) {
		this.name = name;
	}

	/**
	 * Add a file to group, if file already is in the group then nothing happens.
	 * @param file file to be added to group
	 */
	public void addFile(Metafile file) {
		if (!files.contains(file))
			files.add(file);
	}

	/**
	 * Remove a file from group, if file isn't in group then nothing happens.
	 * @param file file to be removed from group
	 */
	public void removeFile(Metafile file) {
		files.remove(file);
	}

	/**
	 * Get the files in the group, returned list is unmodifiable.
	 * @return the files in the group
	 */
	public List<Metafile> files() {
		return Collections.unmodifiableList(files);
	}

	@Override
	public String toString() {
		return name;
	}
}
