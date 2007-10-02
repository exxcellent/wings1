/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
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

package org.wings.batch;

import java.util.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.wings.*;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class BatchFrame
    extends SFrame
{
    public BatchFrame() {
	setRequestURL(new RequestURL("base", "base"));
    }
}
