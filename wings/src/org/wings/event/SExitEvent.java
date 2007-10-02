/*
 * $Id$
 * (c) Copyright 2002 wingS development team.
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
package org.wings.event;

import org.wings.session.Session;
import java.util.EventObject;

/**
 * SExitEvent.java
 *
 *
 * Created: Fri Dec  6 11:07:21 2002
 *
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SExitEvent extends EventObject {
    
    /**
     * 
     */
    public SExitEvent(Session source) {
        super(source);
    }
    
}// SExitEvent

/*
   $Log$
   Revision 1.3  2005/01/16 01:01:26  oliverscheck
   Project URL modified to reflect new domain j-wings.org.

   Revision 1.2  2003/12/10 20:58:58  hzeller
   o some indentation stuff and adding source headers..

   Revision 1.1  2002/12/10 17:15:30  arminhaaf
   o support for exit listener

*/
