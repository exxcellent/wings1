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

package org.wings.plaf.xhtml;

import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SFlowLayout;
import org.wings.SLayoutManager;
import org.wings.io.Device;
import org.wings.plaf.LayoutCG;

import java.io.IOException;
import java.util.List;

public class FlowLayoutCG
implements LayoutCG {
  /**
   * @param d the device to write the code to
   * @param l the layout manager
   * @throws IOException
   */
  public void write(Device d, SLayoutManager l)
  throws IOException {
    SFlowLayout layout = (SFlowLayout)l;
    List components = layout.getComponents();
    int orientation = layout.getOrientation();
    int alignment = layout.getAlignment();
    SComponent container = ( SComponent ) layout.getContainer();
    
    if (components.size() > 0) {
      switch (alignment) {
        case SConstants.RIGHT_ALIGN:
          d.print("<div align=\"right\">");
          break;
        case SConstants.CENTER_ALIGN:
          d.print("<div align=\"center\">");
          break;
      }
      
      int count = 0;
      for (int i=0;  i < components.size(); i++) {
        SComponent comp = (SComponent)components.get(i);
        if (comp.isVisible()) {
          if (count == 0) {
            d.print("<table cellpadding=\"0\" cellspacing=\"0\"");
            // CGUtil.writeSize( d, container );
            if ( Utils.hasSpanAttributes( container ) ) {
              d.print(" style=\"");
              Utils.writeSpanAttributes( d, (SComponent) container );
              d.print("\" ");
            }
            
            d.print("><tr><td");
            Utils.printTableCellAttributes(d, comp);
          } else {
              if (orientation == SConstants.VERTICAL){
              d.print("</td></tr>\n<tr><td");
              Utils.printTableCellAttributes(d, comp);
              } else {
                  d.print("</td><td");
                  Utils.printTableCellAttributes(d, comp);
              }
          }

          SComponent c = ((SComponent)components.get(i));
          Utils.printTableCellAlignment(d, c);

          if ( Utils.hasSpanAttributes(c) || ( layout.getHgap() > 0 || layout.getVgap() > 0 ) ) {
            // i.e. container width, border, etc
            d.print(" style=\"");
            Utils.writeAttributes(d,  c);
            d.print( Utils.createInlineStylesForGaps( layout.getHgap(), layout.getVgap() ).toString() );
            d.print("\"");
          }         
          d.print(">");
          
          c.write(d);
          count++;
        }
      }
      if (count > 0)
        d.print("</td></tr></table>\n");
      
      switch (alignment) {
        case SConstants.RIGHT_ALIGN:
        case SConstants.CENTER_ALIGN:
          d.print("</div>");
          break;
      }
      
    }
  }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
