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
package org.wings.event;

import java.util.EventObject;
import org.wings.LowLevelEventListener;

/**
 * A request to an low level event was dropped due to an outdated epoch of this request.
 * @author bschmid
 * @see org.wings.event.SInvalidLowLevelEventListener
 */
public class InvalidLowLevelEvent extends EventObject {

    /**
     * Default constructtor
     * @param source The LowLevelEventListener which received the outdated request
     */
    public InvalidLowLevelEvent(LowLevelEventListener source) {
        super(source);
    }

}
