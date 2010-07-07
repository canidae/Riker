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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.exent.riker.Riker;
import net.exent.riker.metadata.Metafile;
import org.jaudiotagger.audio.AudioFileIO;

/**
 * Class for reading/writing files.
 */
public class FileHandler implements Runnable {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = new Logger(FileHandler.class);
	/**
	 * A reference to the user interface that created this FileHandler.
	 */
	private Riker riker;
	/**
	 * A list of directories/files to load.
	 */
	private List<String> loadQueue = Collections.synchronizedList(new ArrayList<String>());
	/**
	 * A map of files to save and the metadata it should be saved with.
	 */
	private List<Metafile> saveQueue = Collections.synchronizedList(new ArrayList<Metafile>());
	/**
	 * Whether the thread is active.
	 */
	private boolean active;

	/**
	 * Default constructor.
	 * @param riker reference to the user interface creating this FileHandler
	 */
	public FileHandler(Riker riker) {
		this.riker = riker;
	}

	/**
	 * Add a directory/file to the load queue.
	 * @param path the directory/file to add to the load queue
	 */
	public void load(String path) {
		LOG.info("Loading files from path: ", path);
		loadQueue.add(path);
		wake();
	}

	/**
	 * Add a file to the queue of files to be saved.
	 * @param file the file to be saved
	 */
	public void save(Metafile file) {
		LOG.info("Adding file to save queue: ", file);
		saveQueue.add(file);
		wake();
	}

	/**
	 * Start the thread.
	 */
	public synchronized void start() {
		if (!active) {
			LOG.info("Starting thread");
			active = true;
			new Thread(this).start();
		}
	}

	/**
	 * Stop the thread.
	 */
	public synchronized void stop() {
		if (active) {
			LOG.info("Stopping thread");
			active = false;
			wake();
		}
	}

	/**
	 * Load and save files.
	 */
	@Override
	public void run() {
		while (active) {
			boolean filesLoaded = false;
			while (active && loadQueue.size() > 0) {
				String path = loadQueue.remove(0);
				File file = new File(path);
				if (file.isDirectory()) {
					/* add all files/directories in this directory to the loadQueue */
					for (File f : file.listFiles()) {
						loadQueue.add(f.getAbsolutePath());
					}
				} else if (file.isFile()) {
					/* try to read the file as an MetaFile */
					try {
						LOG.info("Reading file: ", file.getAbsolutePath());
						riker.fileLoaded(new Metafile(AudioFileIO.read(file)));
						filesLoaded = true;
					} catch (Exception e) {
						LOG.notice(e, "Unable to read file: ", path);
						/* TODO: Riker.fileLoadFailed(file, e) */
					}
				} else {
					LOG.notice("Hmm, an entry in the load queue that is neither a directory nor a file(?): ", path);
				}
			}
			if (filesLoaded) {
				riker.filesLoaded();
			}
			boolean filesSaved = false;
			while (active && saveQueue.size() > 0) {
				Metafile metafile = saveQueue.remove(0);
				try {
					LOG.info("Saving file: ", metafile.filename());
					metafile.commit();
					/* TODO: Riker.fileSaved(audioFile); */
					filesSaved = true;
				} catch (Exception e) {
					LOG.warning(e, "Could not save file: ", metafile.filename());
					/* TODO: Riker.fileSaveFailed(audioFile, e); */
				}
			}
			if (filesSaved) {
				/* TODO: Riker.filesSaved(); */
			}
			if (active && loadQueue.size() <= 0 && saveQueue.size() <= 0) {
				sleep();
			}
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
