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
 * Buffer for generated java code that behaves a bit like a StringBuffer.
 * The code append()ed is indented according to the brace structure; So
 * statements for if/while etc. are not indented correctly, if these are not
 * braced. Normal parenthesis influencing the indentation as well.
 *
 * This is a very simple parser, so strings are not handled well: if there 
 * is a '{' '}' in any String, this will influence the indent output, even 
 * though it shouldn't.
 *
 * <p>Overall: Good enough for code generation.
 */
public class JavaBuffer {
    final String indentString;
    final StringBuffer output;
    int braceDepth;
    boolean nextIndent;

    public JavaBuffer(int initialDepth, String istring) {
	output = new StringBuffer();
        braceDepth = 0;
        nextIndent = true;        // first line.
        braceDepth = initialDepth;
        indentString = istring;
    }
    
    /*
     * indent with the current depth.
     */
    private void indentIfNeeded() {
        if (nextIndent) {
            for (int i = 0; i < braceDepth; ++i) {
                output.append( indentString );
            }
            nextIndent = false;
        }
    }
    
    public JavaBuffer append(StringBuffer b) { return append(b.toString()); }

    public JavaBuffer append(String s) {
	for (int i = 0; i < s.length(); ++i) {
	    char c = s.charAt(i); 
	    switch (c) {
	    case ' ' :
	    case '\t':
		if (!nextIndent) output.append(c);
		break;
	    case '\n':
		output.append(c);
                nextIndent = true;
		break;
                // all these braces fall through the indentIfNeeded() call.
            case '(': if (c == '(') ++braceDepth; // fall through
            case ')': if (c == ')') --braceDepth; // fall through
            case '{': if (c == '{') ++braceDepth; // fall through
            case '}': if (c == '}') --braceDepth; // fall through
	    default:
                indentIfNeeded();
		output.append(c);
	    }
	}
        return this;
    }

    /**
     * removes spaces up to - and including - the first newline encountered 
     * at the end of the buffer, if any.
     */
    public void removeTailNewline() {
        int length = output.length();
        if (length == 0) return;
        boolean done = false;
        do {
            --length;
            char c = output.charAt(length);
            switch (c) {
            case ' ' :
            case '\t':
                break;
            case '\n':
                --length; // fall through.
            default:
                done = true;
            }
        }
        while (!done);
        output.setLength(length+1);
    }

    public String toString() { return output.toString(); }
    public int length()      { return output.length(); }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
