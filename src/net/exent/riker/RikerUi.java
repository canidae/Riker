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

import net.exent.riker.metadata.Metafile;

/**
 * All Riker user interfaces must implement this interface.
 */
public interface RikerUi {

	/**
	 * Called by Riker when a file has been loaded.
	 * @param metafile the file that was loaded
	 */
	void fileLoaded(Metafile metafile);

	/**
	 * Called by Riker when all files have been loaded.
	 * Method may be called multiple times if user adds more files/directories to be loaded.
	 */
	void allFilesLoaded();

	/**
	 * Called by Riker when all files have been matched.
	 * Method may be called multiple times if user adds more files/directories to be loaded.
	 */
	void allFilesMatched();
}
