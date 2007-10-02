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

/**
 * Position in a file. This is a row and a column. This position is updated
 * by the reader ..
 */
public final class Position {
    private int row;
    private int col;
    private int oldChar;

    public Position() { row = 1; col = 0; }
    public Position(Position other) { 
	row = other.row; 
	col = other.col; 
	oldChar = other.oldChar;
    }

    /**
     * increment row, reset col.
     * The row-delimiter is given in the parameter to allow this class
     * to decide, whether the row really needs to be counted: CR followed
     * by LF is counted once.
     */
    public void incRow(int c) { 
	// LF only -> Unix; CR only -> Mac ; CRLF -> Dos
	if (oldChar == '\r' && c == '\n') // don't count twice on CRLF
	    return;
	++row; col=0;
	oldChar = c;
    }
    public void incCol() { ++col; }
    public void copyFrom(Position other) { 
	row = other.row; 
	col = other.col; 
	oldChar = other.oldChar;
    }
    public String toString() { return row + ":" + col; }
}
/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
