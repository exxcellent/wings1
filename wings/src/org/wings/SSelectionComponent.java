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

package org.wings;

import org.wings.style.*;

/**
 * better name?
 * this interface is provided for convenience
 */
public interface SSelectionComponent
{
    void setSelectionStyle(String selectionStyle);
    String getSelectionStyle();

    /*
    void setSelectionAttribute(String name, String value);
    String getSelectionAttribute(String name);
    String removeSelectionAttribute(String name);
    */
    void setSelectionAttributes(AttributeSet selectionAttributes);
    AttributeSet getSelectionAttributes();
}
