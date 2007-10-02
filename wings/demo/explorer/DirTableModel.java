/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of the wingS demo (http://j-wings.org).
 *
 * The wingS demo is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package explorer;

import java.io.*;
import java.util.*;
import java.text.*;

import javax.swing.event.*;
import javax.swing.table.*;
import org.wings.SResourceIcon;
import org.wings.*;

/**
 * TODO: documentation
 *
 * @author Rene Thol
 * @version $Revision$
 */
public class DirTableModel
    extends AbstractTableModel
    implements FileRendererIcons
{
    private static FilenameFilter DEFAULT_FILENAMEFILTER =
        new FilenameFilter() {
            public boolean accept(File d, String name) {
                return true;
            }
        };

    private File directory = null;

    private FilenameFilter filenameFilter = DEFAULT_FILENAMEFILTER;

    private String[]  filenames = {};
    private boolean[] dirType = {};

    private final HashMap filenameCache = new HashMap();

    public DirTableModel() {
    }

    public DirTableModel(File dir) {
        setDirectory(dir);
    }

    public void setDirectory(File d) {
        setDirectory(d, null);
    }

    public String getColumnName(int column) {
        switch(column) {
        case 0:
            return "";
        case 1:
            return "Name";
        case 2:
            return "Modified";
        case 3:
            return "Size";
        }

        return "";
    }

    public boolean isCellEditable(int row, int column) {
        if (column == 1)
            return true;
        else
            return false;
    }

    public File getDirectory() { return directory; }

    public void setDirectory(File d, FilenameFilter f) {
        if ( d==null || !d.isDirectory() ) {
            directory = null;
            filenames = new String[0];
        } 
        else {
            if ( f!=null ) {
                filenameFilter = f;
            }
            
            directory = d;
            filenames = directory.list(filenameFilter);
            if (filenames != null) { // cannot access directory ?
                Arrays.sort(filenames);
                dirType = new boolean[filenames.length];
                for (int i=0; i < filenames.length; ++i) {
                    // I hate generating objects like this..
                    dirType[i] = (new File(d, filenames[i])).isDirectory();
                }
            }
            else {
                filenames = new String[0];
            }
        }
        fireTableStructureChanged();
    }

    protected void reset() {
        setDirectory(directory, filenameFilter);
    }

    public Object getValueAt(int row, int col) {

        switch ( col ) {
        case 0:
            return dirType[row] ? DIR_ICON : FILE_ICON;
        case 1:
            return filenames[row];
        case 2:
            return new Date(getFile(filenames[row]).lastModified());
        case 3:
            return new Long(getFile(filenames[row]).length());
        }

        return null;
    }

    public void setValueAt(Object aValue, int row, int column) {
        if (column == 1) {
            setFileName(row, (String)aValue);

            fireTableChanged(new TableModelEvent(this, row, row, column));
        }
    }

    public void setFileName(int row, String name) {
        // avoid moving a File by renaming it !!
        if ( name.indexOf("..")<0 &&
             name.indexOf("/")<0 &&
             name.indexOf("\\")<0 ) {
            File oldFile = getFile(filenames[row]);
            File newFile = new File(oldFile.getParent() + File.separator + name);
            
            filenames[row] = name;
            
            oldFile.renameTo(newFile);
        } 
    }

    public File getFile(String filename) {
        File f = (File) filenameCache.get(filename);

        if ( f==null ) {
            try {
                f = (new File(directory, filename)).getCanonicalFile();
            }
            catch (IOException e) {}
            //f = new File(directory.getAbsolutePath() + File.separator + filename);
            filenameCache.put(filename, f);
        }

        return f;
    }

    public File getFileAt(int row) {
        return (File) filenameCache.get(filenames[row]);
    }

    public int getRowCount() {
        return filenames.length;
    }

    public int getColumnCount() {
        return 4;
    }

    public Class getColumnClass(int col) {
        switch ( col ) {
        case 0:
            return SIcon.class;
        case 2:
            return Date.class;
        case 3:
            return Long.class;
        default:
            return String.class;
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
