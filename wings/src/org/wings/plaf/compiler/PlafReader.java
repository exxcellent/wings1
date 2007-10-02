/* -*- java -*-
 * $Id$
 * (c) Copyright 2001 wingS development team.
 *
 * This file is part of wingS (http://j-wings.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings.plaf.compiler;

import java.io.File;
import java.io.Reader;
import java.io.IOException;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

/**
 * Input source for Plaf files with a java.io.Reader like
 * interface.
 */
public class PlafReader extends Reader {
    private ColumnReader in;
    private File    currentFile;
    private final File    cwd;
    private final Stack  fileStack;  // Stack<File>
    private final Map    openFiles;  // Map<File,ColumnReader>

    public PlafReader(File cwd, String file) throws IOException {
        in = null;
        currentFile = null;
        this.cwd = cwd;
	fileStack = new Stack();
	openFiles = new HashMap();
	open(file);
    }

    /**
     * open a new file. The following reading will be from the new
     * file, until EOF is reached. Then, reading goes on at the position
     * current before this open() call. This is to include files.
     */
    public void open(String fileName) throws IOException {
	File f = new File(fileName);
	// open relative files relative to the current file.
	if (!f.isAbsolute() && currentFile != null) {
	    f = new File(currentFile.getParent(), fileName);
	}
	f = f.getCanonicalFile();
        if (openFiles.containsKey(f)) {
            throw new IOException (getFileStackTrace() + 
                                   ": cannot recursively include files: '"
                                   + fileName + "'");
        }
        fileStack.push(currentFile);
        currentFile = f;
	in = new ColumnReader(f);
        openFiles.put(currentFile, in);
    }

    public void close() throws IOException {
	if (in != null) {
	    in.close();
            openFiles.remove(currentFile);
	}
        currentFile = (File) fileStack.pop();
	in = (ColumnReader) openFiles.get(currentFile);
    }
    
    public FilePosition getFilePosition() {
        if (in == null) return null;
        return in.getFilePosition();
    }

    /**
     * returns a Stack trace of current file positions.
     */
    public String getFileStackTrace() {
        StringBuffer result = new StringBuffer();
        int filesInStack = fileStack.size();
        if (filesInStack > 1) {
            result.append(getFilePosition().toString(cwd))
                .append(": here in the file, that is\n");
            for (int pos = filesInStack-1; pos > 0; --pos) {
                File file = (File) fileStack.elementAt(pos);
                ColumnReader openReader = (ColumnReader) openFiles.get(file);
                result.append(openReader.getFilePosition().toString(cwd));
                result.append(": .. included from here \n");
            }
        }
        result.append(getFilePosition().toString(cwd));
        return result.toString();
    }

    public int read()  throws IOException {
        if (in == null) return -1; // run out of files.
	int result = in.read();
	if (result == -1) {
	    close();
	    if (in == null) return -1; // run out of files.
	    return read(); // recursive retry.
	}
	return result;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        if (in == null) return -1; // run out of files.
	int result = in.read(cbuf, off, len);
	if (result == -1) {
	    close();
	    if (in == null) return -1; // run out of files.
	    return read(cbuf, off, len); // recursive retry.
	}
	return result;
    }

    // --- implementation of the remaining methods provided by reader.
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    public boolean ready() throws IOException {
        return in.ready();
    }

    public boolean markSupported() { return true; }

    public void mark(int readAheadLimit) throws IOException {
        if (in != null) in.mark(readAheadLimit);
    }
    
    public void reset() throws IOException {
        if (in != null) in.reset();
    }

}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
