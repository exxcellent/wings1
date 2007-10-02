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
package explorer;

import javax.swing.tree.*;
import javax.swing.event.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Tree model, that works directly on top of
 * the filesystem. It internally cached the nodes it visited
 * for a period of 30 seconds so that multiple accesses of the same
 * node within a short period of time is not expensive (the rendering
 * process needs this). On the other hand: changes that are made
 * in the filesystem are reflected in the tree at most 30seconds
 * later.
 *
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 */
public class DirectoryTreeModel implements TreeModel {
    // use -1 for non-caching.
    private final static long CACHE_TIME = 30 * 1000;

    private final boolean _onlyDirs;
    private final File    _rootDir;
    private final Map     _dirCache;

    /**
     * create a new Directory Tree Model.
     * @param root The root directory.
     * @param onlyDirectories 'true' if this TreeModel should only
     *                        represent directories, no regular file nodes.
     */
    public DirectoryTreeModel(File root, boolean onlyDirectories) 
	throws IOException {
	_onlyDirs = onlyDirectories;
	_rootDir = root;
        _dirCache = new HashMap();
	if (!_rootDir.isDirectory()) {
	    throw new IOException("invalid base directory.");
	}
    }

    /**
     * create a new Directory Tree Model, that shows only directories.
     * @param root The root directory.
     */
    public DirectoryTreeModel(File root) throws IOException {
	this(root, true);
    }
    
    public Object getRoot() {
	return _rootDir;
    }
    
    /**
     * get the entries of a specific directory (sorted). The entries
     * returned here are not always retrieved from the filesystem, but
     * cached for CACHE_TIME.
     */
    private File[] getEntries(Object parent) {
        if (_dirCache.containsKey(parent)) {
            CacheEntry cacheItem = (CacheEntry) _dirCache.get(parent);
            if (!cacheItem.isExpired()) {
                return cacheItem.getEntries();
            }
        }

	File parentDir = (File) parent;
	File entries[] = parentDir.listFiles();
	if (entries == null) { // cannot access directory ?
	    return null;
	}

	if (_onlyDirs) {
	    ArrayList dirEntries = new ArrayList();
	    for (int i=0; i < entries.length; ++i) {
		if (entries[i].isDirectory()) {
		    dirEntries.add(entries[i]);
		}
	    }
	    entries = (File[])dirEntries.toArray(new File[dirEntries.size()]);
	}

	Arrays.sort(entries);
        _dirCache.put(parent, new CacheEntry(entries));
	return entries;
    }

    public Object getChild(Object parent, int index) {
	File entries[] = getEntries(parent);
	if (entries == null) {
	    return null;
	}
	return entries[index];
    }
    
    public int getChildCount(Object parent) {
	File entries[] = getEntries(parent);
	if (entries == null) return 0;
	return entries.length;
    }
    
    public boolean isLeaf(Object node) {
        /* this is a leaf, if this is a file or
         * if the directory does not have any content.
         */
        File f = (File) node;
        if (f.isFile()) return true;
        File entries[] = getEntries(f);
        return (entries == null || entries.length == 0);
    }
    
    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO: update cache.
    }

    public int getIndexOfChild(Object parent, Object child) {
	File entries[] = getEntries(parent);
	for (int i=0; i < entries.length; ++i) {
	    if (entries[i].equals(child)) {
		return i;
	    }
	}
	return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
    }
    public void removeTreeModelListener(TreeModelListener l) {
    }

    /**
     * the cache entry knows when it was entered so can decide
     * wheter it already expired, according to the CACHE_TIME
     */
    private final static class CacheEntry {
        public final long   _lastModified;
        public final File[] _entries;

        public CacheEntry(File[] entries) {
            _lastModified = System.currentTimeMillis();
            _entries = entries;
        }

        public File[] getEntries() {
            return _entries;
        }

        public boolean isExpired() {
            return (_lastModified + CACHE_TIME) < System.currentTimeMillis();
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
