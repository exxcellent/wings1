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

/**
 * A Position in a file.
 */
public final class FilePosition {
    private File  thisFile;
    private Position pos;
    
    public FilePosition(File currentFile, Position pos) {
        this.thisFile     = currentFile;
        this.pos  = new Position(pos);
    }

    public boolean isSameFile(FilePosition other) {
        return other.thisFile.equals(thisFile);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(thisFile.getAbsolutePath());
        result.append(':').append(pos.toString());
        return result.toString();
    }

    /**
     * print relative to the given working directory. If the file
     * is up to three hierarchies up, then the path is printed
     * with means of '../../'. Otherwise the full absolute path
     * is printed.
     */
    public String toString(File cwd) {
        StringBuffer result = new StringBuffer();
        result.append(printRelative(cwd, thisFile));
        result.append(':').append(pos.toString());
        return result.toString();
    }
    
    /**
     * prints the current file relative to the given directory.
     * Both files are expected to be relative.
     */
    private String printRelative(File directory, File f) {
        // file in subdirectory ? Just do a simple string-compare.
        if (f.getAbsolutePath().startsWith(directory.getAbsolutePath())) {
            String rel =f.getAbsolutePath()
		.substring(directory.getAbsolutePath().length());
            return (rel.startsWith(File.separator)
                    ? rel.substring(1) : rel);
        }
        // no, now check, whether we are at most three ../../../ back
        File fileDir = f.getParentFile();
	int depth;
        if (fileDir == null) return f.getName();
        for (depth=1; depth < 3; ++depth) {
            directory = directory.getParentFile();
            if (directory == null)
                return f.getAbsolutePath(); // cannot handle this.
            if (fileDir.equals(directory))
                break;
        }
        if (fileDir.equals(directory)) {
            StringBuffer result = new StringBuffer();
            for (int i = 1; i <= depth; ++i) {
                result.append("..");
                result.append(File.separatorChar);
            }
            result.append(f.getName());
            return result.toString();
        }
        return f.getAbsolutePath();
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
