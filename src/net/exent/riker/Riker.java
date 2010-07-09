/*
 *  The MIT License
 * 
 *  Copyright 2010 canidae.
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
package net.exent.riker;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.exent.riker.gui.RikerGui;
import net.exent.riker.metadata.Group;
import net.exent.riker.metadata.Metafile;
import net.exent.riker.util.FileHandler;
import net.exent.riker.util.Logger;
import net.exent.riker.util.Matcher;

/**
 * Main class, organizes threads & logic.
 * A user interface must be supplied to this class.
 */
public final class Riker {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = new Logger(Riker.class);
	/**
	 * Reference to the user interface.
	 */
	private static RikerUi rikerUi;
	/**
	 * Map of all groups.
	 */
	private static Map<String, Group> groups = Collections.synchronizedMap(new HashMap<String, Group>());
	/**
	 * Set of active matchers.
	 */
	private static Map<Matcher, List<Metafile>> matchers = Collections.synchronizedMap(new HashMap<Matcher, List<Metafile>>());

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Riker() {
	}

	/**
	 * Get all groups.
	 * @return all groups
	 */
	public static Map<String, Group> groups() {
		return groups;
	}

	/**
	 * Main method.
	 * @param args arguments to Riker
	 */
	public static void main(String... args) {
		RikerGui rg = new RikerGui();
		rg.setVisible(true);
		Riker.rikerUi = rg;
		FileHandler.start();
		FileHandler.load("/home/canidae/Music");
	}

	/**
	 * Called by Matchers when they're done matching files.
	 */
	public static void matcherFinished(Matcher matcher) {
		LOG.info("Matcher finished: " + matcher);
		matchers.remove(matcher);
	}

	/**
	 * Called by FileHandler when it reads a new file.
	 * @param metafile the file just read
	 */
	public static void fileLoaded(Metafile metafile) {
		LOG.info("Adding Metafile to Riker: ", metafile);
		String groupName = metafile.createGroupName();
		Group group = groups.get(groupName);
		if (group == null) {
			group = new Group(groupName);
			groups.put(groupName, group);
		}
		group.addFile(metafile);
		metafile.group(group);
		/* tell the UI that a file was loaded */
		rikerUi.fileLoaded(metafile);
	}

	/**
	 * Called by FileHandler when all files in queue are loaded.
	 */
	public static void allFilesLoaded() {
		LOG.info("Done loading files");
		for (Map.Entry<String, Group> groupEntry : groups.entrySet()) {
			List<Metafile> files = groupEntry.getValue().files();
			Matcher matcher = new Matcher(files);
			matchers.put(matcher, files);
			matcher.start();
		}
		/* tell the UI that we're done loading files */
		rikerUi.allFilesLoaded();
	}
}
