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
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * a column reader is like a LineNumberReader, but it can report
 * its line and column position.
 */
public class ColumnReader extends Reader {
    private final BufferedReader in;
    private final File thisFile;
    private final Position current;
    private final Position markPos;

    public ColumnReader(File f) throws IOException {
	in = new BufferedReader(new FileReader(f));
	thisFile = f.getCanonicalFile();
	current = new Position();
	markPos = new Position();
    }

    public boolean markSupported() { return in.markSupported(); }
    
    public void close() throws IOException {
	in.close();
    }

    public void mark(int readAheadLimit) throws IOException {
	in.mark(readAheadLimit);
	markPos.copyFrom(current);
    }
    
    public void reset() throws IOException {
	in.reset();
	current.copyFrom(markPos);
    }
    
    public int read() throws IOException {
	int c = in.read();
	if (c == '\r' || c == '\n')
	    current.incRow(c);
	else
	    current.incCol();
	return c;
    }
    
    public int read(char cbuf[], int off, int len) throws IOException {
	int n = in.read(cbuf, off, len);
	for (int i = off; i < off + n; ++i) {
	    char c = cbuf[i];
	    if (c == '\r' || c == '\n')
		current.incRow(c);
	    else
		current.incCol();
	}
	return n;
    }

    public FilePosition getFilePosition() { 
	return new FilePosition(thisFile, current);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
