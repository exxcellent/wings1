/*
 * $Id$
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://www.j-wings.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package org.wings.session;



/**
 * An error handling which will be notified on an uncatched exceptions occuring
 * during event processing and rendering process.
 *
 * <p>This is currently the only way to get notified about uncatched Exceptions inside
 * event listeners or table cell renderers.
 *
 * @author bschmid
 * TODO: Allow exceptions handler to really "handle" exceptions i.e. also component-wise.
 */
public interface ExceptionHandler {
    /**
     * @param e Exceptionn thrown
     */
    void handleException(Throwable e);

}
