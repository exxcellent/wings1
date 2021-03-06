/*
  $Id$
  (c) Copyright 2002 mercatis information systems GmbH

  Part of e-lib 
 
  This file contains unpublished, proprietary trade secret information of
  mercatis information systems GmbH. Use, transcription, duplication and
  modification are strictly prohibited without prior written consent of
  mercatis information systems GmbH.
  See http://www.mercatis.de
*/

package org.wings.template;

/**
 * <!--
 * PropertyValueConverter.java
 * Created: Tue Aug  6 17:11:12 2002
 * -->
 *
 * A PropertyValueConverter is able to convert a property value (a string) to an Java object. 
 *
 * <p><b>(c)2002 <a href="http://www.mercatis.de">mercatis information systems GmbH</a></b></p>
 *
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public interface PropertyValueConverter  {

    /**
     * Describe <code>toObject</code> method here.
     *
     * @param value a <code>String</code> value
     * @param targetClass a <code>Class</code> value
     * @return an <code>Object</code> value
     * @exception IllegalArgumentException if an error occurs
     */
    public Object convertPropertyValue(String value, Class targetClass) 
	throws IllegalArgumentException;
    
}// PropertyValueConverter

/*
   $Log$
   Revision 1.2  2002/11/19 15:38:28  ahaaf
   o add doku

   Revision 1.1  2002/08/06 16:45:55  ahaaf
   add DefaultPropertyManager using reflection and bean shell scripting support

*/
