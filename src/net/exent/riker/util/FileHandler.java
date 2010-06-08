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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.exent.riker.metadata.MetaFile;
import net.exent.riker.metadata.Track;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

/**
 * Class for reading/writing files.
 */
public final class FileHandler implements Runnable {
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = new Logger(FileHandler.class);
	/**
	 * The only instance of this class.
	 */
	private static FileHandler handler = new FileHandler();
	/**
	 * A list of directories/files to load.
	 */
	private List<String> loadQueue = Collections.synchronizedList(new ArrayList<String>());
	/**
	 * A map of files to save and the metadata it should be saved with.
	 */
	private Map<MetaFile, Track> saveQueue = Collections.synchronizedMap(new HashMap<MetaFile, Track>());
	/**
	 * A map of loaded files.
	 * TODO: get rid off
	 */
	private Map<String, AudioFile> files = Collections.synchronizedMap(new HashMap<String, AudioFile>());
	/**
	 * Whether the thread is active.
	 */
	private boolean active;

	/**
	 * Private constructor as we only want one instance of FileHandler.
	 */
	private FileHandler() {
	}

	/**
	 * Add a directory/file to the load queue.
	 * @param path the directory/file to add to the load queue
	 */
	public static void load(String path) {
		handler.loadQueue.add(path);
		handler.wake();
	}

	/**
	 * Add a file to the queue of files to be saved.
	 * @param file the file to be saved
	 * @param track track metadata to be stored in this file
	 */
	public static void save(MetaFile file, Track track) {
		handler.saveQueue.put(file, track);
		handler.wake();
	}

	/**
	 * Start the thread.
	 */
	public static void start() {
		if (!handler.active) {
			handler.active = true;
			new Thread(handler).run();
		}
	}

	/**
	 * Stop the thread.
	 */
	public static void stop() {
		handler.active = false;
		handler.wake();
	}

	/**
	 * Load and save files.
	 */
	@Override
	public synchronized void run() {
		while (active) {
			boolean matchFiles = false;
			while (active && loadQueue.size() > 0) {
				String path = loadQueue.remove(0);
				File file = new File(path);
				if (file.isDirectory()) {
					/* add all files/directories in this directory to the loadQueue */
					for (File f : file.listFiles())
						loadQueue.add(f.getAbsolutePath());
				} else if (file.isFile()) {
					/* try to read the file as an MetaFile */
					if (files.containsKey(path)) {
						LOG.info("Skipping loading a file as it's already loaded: " + path);
					} else {
						try {
							MetaFile metaFile = new MetaFile(AudioFileIO.read(file));
							files.put(path, metaFile);
							matchFiles = true;
						} catch (CannotReadException e) {
							LOG.notice(e, "Unable to open file: " + path);
						} catch (IOException e) {
							LOG.notice(e, "Failed reading file: " + path);
						} catch (TagException e) {
							LOG.notice(e, "Unable to read tag from file: " + path);
						} catch (ReadOnlyFileException e) {
							LOG.notice(e, "Unable to open readonly file: " + path);
						} catch (InvalidAudioFrameException e) {
							LOG.notice(e, "Unable to parse audio frame in file: " + path);
						}
					}
				} else {
					LOG.notice("Hmm, an entry in the load queue that is neither a directory nor a file? " + path);
				}
			}
			if (matchFiles) {
				/* all files read, start matching.
				 * note that matching will start immediately when reading is done,
				 * if user adds more directories/files later,
				 * then all the files will run through the Matcher again.
				 */
				/* TODO: call Riker.filesLoaded() or something, let that class do this instead */
				/* create groups based on album mbid/album title/directory path, audio format, samplerate and channels */
				Map<String, List<AudioFile>> groups = new HashMap<String, List<AudioFile>>();
				for (Map.Entry<String, AudioFile> file : files.entrySet()) {
					String path = file.getKey();
					AudioFile audioFile = file.getValue();
					AudioHeader header = audioFile.getAudioHeader();
					Tag tag = audioFile.getTag();
					String group = tag.getFirst(FieldKey.MUSICBRAINZ_RELEASEID);
					if (group == null || "".equals(group))
						group = tag.getFirst(FieldKey.ALBUM);
					if (group == null || "".equals(group))
						group = path.substring(0, path.lastIndexOf(File.separatorChar));
					if (group == null)
						group = "<none>";
					group = group + " (" + header.getFormat() + ", " + header.getSampleRate() + ", " + header.getChannels() + ")";

					if (groups.containsKey(group)) {
						groups.get(group).add(audioFile);
					} else {
						List<AudioFile> audioFiles = new ArrayList<AudioFile>();
						audioFiles.add(audioFile);
						groups.put(group, audioFiles);
					}
				}
				/* feed groups to matchers */
			}
			while (active && saveQueue.size() > 0) {
			}
			if (active && loadQueue.size() <= 0 && saveQueue.size() <= 0)
				sleep();
		}
	}

	/**
	 * Make thread go to sleep.
	 */
	private synchronized void sleep() {
		try {
			wait();
		} catch (InterruptedException e) {
			/* this never really happens */
			LOG.warning(e, "Could not put thread to sleep");
		}
	}

	/**
	 * Wake up thread if it went to sleep.
	 */
	private synchronized void wake() {
		notify();
	}
}
