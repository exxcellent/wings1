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

import java.awt.Color;
import java.io.IOException;
import java.util.*;

import org.wings.plaf.*;
import org.wings.io.Device;

/**
 * This is a grid layout
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SGridLayout
    extends SAbstractLayoutManager
{
    /**
     * @see #getCGClassID
     */
    private static final String cgClassID = "GridLayoutCG";

    private static final boolean DEBUG = true;

    /**
     * TODO: documentation
     */
    protected ArrayList components = new ArrayList(2);

    protected int rows = 1;
    protected int cols = 1;

    protected int border = 0;

    protected boolean header = false;
    protected boolean relative = false;

    protected int width = -1;
    protected int cellPadding = -1;
    protected int cellSpacing = -1;

    /**
     * The horizontal gap (in pixels) specifiying the space
     * between columns.  They can be changed at any time.
     * This should be a non-negative integer.
     */
    protected int hgap = 0;

    /**
     * The vertical gap (in pixels) which specifiying the space
     * between rows.  They can be changed at any time.
     * This should be a non negative integer.
     */
    protected int vgap = 0;
    
    /**
     * creates a new grid layout with 1 row and 1 column extent
     **/
    
    public SGridLayout() {
      this.setWidth(100);
      this.setRelative(true);
    }
    
    /**
     * creats a new grid layout with the given number of columns
     *
     * @param cols number of columns
     */
    public SGridLayout(int cols) {
        this();
        setColumns(cols);
    }

    /**
     * creats a new grid layout with the given number of columns and rows
     *
     * @param rows number of rows
     * @param cols number of columns
     */
    public SGridLayout(int rows, int cols) {
        this(cols);
        setRows(rows);
    }
    
    /**
     * creats a new grid layout with the given number of columns and rows and the given gaps
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param hgap horizontal gap
     * @param vgap vertical gap
     */
    public SGridLayout(int rows, int cols, int hgap, int vgap ) {
        this( rows, cols );
        setHgap( hgap );
        setVgap( vgap );
    }

    /**
     * sets the number of columns
     *
     * @param c number of columns
     */
    public void setColumns(int c) {
        cols = c;
    }

    /**
     * returns the number of columns
     *
     * @return number of columns
     */
    public int getColumns() { return cols; }

    /**
     * sets the number of rows
     *
     * @param r number of rows
     */
    public void setRows(int r) {
        rows = r;
    }

    /**
     * returns the number of rows
     *
     * @returns number of rows
     */
    public int getRows() { return rows; }

    public void addComponent(SComponent c, Object constraint, int index) {
        components.add(index, c);
    }

    public void removeComponent(SComponent c) {
        components.remove(c);
    }

    /**
     * returns a list of all components
     * @return all components
     */
    public List getComponents() {
        return components;
    }

    /**
     * TODO: documentation
     * @param p 
     * @deprecated 
     */
    public void setCellPadding(int p) {
        cellPadding = p;
    }
    /**
     * 
     * @deprecated 
     */
    public int getCellPadding() { return cellPadding; }

    /**
     * TODO: documentation
     * @param s 
     * @deprecated 
     */
    public void setCellSpacing(int s) {
        cellSpacing = s;
    }
    /**
     * 
     * @deprecated 
     */
    public int getCellSpacing() { return cellSpacing; }

    /**
     * Gets the horizontal gap between components in pixel. Rendered half as margin left and margin right
     * Some PLAFs might ignore this property.
     *
     * @return the horizontal gap between components
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * Sets the horizontal gap between components to the specified value in pixe. Rendered half as margin left and margin right
     * Some PLAFs might ignore this property.
     *
     * @param hgap the horizontal gap between components
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    /**
     * Gets the vertical gap between components in pixel. Rendered half as margin top and margin bottom
     * Some PLAFs might ignore this property.
     *
     * @return the vertical gap between components
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * Sets the vertical gap between components to the specified value in pixel.
     * Rendered half as margin top and margin bottom. Some PLAFs might ignore this property.
     *
     * @param vgap the vertical gap between components
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
    }
    
    /**
     * TODO: documentation
     *
     * @param pixel
     */
    public void setBorder(int pixel) {
        border = pixel;
    }
    public int getBorder() { return border; }

    /**
     * TODO: documentation
     *
     * @param percentage
     */
    public void setWidth(int width) {
        this.width = width;
    }
    public int getWidth() { return width; }

    /**
     * TODO: documentation
     *
     * @param abs
     */
    public void setRelative(boolean relative) {
        this.relative = relative;
    }
    public boolean isRelative() { return relative; }

    /**
     * TODO: documentation
     *
     * @param b
     */
    public void setHeader(boolean b) {
        header = b;
    }
    public boolean getHeader() { return header; }

    public String getCGClassID() {
        return cgClassID;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
