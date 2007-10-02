/*
 * $Id$
 * (c) Copyright 2001 wingS development team.
 *
 * This file is part of a wingS demo (http://j-wings.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package explorer;

import org.wings.SIcon;
import org.wings.SResourceIcon;

public interface FileRendererIcons {
    public final static SIcon DIR_ICON =
        new SResourceIcon(FileRendererIcons.class.getClassLoader(),
                              "explorer/Directory.gif");

    public final static SIcon FILE_ICON =
        new SResourceIcon(FileRendererIcons.class.getClassLoader(),
                              "explorer/File.gif");
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
