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

package org.wingx.plaf;

import org.wings.DynamicResource;
import org.wings.plaf.ContainerCG;
import org.wingx.XCalendar;

/**
 *
 *  * @author <a href="mailto:e.habicht@thiesen.com">Erik Habicht</a>
 */
public interface CalendarCG extends ContainerCG {
    public DynamicResource installStyleSheet(XCalendar comp);
}
/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
