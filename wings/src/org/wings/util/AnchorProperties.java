/*
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
package org.wings.util;

import org.wings.SimpleURL;

/**
 * This class contains the properties for the AnchorStack.
 */
public final class AnchorProperties {
    private final SimpleURL url;
    private final String toolTipText;
    private final String target;
    private final String formEventName;
    private final String formEventValue;

    public AnchorProperties(SimpleURL url, String target, String toolTipText) {
        this.url = url;
        this.target = target;
        this.formEventName = null;
        this.formEventValue = null;
        this.toolTipText = toolTipText;
    }

    public AnchorProperties(String pFormEventName, String pFormEventValue,
                            String toolTipText) {
        formEventName = pFormEventName;
        formEventValue = pFormEventValue;
        this.url = null;
        this.target = null;
        this.toolTipText = toolTipText;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public SimpleURL getURL() {
        return url;
    }

    public String getTarget() {
        return target;
    }

    public String getFormEventName() {
        return formEventName;
    }

    public String getFormEventValue() {
        return formEventValue;
    }

    public boolean isFormAnchor() {
        return formEventName != null;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
