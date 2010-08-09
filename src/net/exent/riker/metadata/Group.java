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
import java.util.List;

/**
 * A group is a collection of files that we believe come from the same album.
 */
public class Group {
	/**
	 * Name of current group.
	 */
	private String name;
	/**
	 * List of files in group.
	 */
	private List<Metafile> files = new ArrayList<Metafile>();
	/**
	 * TODO: List of albums loaded for this group, but wasn't used (ie. potentially matching albums).
	 */
	private List<Album> albums = new ArrayList<Album>();

	/**
	 * Default constructor.
	 * @param name the name of the group
	 */
	public Group(String name) {
		this.name = name;
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
	 * Get the files in the group.
	 * @return the files in the group
	 */
	public List<Metafile> files() {
		return files;
	}

	@Override
	public String toString() {
		return name + " (" + files.size() + " files)";
	}
}
