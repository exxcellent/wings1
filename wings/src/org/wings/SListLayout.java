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

import java.io.IOException;
import java.util.ArrayList;
import org.wings.plaf.*;
import org.wings.io.Device;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:mreinsch@to.com">Michael Reinsch</a>
 * @version $Revision$
 */
public class SListLayout
    extends SAbstractLayoutManager
{
    /**
     * TODO: documentation
     */
    protected String listType = SConstants.UNORDERED_LIST;

    /**
     * TODO: documentation
     */
    protected String orderType = null;

    /**
     * TODO: documentation
     */
    protected int start = 0;

    /**
     * TODO: documentation
     */
    protected ArrayList components = new ArrayList(2);
    private SContainer container = null;


    /**
     * TODO: documentation
     *
     */
    public SListLayout() {
    }

    public void addComponent(SComponent c, Object constraint, int index) {
        components.add(index, c);
    }

    /**
     * TODO: documentation
     *
     */
    public void removeComponent(SComponent c) {
        components.remove(c);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public SComponent getComponentAt(int i) {
        return (SComponent)components.get(i);
    }

    /**
     * TODO: documentation
     *
     * @param c
     */
    public void setContainer(SContainer c) {
        container = c;
    }


    /**
     * Sets the list type. Use one of the following types:
     * <UL>
     *  <LI>{@link SConstants#ORDERED_LIST}
     *  <LI>{@link SConstants#UNORDERED_LIST}
     *  <LI>{@link SConstants#MENU_LIST}
     *  <LI>{@link SConstants#DIR_LIST}
     * </UL>
     * null sets default list type.
     */
    public void setListType(String t) {
        if ( t != null )
            listType = t;
        else
            listType = SConstants.UNORDERED_LIST;
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public String getListType() {
        return listType;
    }

    /**
     * null sets default order type.
     */
    public void setOrderType( String t ) {
        orderType = t;
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public String getOrderType() {
        return orderType;
    }


    /**
     * Sets the list type and order. Use one of the following types:
     * <UL>
     *  <LI>{@link SConstants#ORDER_TYPE_CIRCLE}
     *  <LI>{@link SConstants#ORDER_TYPE_SQUARE}
     *  <LI>{@link SConstants#ORDER_TYPE_DISC}
     *  <LI>{@link SConstants#ORDER_TYPE_BIG_ALPHA}
     *  <LI>{@link SConstants#ORDER_TYPE_SMALL_ALPHA}
     *  <LI>{@link SConstants#ORDER_TYPE_NUMBER}
     *  <LI>{@link SConstants#ORDER_TYPE_NORMAL}
     *  <LI>{@link SConstants#ORDER_TYPE_BIG_ROMAN}
     *  <LI>{@link SConstants#ORDER_TYPE_SMALL_ROMAN}
     * </UL>
     * null sets default list and order type.
     */
    public void setType(String[] t)
    {
        if ( t == null ) {
            setListType( (String) null );
            setOrderType( (String) null );
        }
        if ( t!=null && t.length==2 ) {
            setListType( t[0] );
            setOrderType( t[1] );
        }
    }

    /**
     * TODO: documentation
     *
     */
    public void setStart(int s) {
        start = s;
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public int getStart() {
        return start;
    }


    /**
     * TODO: documentation
     *
     * @param s
     * @throws IOException
     */
    public void write(Device s)
        throws IOException
    {
        s.print("<").print( getListType() );

        if ( getOrderType() != null )
            s.print(" type=").print( getOrderType() );

        if ( getStart() > 0 )
            s.print(" start=").print( getStart() );

        s.print(">\n");

        for ( int i = 0; i < components.size(); i++ ) {
            SComponent comp = (SComponent) components.get( i );
            if ( comp.isVisible() ) {
                s.print("<LI>");
                comp.write(s);
                s.print("</LI>\n");
            }
        }

        s.print("</").print( getListType() ).print(">\n");
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
